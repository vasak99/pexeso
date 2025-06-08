package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.network.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles server responses related to in-game events. Registers observers for game update,
 * invalid move, result, redirect, game room update, and error messages.
 * Delegates each event to the provided GameResultListener.
 *
 * @author kott10
 * @version June 2025
 */
public class GameResultHandler {
    private static final Logger log = LoggerFactory.getLogger(GameResultHandler.class);

    private final ConnectionService connectionService;
    private GameResultListener listener;

    private final ObserverWithData gameUpdateObserver = data -> listener.onGameUpdate((GameUpdatePayload) data);
    private final ObserverWithData invalidMoveObserver = data -> listener.onInvalidMove((String) data);
    private final Observer resultObserver = () -> listener.onResult();
    private final ObserverWithData redirectObserver = data -> listener.onRedirect((RedirectParameters) data);
    private final ObserverWithData gameServerUpdateObserver = data -> listener.onGameServerUpdate((GameListPayload) data);
    private final ObserverWithData errorObserver = data -> listener.onError((String) data);

    /**
     * Constructs a GameResultHandler with the given listener and ConnectionService.
     */
    public GameResultHandler(GameResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
        log.debug("GameResultHandler created with listener={} and connectionService={}", listener, connectionService);
    }

    /**
     * Registers the observers.
     * Subsequent server messages of these types will be forwarded to the listener.
     */
    public void register() {
        log.info("Registering GameResultHandler observers");
        connectionService.getMessageHandler().registerWithData(MessageType.GAME_UPDATE, gameUpdateObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.INVALID_MOVE, invalidMoveObserver);
        connectionService.getMessageHandler().register(MessageType.RESULT, resultObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.REDIRECT, redirectObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.GAME_SERVER_UPDATE, gameServerUpdateObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.ERROR, errorObserver);
    }

    /**
     * Unregisters the previously registered observers.
     */
    public void unregister() {
        log.info("Unregistering GameResultHandler observers");
        connectionService.getMessageHandler().unregisterWithData(MessageType.GAME_UPDATE, gameUpdateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.INVALID_MOVE, invalidMoveObserver);
        connectionService.getMessageHandler().unregister(MessageType.RESULT, resultObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.REDIRECT, redirectObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.GAME_SERVER_UPDATE, gameServerUpdateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.ERROR, errorObserver);
    }
}
