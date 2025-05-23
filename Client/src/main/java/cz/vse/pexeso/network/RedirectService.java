package cz.vse.pexeso.network;

import cz.vse.pexeso.model.service.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedirectService {
    private static final Logger log = LoggerFactory.getLogger(RedirectService.class);

    private final ConnectionService connectionService;

    public RedirectService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void redirect(String data) {
        String[] parts = data.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        ClientConnection oldConnection = connectionService.getConnection();

        log.info("Redirecting to {}:{}", host, port);

        ClientConnection newConnection = null;
        try {
            newConnection = new ClientConnection(host, port);

            newConnection.setMessageHandler(connectionService.getMessageHandler());

            connectionService.setConnection(newConnection);
            oldConnection.close();

            log.info("Redirect successful");
        } catch (Exception e) {
            log.error("Redirect failed", e);
            if (newConnection != null) {
                newConnection.close();
            }
        }
    }
}