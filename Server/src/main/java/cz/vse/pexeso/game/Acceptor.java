package cz.vse.pexeso.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.main.Connection;
import cz.vse.pexeso.utils.Observer;
import cz.vse.pexeso.utils.Rand;

public class Acceptor implements Runnable {
    public static final Logger log = LoggerFactory.getLogger(Acceptor.class);

    private ServerSocket serverSocket;
    private Map<String, Connection> output;
    private int capacity;
    private boolean keepAlive;
    private Observer obs;

    public Acceptor(ServerSocket serverSocket, Map<String, Connection> output, int capacity, Observer obs) {
        this.serverSocket = serverSocket;
        this.output = output;
        this.capacity = capacity;
        this.obs = obs;
    }

    public void run() {
        this.keepAlive = true;
        synchronized(output) {
            synchronized(this.serverSocket) {
                while(keepAlive) {
                    Socket socket;
                    try {
                        socket = this.serverSocket.accept();
                    } catch(IOException e) {
                        log.error("An error occurred when accepting a connection: " + e);
                        continue;
                    }

                    if(this.output.size() < this.capacity) {
                        Connection conn = new Connection(socket);
                        String playerId;
                        do {
                            playerId = "" + Rand.between(0, Integer.MAX_VALUE);
                        } while (this.output.containsKey(playerId));

                        synchronized(this.obs) {
                            conn.subscribe(this.obs);
                        }

                        this.output.put(playerId, conn);
                    }
                }
            }
        }
    }

    public void terminate() {
        this.keepAlive = false;
        try {
            synchronized(serverSocket) {
                serverSocket.close();
            }
        }
        catch (IOException e) {
            log.info("Server socket closed");
        }
    }

}
