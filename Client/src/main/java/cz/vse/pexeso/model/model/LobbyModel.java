package cz.vse.pexeso.model.model;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.service.LobbyService;
import cz.vse.pexeso.model.service.SessionService;

public class LobbyModel {

    private final LobbyService lobbyService;
    private final SessionService sessionService;

    public LobbyModel(LobbyService lobbyService, SessionService sessionService) {
        this.lobbyService = lobbyService;
        this.sessionService = sessionService;
    }

    public void attemptJoin(GameRoom gameRoom) {
        lobbyService.sendJoinGameRequest(gameRoom, sessionService.getSession().getPlayerId());
    }

    public void attemptLeave() {
        lobbyService.sendLeaveGameRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
    }

    public void attemptReady() {
        lobbyService.sendReadyRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
        sessionService.getSession().setReady(true);
    }

    public void finalizeSuccess(String gameId) {
        GameRoom gameRoom = GameRoom.findById(gameId);
        sessionService.getSession().setCurrentGameRoom(gameRoom);
    }

    public boolean isHosting() {
        return sessionService.getSession().isHostingAGameRoom();
    }

    public boolean isReady() {
        return sessionService.getSession().isReady();
    }

    public boolean isHost(GameRoom gameRoom) {
        return sessionService.getSession().getPlayerId() == gameRoom.getHostId();
    }

    public boolean isInARoom() {
        return getCurrentGameRoomId() != null;
    }

    public String getCurrentGameRoomId() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom != null) {
            return gameRoom.getGameId();
        }
        return null;
    }
}
