package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.util.MessageBuilder;

public class LobbyService {
    private final ConnectionService connectionService;

    public LobbyService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void sendJoinGameRequest(GameRoom gameRoom, long playerId) {
        String message = MessageBuilder.buildJoinGameMessage(gameRoom, playerId);
        connectionService.send(message);
    }

    public void sendLeaveGameRequest(GameRoom gameRoom, long playerId) {
        String message = MessageBuilder.buildLeaveGameMessage(gameRoom, playerId);
        connectionService.send(message);
    }

    public void sendReadyRequest(GameRoom gameRoom, long playerId) {
        String message = MessageBuilder.buildPlayerReadyMessage(gameRoom, playerId);
        connectionService.send(message);
    }

    public void sendIdentity(long playerId) {
        String message = MessageBuilder.buildIdentityMessage(playerId);
        connectionService.send(message);
    }
}
