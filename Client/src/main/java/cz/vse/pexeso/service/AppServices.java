package cz.vse.pexeso.service;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.network.ClientConnection;
import cz.vse.pexeso.network.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton service that manages application-wide services.
 */
public class AppServices {
    private static final Logger log = LoggerFactory.getLogger(AppServices.class);

    private static AppServices instance;

    private ClientConnection connection;
    private MessageHandler messageHandler;
    private ClientSession clientSession;

    private AppServices() {
        log.info("Creating AppServices instance");
    }

    public static synchronized AppServices getInstance() {
        if (instance == null) {
            instance = new AppServices();
        }
        return instance;
    }

    public void initialize() {
        log.info("Initializing AppServices");
        connection = new ClientConnection(Variables.SERVER_ADDR, Variables.DEFAULT_PORT);
        messageHandler = new MessageHandler();
        connection.setMessageHandler(messageHandler);
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public ClientSession getClientSession() {
        return clientSession;
    }

    public void setClientSession(ClientSession session) {
        this.clientSession = session;
    }

    public void clear() {
        log.info("Clearing AppServices");

        if (connection != null) {
            connection.close();
        }
        connection = null;
        messageHandler = null;
        clientSession = null;
    }
}
