package cz.vse.pexeso.network;

import cz.vse.pexeso.model.RedirectParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles redirect messages from the server instructing the client to connect to a new host and port.
 * Parses redirect data strings of the form “host:port” and attempts to establish a new ClientConnection.
 *
 * @author kott10
 * @version June 2025
 */
public class RedirectService {
    private static final Logger log = LoggerFactory.getLogger(RedirectService.class);

    private final ConnectionService connectionService;

    /**
     * Constructs a RedirectService with the given ConnectionService.
     *
     * @param connectionService the ConnectionService to update
     * @throws NullPointerException if connectionService is null
     */
    public RedirectService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Redirects the connection based on server instructions.
     *
     * @param parameters redirect parameters
     */
    public void redirect(RedirectParameters parameters) {
        // 1) Grab and close the old connection (A) first:
        ClientConnection oldConnection = connectionService.getConnection();
        if (oldConnection != null) {
            log.info("Closing old connection before redirect");
            oldConnection.close();
        }

        // 2) Now create the brand‐new connection (B):
        log.info("Connecting to new port {}:{}", parameters.host(), parameters.port());
        try {
            ClientConnection newConnection = new ClientConnection(parameters.host(), parameters.port());
            // Attach the existing MessageHandler to the new socket so it can keep receiving messages:
            newConnection.setMessageHandler(connectionService.getMessageHandler());

            // 3) Only *after* B is built and has its handler do we update ConnectionService:
            connectionService.setConnection(newConnection);
            log.info("Redirect successful to {}:{}", parameters.host(), parameters.port());
        } catch (Exception e) {
            log.error("Redirect failed", e);
        }
    }

    /**
     * Parses a redirectData string in the format "host:port". Returns a RedirectParameters record
     * or null if parsing fails.
     *
     * @param redirectData the raw redirect string
     * @return a RedirectParameters instance, or null if parsing fails
     */
    public static RedirectParameters parseRedirectData(String redirectData) {
        String[] parts = redirectData.split(":");
        if (parts.length != 2) {
            log.error("Invalid redirect data: {}", redirectData);
            return null;
        }

        String host = parts[0].trim();
        if (host.isEmpty()) {
            log.error("Empty host in redirect data: {}", redirectData);
            return null;
        }

        int port;
        try {
            port = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException nfe) {
            log.error("Invalid port number in redirect data: {}", redirectData, nfe);
            return null;
        }
        return new RedirectParameters(host, port);
    }
}