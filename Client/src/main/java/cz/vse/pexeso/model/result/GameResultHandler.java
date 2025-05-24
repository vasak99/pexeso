package cz.vse.pexeso.model.result;

import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.model.service.ConnectionService;

public class GameResultHandler {
    private final ConnectionService connectionService;
    private GameResultListener listener;

    private final ObserverWithData invalidMoveObserver = data -> listener.onInvalidMove((String) data);
    private final ObserverWithData updateObserver = data -> listener.onGameUpdate((String) data);
    private final ObserverWithData resultObserver = data -> listener.onGameResult((String) data);
    private final ObserverWithData errorObserver = data -> listener.onGameError((String) data);

    public GameResultHandler(GameResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
    }

    public void register() {
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.INVALID_MOVE, invalidMoveObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.GAME_UPDATE, updateObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.GAME_RESULT, resultObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
    }

    public void unregister() {
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.INVALID_MOVE, invalidMoveObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_UPDATE, updateObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_RESULT, resultObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
    }
}
