package cz.vse.pexeso.helper;

import cz.vse.pexeso.network.ClientConnection;
import cz.vse.pexeso.network.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to the ClientConnection and MessageHandler instances.
 */
public class AppServices {
    public static final Logger log = LoggerFactory.getLogger(AppServices.class);
    private static ClientConnection connection;
    private static MessageHandler messageHandler;

    public static void initialize() {
        log.info("Initializing AppServices");
        connection = new ClientConnection();
        messageHandler = new MessageHandler();
    }

    public static ClientConnection getConnection() {
        return connection;
    }

    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
