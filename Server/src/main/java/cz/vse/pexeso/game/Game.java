package cz.vse.pexeso.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.exceptions.PlayersException;
import cz.vse.pexeso.main.Connection;
import cz.vse.pexeso.main.GameServerRuntime;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;
import cz.vse.pexeso.utils.Utils;
import cz.vse.pexeso.main.MessageFactory;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.EditGamePayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.KickPlayerPayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.common.message.payload.ResultPayload;
import cz.vse.pexeso.common.message.payload.RevealCardPayload;
import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.database.DatabaseController;

/**
 * Object representation of a game session
 */
public class Game implements Observer {

    public static final Logger log = LoggerFactory.getLogger(Game.class);

    private GameBoard gameBoard;
    private ServerSocket serverSocket;
    private DatabaseController dc;
    private GameServerRuntime gsr;

    private boolean isStarted = false;
    private String gameId;
    private String gameName;

    private int playersCapacity;
    private int cardCount;
    private int port;
    private String creatorId;
    private Map<Long, Player> players;
    private List<Long> playersOrder;
    private int activePlayerIndex;

    private Card lastCard;

    private Acceptor acceptor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Game(String name, long creatorId, int playersCapacity, int cardCount, int port, DatabaseController dc, GameServerRuntime gsr) throws PlayersException, IOException {
        if(playersCapacity < Variables.MIN_PLAYERS) {
            throw new PlayersException("Minimum number of players is " + Variables.MIN_PLAYERS);
        }
        if(playersCapacity > Variables.MAX_PLAYERS) {
            throw new PlayersException("Maximum number of players is " + Variables.MAX_PLAYERS);
        }

        this.startTime = LocalDateTime.now();

        this.gameName = name;
        this.creatorId = "" + creatorId;
        this.players = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.playersCapacity = playersCapacity;
        this.cardCount = cardCount;
        this.port = port;
        this.dc = dc;

        this.gsr = gsr;
    }

    /**
     * Sets the game ID recieved from outside
     * @param id Game ID
     */
    public void setGameId(String id) {
        this.gameId = id;
    }

    /**
     * Starts recieving connections from client
     */
    public void startSession() {
        this.acceptor = new Acceptor(this.serverSocket, this.players, this.dc, this, this);
        new Thread(this.acceptor).start();
    }

    /**
     * Returns the port on which the game runs
     * @return int
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Is game capacity full
     * @return boolean
     */
    public boolean isFull() {
        return this.players.size() >= this.playersCapacity;
    }

    /**
     * Closes the connection acceptor & starts game
     */
    public void startGame() {
        this.acceptor.terminate();
        this.isStarted = true;

        try {
            this.gameBoard = new GameBoard(this.cardCount);
        } catch (Exception e) {
            sendToAll(MessageFactory.getError(e.getMessage()));
        }

        this.playersOrder = new ArrayList<>();
        this.playersOrder.addAll(this.players.keySet());
        Collections.shuffle(this.playersOrder);

        this.activePlayerIndex = 0;

        this.sendToAll(MessageFactory.getGameStartMessage(this.getGameUpdateData().toSendable()));
    }

    /**
     * Moves the turn to next player in order
     */
    public void moveTurn() {
        if(this.activePlayerIndex == this.playersOrder.size() - 1) {
            this.activePlayerIndex = 0;
        } else {
            this.activePlayerIndex += 1;
        }
    }

    /**
     * Ends game & closes the socket
     */
    public void terminate() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            log.error("An error occurred while terminating game: " + e.getMessage());
        }
    }

    /**
     * Sends a message to single player
     * @param player Player ID
     * @param message Message to be sent
     */
    private void sendTo(long player, Message message) {
        var pl = this.players.get(player);
        if(pl != null) {
            pl.getConnection().sendMessage(message.toSendable());
        }
    }

    /**
     * Sends a message to single player
     * @param conn Player connection
     * @param message Message to be sent
     */
    private void sendTo(Connection conn, Message message) {
        if(conn != null) {
            conn.sendMessage(message.toSendable());
        }
    }

    /**
     * Returns the game ID
     * @return String
     */
    public String getId() {
        return this.gameId;
    }

    /**
     * Returns game name
     * @return String
     */
    public String getName() {
        return this.gameName;
    }

    /**
     * Has game already started
     * @return boolean
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Returns the max amount of players
     * @return int
     */
    public int getPlayersCapacity() {
        return playersCapacity;
    }

    /**
     * Returns nomber of cards
     * @return int
     */
    public int getCardCount() {
        return cardCount;
    }

    /**
     * Returns the ID of game creator
     * @return String
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * Returns the name of game creator
     * @return String
     */
    public String getCreatorName() {
        return dc.getUserById(Long.parseLong(creatorId)).name;
    }

    @Override
    public void onNotify(Observable obj, Object o) {
        if(obj instanceof Connection && o instanceof Message) {
            Message msg = (Message) o;
            Connection conn = (Connection) obj;

            switch(msg.getType()) {
                case MessageType.START_GAME:
                    this.startGame();
                    break;
                case MessageType.REVEAL:
                    this.revealCard(conn, msg);
                    break;
                case MessageType.EDIT_GAME:
                    this.editGame(conn, msg);
                    break;
                case MessageType.DELETE_GAME:
                    this.deleteGame(conn, msg);
                    break;
                case MessageType.LEAVE_GAME:
                    this.leaveGame(conn, msg);
                    break;
                case MessageType.PLAYER_READY:
                    this.playerReady(conn, msg);
                    break;
                case MessageType.KICK_PLAYER:
                    this.kickPlayer(conn, msg);
                    break;
                case MessageType.GIVE_UP:
                    this.giveUp(conn, msg);
                    break;
                default:
                    break;

            }
        }
    }

    /**
     * Turns the card on game board
     * @param conn Player connection for sending response messages
     * @param msg Recieved message
     */
    public void revealCard(Connection conn, Message msg) {
        RevealCardPayload data = new RevealCardPayload(msg.getData());
        long playerId = msg.getPlayerId();
        Card revealed = this.gameBoard.revealCard(data.row, data.column);

        if(!this.checkGamePlayer(conn, msg)) {
            return;
        }

        if(revealed == null) {
            sendTo(conn, MessageFactory.getInvalidMoveMessage("Selected field is empty"));
            return;
        }

        if(this.lastCard == null) {
            this.lastCard = revealed;
        } else {
            sendToAll(MessageFactory.getGameUpdateMessage(this.getGameUpdateData()));
            if(this.lastCard.getId() == revealed.getId()) {
                this.players.get(playerId).addPoint();
                this.gameBoard.removePair(revealed.getId());
            } else {
                this.moveTurn();
            }

            synchronized (this) {
            try {
                this.wait(2000);
            } catch (InterruptedException e) {}
            }
            this.lastCard = null;
            this.gameBoard.hideAll();
        }

        if(this.gameBoard.allRevealed()) {
            sendToAll(MessageFactory.getGameUpdateMessage(this.getGameUpdateData()));
            sendToAll(MessageFactory.getResultMessage(this.getResult()));
            sendToAll(MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));
            this.endTime = LocalDateTime.now();
            this.dc.saveGameResult(this.gameId, this.gameName, this.startTime, this.endTime, this.getPlayersList());
            gsr.getAllGames().remove(this.gameId);
            this.terminate();
        } else {
            sendToAll(MessageFactory.getGameUpdateMessage(this.getGameUpdateData()));
        }


    }

    /**
     * Returns the list of players
     * @return List<Player>
     */
    public List<Player> getPlayersList() {
        List<Player> players = new ArrayList<>();
        for(var pl : this.players.entrySet()) {
            players.add(pl.getValue());
        }
        return players;
    }

    private void giveUp(Connection conn, Message msg) {
        if(!checkGamePlayer(conn, msg)) {
            return;
        }

        sendTo(conn, MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));
        conn.terminate();

        this.players.remove(msg.getPlayerId());

        //if there is only one player left, end the game
        if (this.players.size() < 2) {
            sendToAll(MessageFactory.getResultMessage(this.getResult()));
            sendToAll(MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));
            gsr.getAllGames().remove(this.gameId);
            this.terminate();
        } else {
            //if active
            if (playersOrder.get(activePlayerIndex) == msg.getPlayerId()) {
                // if last in order, move back to start
                if (activePlayerIndex == playersOrder.size()-1) {
                    moveTurn();
                }
                //reset reveal
                this.lastCard = null;
                this.gameBoard.hideAll();
            //if in front of active player, move one back
            } else if (activePlayerIndex > playersOrder.indexOf(msg.getPlayerId())) {
                activePlayerIndex--;
            }
            playersOrder.remove(msg.getPlayerId());
            sendToAll(MessageFactory.getGameUpdateMessage(this.getGameUpdateData()));
        }
    }

    /**
     * Sends a message to all connected players
     * @param msg Message to be sent
     */
    public void sendToAll(Message msg) {
        for(var pl : this.players.entrySet()) {
            pl.getValue().getConnection().sendMessage(msg.toSendable());
        }
    }

    /**
     * Changes the game settings - must be made by game creator
     * @param conn Player connection for sending responses
     * @param msg Received message with new config
     */
    public void editGame(Connection conn, Message msg) {
        if(this.isStarted) {
            sendTo(conn, MessageFactory.getError("Cannot modify game after it has started"));
            return;
        }
        EditGamePayload data = null;
        try {
            data = new EditGamePayload(msg.getData());
        } catch (DataFormatException e) {
            sendTo(conn, MessageFactory.getError(e.getMessage()));
            return;
        }

        if(!this.checkGamePlayer(conn, msg) || !this.checkCreator(conn, msg)) {
            return;
        }

        if(!checkPlayersCapacity(conn, data.capacity) || !checkCardCount(conn, data.cardCount)) {
            return;
        }

        this.gameName = data.name;
        this.playersCapacity = data.capacity;
        this.cardCount = data.cardCount;

        sendToAll(MessageFactory.getLobbyMessage(getLobbyData()));
    }

    /**
     * Deletes the game object - requires creator
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    public void deleteGame(Connection conn, Message msg) {
        if(!checkGamePlayer(conn, msg) || !this.checkCreator(conn, msg)) {
            return;
        }

        gsr.getAllGames().remove(msg.getGameId());
        sendToAll(MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));

        this.acceptor.terminate();
        this.terminate();
    }

    /**
     * Removes player from game & redirects him to main server port
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    public void leaveGame(Connection conn, Message msg) {
        if(!checkGamePlayer(conn, msg)) {
            return;
        }

        sendTo(conn, MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));

        this.players.remove(msg.getPlayerId());
        conn.terminate();

        if(this.creatorId.equals("" + msg.getPlayerId())) {
            sendToAll(MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));
            for(var con : this.players.entrySet()) {
                con.getValue().getConnection().terminate();
            }
            gsr.getAllGames().remove(this.gameId);
            this.terminate();
            return;
        }

        if(this.players.isEmpty()) {
            this.terminate();
        } else {
            sendToAll(MessageFactory.getLobbyMessage(getLobbyData()));
        }
    }

    /**
     * Sets player status to ready
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    public void playerReady(Connection conn, Message msg) {
        String gameId = msg.getGameId();
        if(gameId ==  null || !gameId.equals(this.gameId)) {
            sendTo(conn, MessageFactory.getError("Wrong game id"));
            return;
        }

        long playerId = msg.getPlayerId();

        this.players.get(playerId).setReady();

        sendToAll(MessageFactory.getLobbyMessage(this.getLobbyData()));
    }

    /**
     * Forcibly removes a player from game & redirects him to main port - requires creator
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    public void kickPlayer(Connection conn, Message msg) {
        if(!(checkGamePlayer(conn, msg) && checkCreator(conn, msg))) {
            return;
        }

        KickPlayerPayload data = new KickPlayerPayload(msg);
        if(this.creatorId.equals("" + data.playerId)) {
            sendTo(conn, MessageFactory.getError("Cannot kick creator"));
            return;
        }

        var kickedPlayer = this.players.remove(data.playerId);
        sendTo(kickedPlayer.getConnection(), MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));

        LobbyUpdatePayload ret = this.getLobbyData();
        sendToAll(MessageFactory.getLobbyMessage(ret));
    }

    /**
     * Checks whether player refers to the correct game
     * @param conn Player connection to send messages
     * @param msg Received message with data
     * @return boolean
     */
    private boolean checkGameId(Connection conn, Message msg) {
        if(!this.gameId.equals(msg.getGameId())) {
            sendTo(conn, MessageFactory.getError("Wrong game ID"));
            return false;
        }
        return true;
    }

    /**
     * Checks whether player is a member of the game
     * @param conn Player connection for sending messages
     * @param msg Received message with data
     * @return boolean
     */
    private boolean checkPlayer(Connection conn, Message msg) {
        Set<Long> players = this.players.keySet();
        for(var player : players) {
            if(player.equals(msg.getPlayerId())) {
                return true;
            }
        }

        sendTo(conn, MessageFactory.getError("You are not a member of this session"));
        return false;
    }

    /**
     * Checks whether the player is creator of game & thus has elevated privileges
     * @param conn Player connection for sending messages
     * @param msg Received message with data
     * @return boolean
     */
    private boolean checkCreator(Connection conn, Message msg) {
        if(!this.creatorId.equals("" + msg.getPlayerId())) {
            sendTo(conn, MessageFactory.getError("You do not have elevated permissions on this session"));
            return false;
        }
        return true;
    }

    /**
     * Checks whether player is in game & refers to the correct game
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     * @return boolean
     */
    private boolean checkGamePlayer(Connection conn, Message msg) {
        return checkGameId(conn, msg) && checkPlayer(conn, msg);
    }

    /**
     * Returns current game data for update - before game starts
     * @return LobbyUpdatePayload
     */
    public LobbyUpdatePayload getLobbyData() {
        var ret = new LobbyUpdatePayload();

        var playersList = new ArrayList<SendablePlayer>();
        for(var pl : this.players.entrySet()) {
            Player pp = pl.getValue();
            playersList.add(new SendablePlayer(pp.getPlayerId(), pp.getName(), pp.isReady(), pp.getScore()));
        }

        ret.players = playersList;
        ret.name = this.gameName;
        ret.cardCount = this.cardCount;
        ret.playersCapacity = this.playersCapacity;

        return ret;
    }

    /**
     * Returns current game data for update - after game starts
     * @return GameUpdatePayload
     */
    public GameUpdatePayload getGameUpdateData() {
        var players = new ArrayList<SendablePlayer>();

        for(var pl : this.players.entrySet()) {
            var pp = pl.getValue();
            players.add(new SendablePlayer(pp.getPlayerId(), pp.getName(), pp.isReady(), pp.getScore()));
        }

        return new GameUpdatePayload(
            this.gameBoard.getAsData(),
            players,
            this.playersOrder.get(this.activePlayerIndex),
            this.gameId
        );
    }

    /**
     * Returns game result as sendable data
     * @return ResultPayload
     */
    public ResultPayload getResult() {
        var players = new ArrayList<>(this.players.values());
        players.sort((Player p1, Player p2) -> p1.getScore() - p2.getScore());

        var sendable = new SendablePlayer[players.size()];
        for(int i = 0; i < players.size(); i++) {
            sendable[i] = Utils.toSendable(players.get(i));
        }
        SendablePlayer winner = sendable[0];

        return new ResultPayload(sendable, winner);
    }

    /**
     * Checks whether new player capacity is within acceptable bounds
     * @param conn Player connection for sending responses
     * @param capacity New capacity to be set
     * @return boolean
     */
    private boolean checkPlayersCapacity(Connection conn, int capacity) {
        if(capacity != this.playersCapacity && capacity < this.players.size()) {
            sendTo(conn, MessageFactory.getError("Game size too small"));
            return false;
        }

        if(capacity > Variables.MAX_PLAYERS || capacity < Variables.MIN_PLAYERS) {
            sendTo(conn, MessageFactory.getError("Game size out of bounds"));
            return false;
        }
        return true;

    }

    /**
     * Checks whether new card count is within acceptable bounds
     * @param conn Player connection for sending responses
     * @param count New card count to be set
     * @return boolean
     */
    private boolean checkCardCount(Connection conn, int count) {
        if(count < Variables.MIN_CARDS || count > Variables.MAX_CARDS) {
            sendTo(conn, MessageFactory.getError("Cards count out of bounds"));
            return false;
        }
        return true;
    }

}
