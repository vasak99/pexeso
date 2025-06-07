package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.network.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles server responses relevant to lobby. Registers observers for identity request,
 * redirect, update, and error messages on the ConnectionService's MessageHandler.
 * Delegates events to the provided LobbyResultListener.
 *
 * @author kott10
 * @version June 2025
 */
public class LobbyResultHandler {
    private static final Logger log = LoggerFactory.getLogger(LobbyResultHandler.class);

    private final ConnectionService connectionService;
    private LobbyResultListener listener;

    private final Observer requestIdentityObserver = () -> listener.onRequestIdentity();
    private final ObserverWithData startGameObserver = data -> listener.onStartGame((GameUpdatePayload) data);
    private final ObserverWithData redirectObserver = data -> listener.onRedirect((RedirectParameters) data);
    private final ObserverWithData lobbyUpdateObserver = data -> listener.onLobbyUpdate((LobbyUpdatePayload) data);
    private final ObserverWithData gameServerUpdateObserver = data -> listener.onGameServerUpdate((GameListPayload) data);
    private final ObserverWithData errorObserver = data -> listener.onError((String) data);

    /**
     * Constructs a LobbyResultHandler with the given listener and ConnectionService.
     */
    public LobbyResultHandler(LobbyResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
        log.debug("LobbyResultHandler created with listener={} and connectionService={}", listener, connectionService);
    }

    /**
     * Registers the observers that should be active even if there is a window opened over the lobby screen.
     * Subsequent server messages of these types will be forwarded to the listener.
     */
    public void initialRegister() {
        log.info("Performing initial registration of LobbyResultHandler observers");
        connectionService.getMessageHandler().register(MessageType.REQUEST_IDENTITY, requestIdentityObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.START_GAME, startGameObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.LOBBY_UPDATE, lobbyUpdateObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.GAME_SERVER_UPDATE, gameServerUpdateObserver);
        register();
    }

    /**
     * Registers the observers that should not be active if there is a window opened over the lobby screen.
     * Subsequent server messages of these types will be forwarded to the listener.
     */
    public void register() {
        log.info("Registering redirect and error observers for LobbyResultHandler");
        connectionService.getMessageHandler().registerWithData(MessageType.REDIRECT, redirectObserver);
        connectionService.getMessageHandler().registerWithData(MessageType.ERROR, errorObserver);
    }

    /**
     * Unregisters the previously registered observers that should not be active if there is a window opened over the lobby screen.
     */
    public void unregister() {
        log.info("Unregistering redirect and error observers for LobbyResultHandler");
        connectionService.getMessageHandler().unregisterWithData(MessageType.REDIRECT, redirectObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.ERROR, errorObserver);
    }

    /**
     * Unregisters the previously registered observers that should be active even if there is a window opened over the lobby screen.
     */
    public void finalUnregister() {
        log.info("Performing final unregistration of all LobbyResultHandler observers");
        unregister();
        connectionService.getMessageHandler().unregister(MessageType.REQUEST_IDENTITY, requestIdentityObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.START_GAME, startGameObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.LOBBY_UPDATE, lobbyUpdateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageType.GAME_SERVER_UPDATE, gameServerUpdateObserver);
    }
}
