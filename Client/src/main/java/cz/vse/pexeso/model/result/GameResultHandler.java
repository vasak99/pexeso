package cz.vse.pexeso.model.result;

import cz.vse.pexeso.model.service.ConnectionService;

public class GameResultHandler {
    private final ConnectionService connectionService;
    private GameResultListener listener;

    public GameResultHandler(GameResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
    }
}
