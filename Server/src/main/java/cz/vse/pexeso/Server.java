package cz.vse.pexeso;

import cz.vse.pexeso.main.GameServerRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    public static final Logger log = LoggerFactory.getLogger(Server.class);

    static final int DEFAULT_PORT = 8080;
    static GameServerRuntime game;

    public static void main(String[] args) {
        log.info("Starting server on port: " + DEFAULT_PORT);
        game = new GameServerRuntime(DEFAULT_PORT);

        attachGracefulShutdown();
    }

    public static void attachGracefulShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                synchronized (game) {
                    game.terminate();
                }
            }
        });
    }
}
