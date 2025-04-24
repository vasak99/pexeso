package cz.vse.pexeso.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.StartGameData;
import cz.vse.pexeso.common.utils.MessageData;
import cz.vse.pexeso.exceptions.CardsException;
import cz.vse.pexeso.exceptions.ExceededGameCapacityException;
import cz.vse.pexeso.exceptions.PlayersException;
import cz.vse.pexeso.game.Game;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;

public class GameServerRuntime implements Observer {

    public static final Logger log = LoggerFactory.getLogger(GameServerRuntime.class);

    private ServerSocket serverSocket;
    private boolean keepAlive;

    private Set<Connection> connections = new HashSet<Connection>();
    private Map<String, Game> games = new HashMap<String, Game>();

    public GameServerRuntime(int port) {
        try {
            log.info("Attempting to start socket");
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("IOException occurred while starting the server: " + e);
            return;
        }

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

    public void terminate() {
        for (var conn : this.connections) {
            conn.terminate();
        }
    }

    @Override
    public void onNotify(Observable obs, Object o) {
        if(o instanceof Message) {
            Message msg = (Message) o;
            Connection conn = (Connection) obs;

            switch(msg.getType()) {
                case MessageType.CREATE_GAME:
                    this.createGame(conn, msg.getData());
                    break;
                default:
                    break;
            }
        }
        // this.connections.remove(obs);
    }

    private void createGame(Connection conn, String data) {
        if(this.games.size() >= Variables.MAX_GAMES) {
            Message msg = new Message();
            msg.setType(MessageType.ERROR);
            msg.setData("Server capacity exceeded");
            conn.sendMessage(msg.toSendable());
            return;
        }

        int port = 1;
        try {
            StartGameData sgd = MessageData.getCreateGameData(data);

            Game game = null;

            for(int i = 1; game == null; i++) {
                if(Variables.DEFAULT_PORT + i > 65_535) {
                    break;
                }
                try {
                    port = Variables.DEFAULT_PORT + i;
                    game = new Game(sgd.capacity, sgd.cardCount, port);
                } catch (IOException e) {}

            }

            this.games.put(game.getId(), game);

            try {
                Message msg = new Message();
                msg.setType(MessageType.REDIRECT);
                msg.setData(InetAddress.getLocalHost().getLocalHost() + ":" + port);
                conn.sendMessage(msg.toSendable());
            } catch (UnknownHostException e) {}
        } catch (DataFormatException e) {
            Message msg = new Message();
            msg.setType(MessageType.ERROR);
            msg.setData("Provided data has wrong format");
            conn.sendMessage(msg.toSendable());
        } catch (CardsException e) {
            Message msg = new Message();
            msg.setType(MessageType.ERROR);
            msg.setData(e.getMessage());
            conn.sendMessage(e.getMessage());
        } catch (PlayersException e) {
            Message msg = new Message();
            msg.setType(MessageType.ERROR);
            msg.setData(e.getMessage());
            conn.sendMessage(e.getMessage());
        }
    }


}

