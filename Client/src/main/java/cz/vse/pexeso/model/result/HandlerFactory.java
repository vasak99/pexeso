package cz.vse.pexeso.model.result;


import cz.vse.pexeso.network.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating result handlers that dispatch server messages to observers.
 * Listeners get passed in as parameters.
 *
 * @author kott10
 * @version June 2025
 */
public class HandlerFactory {
    private static final Logger log = LoggerFactory.getLogger(HandlerFactory.class);
    private final ConnectionService connectionService;

    public HandlerFactory(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public GameResultHandler createGameResultHandler(GameResultListener listener) {
        log.debug("Creating GameResultHandler");
        return new GameResultHandler(listener, connectionService);
    }

    public AuthResultHandler createAuthResultHandler(AuthResultListener listener) {
        log.debug("Creating AuthResultHandler");
        return new AuthResultHandler(listener, connectionService);
    }

    public LobbyResultHandler createLobbyResultHandler(LobbyResultListener listener) {
        log.debug("Creating LobbyResultHandler");
        return new LobbyResultHandler(listener, connectionService);
    }

    public GameRoomResultHandler createGameRoomResultHandler(GameRoomResultListener listener) {
        log.debug("Creating GameRoomResultHandler");
        return new GameRoomResultHandler(listener, connectionService);
    }
}