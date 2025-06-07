package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.network.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles server responses relevant to game room creation/management. Registers observers for redirect,
 * current room update, and error messages on the ConnectionService's MessageHandler.
 * Delegates events to the provided GameRoomResultListener.
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoomResultHandler {
    private static final Logger log = LoggerFactory.getLogger(GameRoomResultHandler.class);

    private final ConnectionService connectionService;
    private GameRoomResultListener listener;

    private final ObserverWithData redirectObserver = data -> listener.onRedirect((RedirectParameters) data);
    private final ObserverWithData lobbyUpdateObserver = data -> listener.onLobbyUpdate((LobbyUpdatePayload) data);
    private final ObserverWithData errorObserver = data -> listener.onError((String) data);

    /**
     * Constructs a GameRoomResultHandler with the specified listener and ConnectionService.
     */
    public GameRoomResultHandler(GameRoomResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
        log.debug("GameRoomResultHandler created with listener={} and connectionService={}", listener, connectionService);
    }

    /**
     * Registers the observers.
     * Subsequent server messages of these types will be forwarded to the listener.
     */
    public void register() {
        log.info("Registering GameRoomResultHandler observers");
        connectionService.getMessageHandler().registerWithData(MessageType.REDIRECT, redirectObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.LOBBY_UPDATE, lobbyUpdateObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.ERROR, errorObserver);
    }

    /**
     * Unregisters the previously registered observers.
     */
    public void unregister() {
        log.info("Unregistering GameRoomResultHandler observers");
        connectionService.getMessageHandler().unregisterWithData(MessageType.REDIRECT, redirectObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.LOBBY_UPDATE, lobbyUpdateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.ERROR, errorObserver);
    }
}
