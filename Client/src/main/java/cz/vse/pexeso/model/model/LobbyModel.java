package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.ClientSession;
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
        lobbyService.sendJoinGameRequest(gameRoom, getSession().getPlayerId());
    }

    public void attemptLeave() {
        lobbyService.sendLeaveGameRequest(getCurrentGameRoom(), getSession().getPlayerId());
    }

    public void attemptReady() {
        lobbyService.sendReadyRequest(getCurrentGameRoom(), getSession().getPlayerId());
        getSession().setReady(true);
    }

    public void sendIdentity() {
        lobbyService.sendIdentity(getSession().getPlayerId());
    }

    public void finalizeJoin(String redirectData) {
        getSession().setCurrentGameRoom(GameRoom.findById(attemptedJoinGameId));
        attemptedJoinGameId = null;

        redirectService.redirect(redirectData);
    }

    public void finalizeLeave(String redirectData) {
        getSession().setCurrentGameRoom(null);
        redirectService.redirect(redirectData);
        getSession().setReady(false);
    }

    public void updatePlayers(String data) {
        Updater.updateGameRoom(getCurrentGameRoom(), new LobbyUpdatePayload(data));
    }

    public void updateGameRooms(String data) {
        Updater.updateLobby(new GameListPayload(data));
    }

    public void initializeGame(String data) {
        getCurrentGameRoom().setGame(new Game());
        Updater.updateGame(getCurrentGameRoom(), new GameUpdatePayload(data));
    }

    public ClientSession getSession() {
        return sessionService.getSession();
    }

    public GameRoom getCurrentGameRoom() {
        return getSession().getCurrentGameRoom();
    }

    public String getCurrentGameRoomId() {
        GameRoom gameRoom = getCurrentGameRoom();
        if (gameRoom != null) {
            return gameRoom.getGameId();
        }
        return null;
    }

    public boolean isInARoom() {
        return getCurrentGameRoomId() != null;
    }

    public boolean isHosting() {
        return getSession().isHostingAGameRoom();
    }

    public boolean isReady() {
        return getSession().isReady();
    }

    public boolean isHost(GameRoom gameRoom) {
        return getSession().getPlayerId() == gameRoom.getHostId();
    }
}
