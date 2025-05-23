package cz.vse.pexeso.model.service;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.network.ClientConnection;
import cz.vse.pexeso.network.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);

    private ClientConnection connection;
    private final MessageHandler messageHandler;

    public ConnectionService() {
        this.messageHandler = new MessageHandler();
        this.connection = new ClientConnection(Variables.SERVER_ADDR, Variables.DEFAULT_PORT);
        this.connection.setMessageHandler(messageHandler);
        log.info("ConnectionService initialized with server: {}:{}", Variables.SERVER_ADDR, Variables.DEFAULT_PORT);
    }

    public void send(String message) {
        connection.sendMessage(message);
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }
}
