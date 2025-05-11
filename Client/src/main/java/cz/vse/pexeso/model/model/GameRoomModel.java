package cz.vse.pexeso.model.model;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.service.GameRoomService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;

public class GameRoomModel {
    private final GameRoomService gameRoomService;
    private final SessionService sessionService;
    private final RedirectService redirectService;

    public GameRoomModel(GameRoomService gameRoomService, SessionService sessionService, RedirectService redirectService) {
        this.gameRoomService = gameRoomService;
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    public void attemptCreateGame(int capacity, int cardCount) {
        gameRoomService.sendCreateGameRequest(capacity, cardCount, sessionService.getSession().getPlayerId());
    }

    public void attemptEditGame(int capacity, int cardCount) {
        gameRoomService.sendEditGameRequest(capacity, cardCount, sessionService.getSession().getPlayerId());
    }

    public void attemptDeleteGame() {
        gameRoomService.sendDeleteGameRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
    }

    public void attemptStartGame() {
        gameRoomService.sendStartGameRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
    }

    public void attemptKickPlayer(LobbyPlayer lobbyPlayer) {
        gameRoomService.sendKickPlayerRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId(), lobbyPlayer);
    }

    public void finalizeGameCreation(Object data, int capacity, int cardCount) {
        long hostId = sessionService.getSession().getPlayerId();
        GameRoom gameRoom = new GameRoom(hostId, capacity, cardCount);
        GameRoom.gameRooms.add(gameRoom);
        sessionService.getSession().setCurrentGameRoom(gameRoom);
        sessionService.getSession().setHostingAGameRoom(true);

        //redirectService.redirect((String) data);
    }

    public void finalizeGameEdit(int capacity, int cardCount) {
        String gameId = getCurrentGameId();
        GameRoom.editGameRoom(gameId, capacity, cardCount);
    }

    public void finalizeGameDeletion() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        sessionService.getSession().setCurrentGameRoom(null);
        GameRoom.gameRooms.remove(gameRoom);
        sessionService.getSession().setHostingAGameRoom(false);
    }

    public Long getCurrentGameHostId() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom != null) {
            return gameRoom.getHostId();
        }
        return null;
    }

    public int getCurrentRoomCardCount() {
        return sessionService.getSession().getCurrentGameRoom().getCardCount();
    }

    public int getCurrentRoomCapacity() {
        return sessionService.getSession().getCurrentGameRoom().getCapacity();
    }

    public String getCurrentGameId() {
        return sessionService.getSession().getCurrentGameRoom().getGameId();
    }
}
