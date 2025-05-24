package cz.vse.pexeso.model.service;

import cz.vse.pexeso.view.GameCard;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.util.MessageBuilder;

public class GameService {
    private final ConnectionService connectionService;

    public GameService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void sendRevealCardRequest(GameCard card, GameRoom gameRoom, long playerId) {
        String message = MessageBuilder.buildRevealCardMessage(card, gameRoom, playerId);
        connectionService.send(message);
    }
}
