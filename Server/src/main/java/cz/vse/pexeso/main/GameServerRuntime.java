package cz.vse.pexeso.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;

public class GameServerRuntime implements Observer {

    public static final Logger log = LoggerFactory.getLogger(GameServerRuntime.class);

    private ServerSocket serverSocket;
    private boolean keepAlive;

    private Set<Connection> connections = new HashSet<Connection>();

    public GameServerRuntime(int port) {
        try {
            log.info("Attempting to start socket");
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("IOException occurred while starting the server: " + e);
            return;
        }

        keepAlive = true;

        while(keepAlive) {
            log.info("Listening for connections");
            try {
                var socket = this.serverSocket.accept();
                log.info("New connection accepted");

                var connection = new Connection(socket);

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
    public void onNotify(Observable obs) {
        this.connections.remove(obs);
    }


}

