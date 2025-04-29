package cz.vse.pexeso.service;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.network.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameRoomWindowService {
    private static final Logger log = LoggerFactory.getLogger(GameRoomWindowService.class);
    private final AppServices appServices = AppServices.getInstance();


    public void create(int capacity, int cardCount) {
        String message = MessageBuilder.buildCreateGameMessage(new GameRoom(capacity, cardCount));
        appServices.getConnection().sendMessage(message);
    }

    public void edit(int capacity, int cardCount) {
        String message = MessageBuilder.buildEditGameMessage(new GameRoom(capacity, cardCount));
        appServices.getConnection().sendMessage(message);
    }

    public void delete() {
        String message = MessageBuilder.buildDeleteGameMessage();
        appServices.getConnection().sendMessage(message);
    }

    public void kick(LobbyPlayer lobbyPlayer) {
        String message = MessageBuilder.buildKickPlayerMessage(lobbyPlayer);
        appServices.getConnection().sendMessage(message);
    }

    public void handleCreateSuccess(Object redirectData, int capacity, int cardCount) {
        long hostId = appServices.getClientSession().getPlayerId();
        //GameRoom gameRoom = new GameRoom((String) gameId, hostId, capacity, cardCount);
        GameRoom gameRoom = new GameRoom(hostId, capacity, cardCount);
        GameRoom.gameRooms.add(gameRoom);
        appServices.getClientSession().setCurrentGameRoom(gameRoom);

        log.info("Redirecting");
        //Redirect.redirect((String) redirectData);
    }

    public void handleEditSuccess(int capacity, int cardCount) {
        String gameId = appServices.getClientSession().getCurrentGameRoom().getGameId();
        GameRoom.editGameRoom(gameId, capacity, cardCount);

    }

    public void handleDeleteSuccess() {
        GameRoom gameRoom = appServices.getClientSession().getCurrentGameRoom();
        appServices.getClientSession().setCurrentGameRoom(null);
        GameRoom.gameRooms.remove(gameRoom);
    }

    public List<LobbyPlayer> filterPlayers() {
        String gameId = appServices.getClientSession().getCurrentGameRoom().getGameId();
        return LobbyPlayer.lobbyPlayers.stream()
                .filter(player -> player.getCurrentGameId().equals(gameId))
                .toList();
    }

}
