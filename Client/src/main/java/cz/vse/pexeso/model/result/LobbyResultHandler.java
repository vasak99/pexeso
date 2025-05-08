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
    private final Observer upateObserver = () -> listener.onLobbyUpdate();

    public LobbyResultHandler(LobbyResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
    }

    public void initialRegister() {
        register();
        connectionService.getMessageHandler().register(MessageTypeClient.GAME_TABLE_CHANGE, upateObserver);
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
        connectionService.getMessageHandler().unregister(MessageTypeClient.GAME_TABLE_CHANGE, upateObserver);
    }
}
