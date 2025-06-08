package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.model.service.AuthService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.GameRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages authentication-related logic: sending login/registration requests, finalizing
 * authentication by storing the session, and updating the lobby listing.
 *
 * @author kott10
 * @version June 2025
 */
public class AuthModel extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(AuthModel.class);

    private UserCredentials credentials;
    private final AuthService authService;

    /**
     * Constructs an AuthModel with the given services.
     *
     * @param authService     service to send auth requests
     * @param sessionService  session manager
     * @param redirectService redirect handler
     */
    public AuthModel(AuthService authService, SessionService sessionService, RedirectService redirectService) {
        super(sessionService, redirectService);
        this.authService = authService;
        this.credentials = null;
    }

    /**
     * Attempts to log in using previously set credentials.
     *
     * @throws IllegalStateException if credentials have not been set
     */
    public void attemptLogin() {
        if (credentials == null) {
            throw new IllegalStateException("Credentials not set before login attempt");
        }
        log.info("Sending login request for user={}", credentials.username());
        authService.sendLoginRequest(credentials);
    }

    /**
     * Attempts to register using previously set credentials.
     *
     * @throws IllegalStateException if credentials have not been set
     */
    public void attemptRegister() {
        if (credentials == null) {
            throw new IllegalStateException("Credentials not set before register attempt");
        }
        log.info("Sending registration request for user={}", credentials.username());
        authService.sendRegisterRequest(credentials);
    }

    /**
     * Finalizes authentication by creating a new ClientSession and storing it in SessionService.
     *
     * @param playerId the player ID returned by the server
     */
    public void finalizeAuth(long playerId) {
        sessionService.setSession(new ClientSession(playerId, credentials));
        log.info("Authentication finalized: new session for playerId={}", playerId);
    }

    /**
     * Sets credentials to be used for subsequent login or registration.
     *
     * @param credentials UserCredentials
     */
    public void setCredentials(UserCredentials credentials) {
        this.credentials = credentials;
        log.debug("Credentials set for user={}", credentials.username());
    }

    /**
     * Clears stored credentials.
     */
    public void emptyCredentials() {
        this.credentials = null;
        log.debug("Credentials cleared");
    }

    /**
     * Updates the lobby listing based on payload
     *
     * @param data update payload
     */
    public void updateLobby(GameListPayload data) {
        GameRoomManager.update(data);
        log.info("Lobby updated via AuthModel");
    }
}