package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.util.MessageBuilder;

public class GameRoomService {
    private final ConnectionService connectionService;

    public GameRoomService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void sendCreateGameRequest(int capacity, int cardCount, long playerId) {
        String message = MessageBuilder.buildCreateGameMessage(new GameRoom(capacity, cardCount), playerId);
        connectionService.send(message);
    }

    public void sendEditGameRequest(int capacity, int cardCount, long playerId) {
        String message = MessageBuilder.buildEditGameMessage(new GameRoom(capacity, cardCount), playerId);
        connectionService.send(message);
    }

    public void sendDeleteGameRequest(GameRoom gameRoom, long playerId) {
        String message = MessageBuilder.buildDeleteGameMessage(gameRoom, playerId);
        connectionService.send(message);
    }

    public void sendStartGameRequest(GameRoom gameRoom, long playerId) {
        String message = MessageBuilder.buildStartGameMessage(gameRoom, playerId);
        connectionService.send(message);
    }

    public void sendKickPlayerRequest(GameRoom gameRoom, long playerId, LobbyPlayer lobbyPlayer) {
        String message = MessageBuilder.buildKickPlayerMessage(gameRoom, playerId, lobbyPlayer);
        connectionService.send(message);
    }
}
