package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.Game;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.service.LobbyService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.Updater;

public class LobbyModel {

    private final LobbyService lobbyService;
    private final SessionService sessionService;
    private final RedirectService redirectService;

    private String attemptedJoinGameId;

    public LobbyModel(LobbyService lobbyService, SessionService sessionService, RedirectService redirectService) {
        this.lobbyService = lobbyService;
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    public void attemptJoin(GameRoom gameRoom) {
        attemptedJoinGameId = gameRoom.getGameId();
        lobbyService.sendJoinGameRequest(gameRoom, sessionService.getSession().getPlayerId());
    }

    public void attemptLeave() {
        lobbyService.sendLeaveGameRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
    }

    public void attemptReady() {
        lobbyService.sendReadyRequest(sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
        sessionService.getSession().setReady(true);
    }

    public void sendIdentity() {
        lobbyService.sendIdentity(sessionService.getSession().getPlayerId());
    }

    public void finalizeJoin(String redirectData) {
        sessionService.getSession().setCurrentGameRoom(GameRoom.findById(attemptedJoinGameId));
        attemptedJoinGameId = null;

        redirectService.redirect(redirectData);
    }

    public void finalizeLeave(String redirectData) {
        sessionService.getSession().setCurrentGameRoom(null);
        redirectService.redirect(redirectData);
        sessionService.getSession().setReady(false);
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

    public GameRoom getCurrentGameRoom() {
        return sessionService.getSession().getCurrentGameRoom();
    }

    public void updatePlayers(String data) {
        Updater.updateGameRoom(getCurrentGameRoom(), new LobbyUpdatePayload(data));
    }

    public void updateGameRooms(String data) {
        Updater.updateLobby(new GameListPayload(data));
    }

    public String getPlayerName() {
        return sessionService.getSession().getPlayerName();
    }

    public void initializeGame(String data) {
        getCurrentGameRoom().setGame(new Game());
        Updater.updateGame(getCurrentGameRoom(), new GameUpdatePayload(data));
    }
}
