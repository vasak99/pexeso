package cz.vse.pexeso.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.exceptions.PlayersException;
import cz.vse.pexeso.main.Connection;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;
import cz.vse.pexeso.utils.Utils;
import cz.vse.pexeso.main.MessageFactory;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.EditGamePayload;
import cz.vse.pexeso.common.message.payload.KickPlayerPayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.database.DatabaseController;

public class Game implements Observer {

    public static final Logger log = LoggerFactory.getLogger(Game.class);

    private GameBoard gameBoard;
    private ServerSocket serverSocket;
    private DatabaseController dc;

    private boolean isStarted = false;
    private String gameId;

    private int playersCapacity;
    private int cardCount;
    private int port;
    private String creatorId;
    private Map<String, Player> players;

    private Acceptor acceptor;

    public Game(String creatorId, int playersCapacity, int cardCount, int port, DatabaseController dc) throws PlayersException, IOException {
        if(playersCapacity < Variables.MIN_PLAYERS) {
            throw new PlayersException("Minimum number of players is " + Variables.MIN_PLAYERS);
        }
        if(playersCapacity > Variables.MAX_PLAYERS) {
            throw new PlayersException("Maximum number of players is " + Variables.MAX_PLAYERS);
        }

        this.creatorId = creatorId;
        this.players = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.playersCapacity = playersCapacity;
        this.cardCount = cardCount;
        this.port = port;
        this.dc = dc;
    }

    public void startSession() {
        this.acceptor = new Acceptor(this.serverSocket, this.players, this.playersCapacity, this.dc, this);
        new Thread(this.acceptor).start();
    }

    public int getPort() {
        return this.port;
    }

    public boolean isFull() {
        return this.players.size() >= this.playersCapacity;
    }

    public void startGame() {
        this.acceptor.terminate();
        this.isStarted = true;

        try {
            this.gameBoard = new GameBoard(this.cardCount);
        } catch (Exception e) {
            sendToAll(MessageFactory.getError(e.getMessage()));
        }

        String data = this.gameBoard.getAsData();
        this.sendToAll(MessageFactory.getGameStartMessage(data));
    }

    public void terminate() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            log.error("An error occurred while terminating game: " + e.getMessage());
        }
    }

    private void sendTo(String player, Message message) {
        var pl = this.players.get(player);
        if(pl != null) {
            pl.getConnection().sendMessage(message.toSendable());
        }
    }

    private void sendTo(Connection conn, Message message) {
        if(conn != null) {
            conn.sendMessage(message.toSendable());
        }
    }

    public String getId() {
        return this.gameId;
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
                    this.revealCard(1,1);
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
                default:
                    break;

            }
        }
    }

    public void revealCard(int row, int column) {
        Card card = this.gameBoard.revealCard(row, column);

    }

    private void sendToAll(Message msg) {
        for(var pl : this.players.entrySet()) {
            pl.getValue().getConnection().sendMessage(msg.toSendable());
        }
    }

    public void editGame(Connection conn, Message msg) {
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

        this.playersCapacity = data.capacity;
        this.cardCount = data.cardCount;

        sendToAll(MessageFactory.getLobbyMessage(getLobbyData()));
    }

    public void deleteGame(Connection conn, Message msg) {
        if(!checkGamePlayer(conn, msg) || !this.checkCreator(conn, msg)) {
            return;
        }

        sendToAll(MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));

        this.terminate();
    }

    public void leaveGame(Connection conn, Message msg) {
        if(!checkGamePlayer(conn, msg)) {
            return;
        }

        sendTo(conn, MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));

        this.players.remove(msg.getPlayerId(), conn);
        conn.terminate();

        if(this.players.size() < 1) {
            this.terminate();
        } else {
            sendToAll(MessageFactory.getLobbyMessage(getLobbyData()));
        }
    }

    public void playerReady(Connection conn, Message msg) {
        String gameId = msg.getGameId();
        if(gameId ==  null || gameId.equals(this.gameId)) {
            sendTo(conn, MessageFactory.getError("Wrong game id"));
            return;
        }

        String playerId = msg.getPlayerId();

        this.players.get(playerId).setReady();

        sendToAll(MessageFactory.getLobbyMessage(this.getLobbyData()));
    }

    public void kickPlayer(Connection conn, Message msg) {
        if(!(checkGamePlayer(conn, msg) && checkCreator(conn, msg))) {
            return;
        }

        KickPlayerPayload data = new KickPlayerPayload(msg);
        if(this.creatorId.equals(data.playerId)) {
            sendTo(conn, MessageFactory.getError("Cannot kick creator"));
            return;
        }

        var kickedPlayer = this.players.remove(data.playerId);
        sendTo(kickedPlayer.getConnection(), MessageFactory.getRedirectMessage(Utils.getLocalAddress(), Variables.DEFAULT_PORT));

        LobbyUpdatePayload ret = this.getLobbyData();
        sendToAll(MessageFactory.getLobbyMessage(ret));
    }

    private boolean checkGameId(Connection conn, Message msg) {
        if(!this.gameId.equals(msg.getGameId())) {
            sendTo(conn, MessageFactory.getError("Wrong game ID"));
            return false;
        }
        return true;
    }

    private boolean checkPlayer(Connection conn, Message msg) {
        Set<String> players = this.players.keySet();
        for(var player : players) {
            if(player.equals(msg.getPlayerId())) {
                return true;
            }
        }

        sendTo(conn, MessageFactory.getError("You are not a member of this session"));
        return false;
    }

    private boolean checkCreator(Connection conn, Message msg) {
        if(!this.creatorId.equals(msg.getPlayerId())) {
            sendTo(conn, MessageFactory.getError("You do not have elevated permissions on this session"));
            return false;
        }
        return true;
    }

    private boolean checkGamePlayer(Connection conn, Message msg) {
        return checkGameId(conn, msg) && checkPlayer(conn, msg);
    }

    public LobbyUpdatePayload getLobbyData() {
        var ret = new LobbyUpdatePayload();

        var playersList = new ArrayList<SendablePlayer>();
        for(var pl : this.players.entrySet()) {
            Player pp = pl.getValue();
            playersList.add(new SendablePlayer(pp.getName(), pp.isReady()));
        }

        ret.gameBoard = this.gameBoard.getAsData();
        ret.players = playersList;
        ret.cardCount = this.cardCount;
        ret.playersCapacity = this.playersCapacity;

        return ret;
    }

}
