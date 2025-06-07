package cz.vse.pexeso;

import cz.vse.pexeso.main.GameServerRuntime;
import cz.vse.pexeso.common.environment.Variables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point of the server application
 */
public final class Server {

    public static final Logger log = LoggerFactory.getLogger(Server.class);

    static final int DEFAULT_PORT = Variables.DEFAULT_PORT;
    static GameServerRuntime game;

    private Server() {}

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        log.info("Starting server on port: " + DEFAULT_PORT);
        game = new GameServerRuntime(DEFAULT_PORT);

        attachGracefulShutdown();
    }

    /**
     * Graceful shutdown - ensures all is terminated properly
     */
    public static void attachGracefulShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                synchronized (game) {
                    game.terminate();
                }
            }
        });
    }

}
