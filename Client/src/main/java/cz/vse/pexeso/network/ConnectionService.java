package cz.vse.pexeso.network;

import cz.vse.pexeso.common.environment.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service that holds ClientConnection instance
 *
 * @author kott10
 * @version June 2025
 */
public class ConnectionService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);

    private ClientConnection connection;
    private final MessageHandler messageHandler;

    /**
     * Constructs a ConnectionService, initializes a ClientConnection to the configured server address and port,
     * and sets up the MessageHandler.
     */
    public ConnectionService() {
        this.messageHandler = new MessageHandler();
        this.connection = new ClientConnection(Variables.SERVER_ADDR, Variables.DEFAULT_PORT);
        this.connection.setMessageHandler(messageHandler);
        log.info("ConnectionService initialized with server: {}:{}", Variables.SERVER_ADDR, Variables.DEFAULT_PORT);
    }

    /**
     * Sends a message to the server.
     *
     * @param message the string to send
     */
    public void send(String message) {
        if (message == null) {
            log.warn("Attempted to send a null message");
            return;
        }
        if (connection == null || connection.isClosed()) {
            log.warn("Cannot send message; connection is null or closed");
            return;
        }
        connection.sendMessage(message);
    }

    /**
     * Returns the MessageHandler.
     *
     * @return MessageHandler instance
     */
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * Returns the current ClientConnection.
     *
     * @return the ClientConnection
     */
    public ClientConnection getConnection() {
        return connection;
    }

    /**
     * Sets a new ClientConnection.
     *
     * @param connection the new ClientConnection to use
     */
    public void setConnection(ClientConnection connection) {
        log.debug("Setting new connection");
        this.connection = connection;
    }
}
