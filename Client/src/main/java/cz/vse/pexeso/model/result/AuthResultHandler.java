package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.network.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles server responses relevant to authentication. Registers observers for identity,
 * game room update, and error messages on the ConnectionService's MessageHandler.
 * Delegates events to the provided AuthResultListener.
 *
 * @author kott10
 * @version June 2025
 */
public class AuthResultHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthResultHandler.class);

    private final ConnectionService connectionService;
    private AuthResultListener listener;

    private final ObserverWithData identityObserver = data -> listener.onIdentity((Long) data);
    private final ObserverWithData gameServerUpdateObserver = data -> listener.onGameServerUpdate((GameListPayload) data);
    private final ObserverWithData errorObserver = data -> listener.onError((String) data);

    /**
     * Constructs an AuthResultHandler with the given listener and ConnectionService.
     */
    public AuthResultHandler(AuthResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
        log.debug("AuthResultHandler created with listener={} and connectionService={}", listener, connectionService);
    }

    /**
     * Registers the observers.
     * Subsequent server messages of these types will be forwarded to the listener.
     */
    public void register() {
        log.info("Registering AuthResultHandler observers");
        connectionService.getMessageHandler().registerWithData(MessageType.IDENTITY, identityObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.GAME_SERVER_UPDATE, gameServerUpdateObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.ERROR, errorObserver);
    }

    /**
     * Unregisters the previously registered observers.
     */
    public void unregister() {
        log.info("Unregistering AuthResultHandler observers");
        connectionService.getMessageHandler().unregisterWithData(MessageType.IDENTITY, identityObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.GAME_SERVER_UPDATE, gameServerUpdateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.ERROR, errorObserver);
    }
}
