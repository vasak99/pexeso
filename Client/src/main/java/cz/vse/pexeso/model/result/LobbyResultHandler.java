package cz.vse.pexeso.model.result;

import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.model.service.ConnectionService;

public class LobbyResultHandler {
    private final ConnectionService connectionService;
    private LobbyResultListener listener;

    private final ObserverWithData successObserver = data -> listener.onLobbySuccess((String) data);
    private final ObserverWithData errorObserver = data -> listener.onLobbyError((String) data);
    private final ObserverWithData lobbyUpdateObserver = data -> listener.onGameRoomUpdate((String) data);
    private final ObserverWithData playerUpdateObserver = data -> listener.onPlayerUpdate((String) data);
    private final Observer uiUpdateObserver = () -> listener.onLobbyUIUpdate();
    private final Observer identityObserver = () -> listener.onIdentityRequested();
    private final ObserverWithData startGameObserver = data -> listener.onStartGame((String) data);

    public LobbyResultHandler(LobbyResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
    }

    public void initialRegister() {
        register();
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_UPDATE, lobbyUpdateObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.PLAYER_UPDATE, playerUpdateObserver);
        connectionService.getMessageHandler().register(MessageTypeClient.LOBBY_UI_UPDATE, uiUpdateObserver);
        connectionService.getMessageHandler().register(MessageTypeClient.IDENTITY_REQUESTED, identityObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.START_GAME, startGameObserver);
    }

    public void register() {
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
    }

    public void unregister() {
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
    }

    public void finalUnregister() {
        unregister();
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_ROOM_UPDATE, lobbyUpdateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.PLAYER_UPDATE, playerUpdateObserver);
        connectionService.getMessageHandler().unregister(MessageTypeClient.LOBBY_UI_UPDATE, uiUpdateObserver);
        connectionService.getMessageHandler().unregister(MessageTypeClient.IDENTITY_REQUESTED, identityObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.START_GAME, startGameObserver);
    }
}
