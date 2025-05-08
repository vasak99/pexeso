package cz.vse.pexeso.model.result;

import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.model.service.ConnectionService;

public class GameRoomResultHandler {
    private final ConnectionService connectionService;
    private GameRoomResultListener listener;

    private final ObserverWithData successObserver = data -> listener.onGameRoomSuccess(data);
    private final ObserverWithData errorObserver = data -> listener.onGameRoomError((String) data);

    public GameRoomResultHandler(GameRoomResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
    }

    public void register() {
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }

    public void unregister() {
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }
}
