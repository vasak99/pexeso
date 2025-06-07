package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.network.ConnectionService;
import cz.vse.pexeso.util.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for sending authentication-related requests (login, register) to the server.
 *
 * @author kott10
 * @version June 2025
 */
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final ConnectionService connectionService;

    /**
     * Constructs an AuthService using the provided ConnectionService.
     *
     * @param connectionService ConnectionService to send messages
     */
    public AuthService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Sends a login request to the server with the given user credentials.
     *
     * @param userCredentials UserCredentials containing username and password
     */
    public void sendLoginRequest(UserCredentials userCredentials) {
        connectionService.send(MessageBuilder.buildLoginMessage(userCredentials));
        log.debug("Sent LOGIN request for username='{}'", userCredentials.username());
    }

    /**
     * Sends a registration request to the server with the given user credentials.
     *
     * @param userCredentials UserCredentials containing username and password
     */
    public void sendRegisterRequest(UserCredentials userCredentials) {
        connectionService.send(MessageBuilder.buildRegisterMessage(userCredentials));
        log.debug("Sent REGISTER request for username='{}'", userCredentials.username());
    }
}