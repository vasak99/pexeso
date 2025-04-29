package cz.vse.pexeso.service;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.network.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyService {
    private static final Logger log = LoggerFactory.getLogger(LobbyService.class);
    private final AppServices appServices = AppServices.getInstance();

    public void join(GameRoom gameRoom) {
        String message = MessageBuilder.buildJoinGameMessage(gameRoom);
        appServices.getConnection().sendMessage(message);
    }

    public void leave(GameRoom gameRoom) {
        String message = MessageBuilder.buildLeaveGameMessage();
        appServices.getConnection().sendMessage(message);
    }

    public void ready() {
        String message = MessageBuilder.buildPlayerReadyMessage();
        appServices.getConnection().sendMessage(message);
    }

    public void handleSuccess(String gameId) {
        GameRoom gameRoom = GameRoom.findById(gameId);
        appServices.getClientSession().setCurrentGameRoom(gameRoom);
    }

    public GameRoom getCurrentGameRoom() {
        return appServices.getClientSession().getCurrentGameRoom();
    }
}
