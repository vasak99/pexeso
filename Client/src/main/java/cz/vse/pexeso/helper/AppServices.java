package cz.vse.pexeso.helper;

import cz.vse.pexeso.network.ClientConnection;
import cz.vse.pexeso.network.MessageHandler;

public class AppServices {
    private static ClientConnection connection;
    private static MessageHandler messageHandler;

    public static void initialize() {
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
