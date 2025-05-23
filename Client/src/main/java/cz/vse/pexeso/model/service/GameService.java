package cz.vse.pexeso.model.service;

public class GameService {
    private final ConnectionService connectionService;

    public GameService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
}
