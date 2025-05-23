package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.service.GameRoomService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.Updater;
import javafx.collections.ObservableList;

import java.util.List;

public class GameRoomModel {
    private final GameRoomService gameRoomService;
    private final SessionService sessionService;
    private final RedirectService redirectService;

    private String pendingGameId;

    public GameRoomModel(GameRoomService gameRoomService, SessionService sessionService, RedirectService redirectService) {
        this.gameRoomService = gameRoomService;
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    public void attemptCreateGame(String name, int capacity, int cardCount) {
        gameRoomService.sendCreateGameRequest(createNewGameId(), name, capacity, cardCount, sessionService.getSession().getPlayerId());
    }

    //temporary
    private String createNewGameId() {
        pendingGameId = String.valueOf(Math.round(Math.random() * 1000000));
        return pendingGameId;
    }

    public void attemptEditGame(String name, int capacity, int cardCount) {
        gameRoomService.sendEditGameRequest(getCurrentGameRoom().getGameId(), name, capacity, cardCount, sessionService.getSession().getPlayerId());
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

    public void finalizeGameCreation(Object data, String name, int capacity, int cardCount) {
        GameRoom gameRoom = new GameRoom(pendingGameId, name, sessionService.getSession().getPlayerId(), sessionService.getSession().getPlayerName(), capacity, cardCount);
        pendingGameId = null;

        GameRoom.gameRooms.add(gameRoom);
        sessionService.getSession().setCurrentGameRoom(gameRoom);
        sessionService.getSession().setHostingAGameRoom(true);
        sessionService.getSession().setReady(true);

        redirectService.redirect((String) data);
    }

    public void finalizeGameDeletion(String redirectData) {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        sessionService.getSession().setCurrentGameRoom(null);
        GameRoom.gameRooms.remove(gameRoom);
        sessionService.getSession().setHostingAGameRoom(false);

        redirectService.redirect(redirectData);
    }

    public Long getCurrentGameHostId() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom != null) {
            return gameRoom.getHostId();
        }
        return null;
    }

    public Integer getCurrentRoomCardCount() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom == null) {
            return null;
        }

        return gameRoom.getCardCount();
    }

    public Integer getCurrentRoomCapacity() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom == null) {
            return null;
        }

        return gameRoom.getCapacity();
    }

    public ObservableList<LobbyPlayer> getFilteredPlayers() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom == null) {
            return null;
        }

        return gameRoom.getPlayers().filtered(lobbyPlayer -> lobbyPlayer.getPlayerId() != getCurrentGameHostId());
    }

    public String getCurrentGameName() {
        GameRoom gameRoom = sessionService.getSession().getCurrentGameRoom();
        if (gameRoom == null) {
            return null;
        }

        return gameRoom.getName();
    }

    public boolean areAllPlayersReady(List<LobbyPlayer> players) {
        if (players == null || players.isEmpty()) {
            return false;
        }

        for (LobbyPlayer lobbyPlayer : players) {
            if (lobbyPlayer.getStatus() == LobbyPlayer.PlayerStatus.NOT_READY) {
                return false;
            }
        }
        return true;
    }

    public void updateRoom(String data) {
        Updater.updateGameRoom(getCurrentGameRoom(), new LobbyUpdatePayload(data));
    }

    private GameRoom getCurrentGameRoom() {
        return sessionService.getSession().getCurrentGameRoom();
    }
}
