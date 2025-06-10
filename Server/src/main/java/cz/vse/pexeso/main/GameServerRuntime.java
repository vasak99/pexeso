package cz.vse.pexeso.main;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.CreateGamePayload;
import cz.vse.pexeso.database.DatabaseController;
import cz.vse.pexeso.exceptions.PlayersException;
import cz.vse.pexeso.game.Game;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;
import cz.vse.pexeso.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import cz.vse.pexeso.main.http.ImageServer;

/**
 * Main logic object - controls the application functionalities
 */
public class GameServerRuntime implements Observer {

    public static final Logger log = LoggerFactory.getLogger(GameServerRuntime.class);

    private ServerSocket serverSocket;
    private ImageServer imageServer;
    private boolean keepAlive;

    private Set<Connection> connections = new HashSet<Connection>();
    private Map<String, Game> games = new HashMap<String, Game>();

    private DatabaseController dc;
    private MessageController messageController;
    private GameLobbyUpdater glu;

    public GameServerRuntime(int port) {
        try {
            log.info("Attempting to start socket");
            this.serverSocket = new ServerSocket(port);

            this.dc = new DatabaseController();
            this.messageController = new MessageController(this, this.dc);

            log.info("Starting image server");
            this.imageServer = new ImageServer();
            new Thread(this.imageServer).start();
            log.info("Image server started");
        } catch (IOException e) {
            log.error("IOException occurred while starting the server: " + e.getMessage());
            return;
        } catch(SQLException e) {
            log.error("An SQLException occurred while trying to connect to database: " + e.getMessage());
            return;
        }

        this.dc.getAllPlayerGames(1);

        this.glu = new GameLobbyUpdater(this);
        new Thread(this.glu).start();

        keepAlive = true;

        log.info("Listening for connections");
        while(keepAlive) {
            try {
                var socket = this.serverSocket.accept();
                log.info("New connection accepted");

                var connection = new Connection(socket);
                connection.subscribe(this);

                synchronized(connections) {
                    connections.add(connection);
                }

                new Thread(connection).start();

            } catch(IOException e) {
                log.error("IOException occurred while creating connection: " + e);
            }
        }

    }

    /**
     * Returns all currently active games
     * @return Map<String, Game>
     */
    public Map<String, Game> getAllGames() {
        return this.games;
    }

    /**
     * Terminates server
     */
    public void terminate() {
        for (var conn : this.connections) {
            conn.terminate();
        }

        this.imageServer.terminate();
        this.glu.terminate();
    }

    @Override
    public void onNotify(Observable obs, Object o) {
        if(obs instanceof Connection && o instanceof Message) {
            Message msg = (Message) o;
            Connection conn = (Connection) obs;

            this.messageController.handleMessage(conn, msg);
        } else {
            log.error("Unable to read message");
        }
    }

    /**
     * Creates new game object & generates a unique ID
     */
    public void createGame(Connection conn, Message inmsg) {
        String data = inmsg.getData();
        if(this.games.size() >= Variables.MAX_GAMES) {
            Message msg = new Message();
            msg.setType(MessageType.ERROR);
            msg.setData("Server capacity exceeded");
            conn.sendMessage(msg.toSendable());
            return;
        }

        for (Game game : this.games.values()) {
            if (inmsg.getPlayerId() == Long.parseLong(game.getCreatorId())) {
                conn.sendMessage(MessageFactory.getError("You already have game").toSendable());
                return;
            }
        }

        int port = 1;
        try {
            CreateGamePayload cgp = new CreateGamePayload(data);

            Game game = null;

            for(int i = 1; game == null; i++) {
                if(Variables.DEFAULT_PORT + i > 65_535) {
                    break;
                }
                try {
                    port = Variables.DEFAULT_PORT + i;
                    game = new Game(cgp.gameName, inmsg.getPlayerId(), cgp.capacity, cgp.cardCount, port, this.dc, this);
                } catch (IOException e) {}
            }

            String gameId = "";
            do {
                gameId = "" + Math.floor(Math.random() * Long.MAX_VALUE);
            } while(this.games.keySet().contains(gameId));
            game.setGameId(gameId);
            this.games.put(gameId, game);

            game.startSession();
            conn.sendMessage(MessageFactory.getRedirectMessage(Utils.getLocalAddress(), port).toSendable());

            this.connections.remove(conn);
            conn.terminate();
        } catch (DataFormatException e) {
            conn.sendMessage(MessageFactory.getError("Provided data has wrong format").toSendable());
        } catch (PlayersException e) {
            conn.sendMessage(MessageFactory.getError(e.getMessage()).toSendable());
        }
    }

    /**
     * Returns game object from ID
     * @param gameId Game ID
     * @return Game
     */
    public Game getGameById(String gameId) {
        return this.games.get(gameId);
    }

    /**
     * Sends a message to all connected users
     * @param msg Message to be sent
     */
    public void sendToAll(Message msg) {
        for(var conn : this.connections) {
            conn.sendMessage(msg.toSendable());
        }
    }

}

