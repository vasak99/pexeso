package cz.vse.pexeso.network;

import cz.vse.pexeso.service.AppServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Redirect {
    private static final Logger log = LoggerFactory.getLogger(Redirect.class);
    private static final AppServices appServices = AppServices.getInstance();

    public static void redirect(String data) {
        String[] parts = data.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        ClientConnection oldConnection = appServices.getConnection();

        log.info("Redirecting to {}:{}", host, port);

        ClientConnection newConnection = null;
        try {
            newConnection = new ClientConnection(host, port);

            newConnection.setMessageHandler(appServices.getMessageHandler());

            oldConnection.close();

            appServices.setConnection(newConnection);

            log.info("Redirect successful");
        } catch (Exception e) {
            log.error("Redirect failed", e);
            if (newConnection != null) {
                newConnection.close();
            }
        }
    }
}