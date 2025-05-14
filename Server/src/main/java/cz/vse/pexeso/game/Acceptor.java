package cz.vse.pexeso.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.database.DatabaseController;
import cz.vse.pexeso.main.Connection;
import cz.vse.pexeso.main.MessageFactory;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;

public class Acceptor implements Runnable, Observer {
    public static final Logger log = LoggerFactory.getLogger(Acceptor.class);

    private String gameId;
    private ServerSocket serverSocket;
    private Map<String, Player> players;
    private Set<Connection> pendingConnections;
    private int capacity;
    private boolean keepAlive;
    private Observer obs;
    private DatabaseController dc;

    public Acceptor(ServerSocket serverSocket, Map<String, Player> players, int capacity, DatabaseController dc, Observer obs) {
        this.serverSocket = serverSocket;
        this.players = players;
        this.capacity = capacity;
        this.obs = obs;
        this.pendingConnections = new HashSet<>();
        this.dc = dc;
    }

    public void run() {
        this.keepAlive = true;
        synchronized(players) {
            synchronized(this.serverSocket) {
                while(keepAlive) {
                    Socket socket;
                    try {
                        socket = this.serverSocket.accept();
                    } catch(IOException e) {
                        log.error("An error occurred when accepting a connection: " + e);
                        continue;
                    }

                    Connection conn = new Connection(socket);

                    this.pendingConnections.add(conn);
                    conn.subscribe(this);

                    conn.sendMessage(MessageFactory.getIdentityRequest().toSendable());
                }
            }
        }
    }

    public void terminate() {
        this.keepAlive = false;
        for(var conn : this.pendingConnections) {
            conn.sendMessage(MessageFactory.getError("Game capacity full").toSendable());
            conn.unsubscribe(this);
        }
        // try {
        //     synchronized(serverSocket) {
        //         serverSocket.close();
        //     }
        // }
        // catch (IOException e) {
        //     log.info("Server socket closed");
        // }
    }

    public void onNotify(Observable obs, Object o) {
        if(obs instanceof Connection && o instanceof Message) {
            Message msg = (Message) o;
            Connection conn = (Connection) obs;

            if(keepAlive) {
                switch (msg.getType()) {
                    case MessageType.IDENTITY:
                        this.processIdentity(conn, msg.getData());
                        break;
                    default:
                        conn.sendMessage(MessageFactory.getError("Unexpected message type").toSendable());
                        break;
                }
            } else {
                conn.sendMessage(MessageFactory.getError("Game already started").toSendable());
            }
        }
    }

    private void processIdentity(Connection conn, String playerId) {
        Player player = new Player(playerId, conn, this.dc);
        synchronized(players) {
            if(this.players.size() < this.capacity) {
                this.players.put(playerId, player);
                this.pendingConnections.remove(conn);
            }
        }
    }

}
