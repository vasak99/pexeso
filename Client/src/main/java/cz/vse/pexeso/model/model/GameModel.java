package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.ResultPayload;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.Game;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.service.GameService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.Updater;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameModel {
    private final GameService gameService;
    private final SessionService sessionService;
    private final RedirectService redirectService;

    private List<LobbyPlayer> resultList = new ArrayList<>();

    boolean requestedToGiveUp = false;

    public GameModel(GameService gameService, SessionService sessionService, RedirectService redirectService) {
        this.gameService = gameService;
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    public void attemptRevealCard(GameCard card) {
        if (!isPlayersTurn()
                || card.getStatus() != GameCard.Status.HIDDEN
                || getCurrentTurn().size() >= 2) {
            return;
        }
        if (getCurrentTurn().isEmpty()) {
            getCurrentTurn().add(card);
            sendReveal(card);
        } else if (getCurrentTurn().add(card)) {
            sendReveal(card);
        }
    }

    private void sendReveal(GameCard card) {
        gameService.sendRevealCardRequest(card, getRoom(), getSession().getPlayerId());
    }

    public void attemptGiveUp() {
        requestedToGiveUp = true;
        gameService.sendGiveUpRequest(getRoom(), getPlayerId());
    }

    public void updateGame(String data) {
        Updater.updateGame(getRoom(), new GameUpdatePayload(data), getCurrentTurn());
    }

    public void setResult(String data) {
        this.resultList = Updater.setResult(getRoom(), new ResultPayload(data));
    }

    public void redirect(String data) {
        redirectService.redirect(data);
    }

    public boolean isPlayersTurn() {
        long playerId = getSession().getPlayerId();
        long activePlayer = getGame().getActivePlayer();

        return playerId == activePlayer;
    }

    public ClientSession getSession() {
        return sessionService.getSession();
    }

    public GameRoom getRoom() {
        return getSession().getCurrentGameRoom();
    }

    public Game getGame() {
        return getRoom().getGame();
    }

    public Board getGameBoard() {
        return getGame().getGameBoard();
    }

    public ObservableList<LobbyPlayer> getPlayers() {
        return getRoom().getPlayers();
    }

    public Map<Long, String> getPlayerColors() {
        return getGame().getPlayerColors();
    }

    private Set<GameCard> getCurrentTurn() {
        return getGame().getCurrentTurn();
    }

    public long getPlayerId() {
        return getSession().getPlayerId();
    }

    public void setInProgress(boolean b) {
        getRoom().setInProgress(b);
    }

    public boolean isRequestedToGiveUp() {
        return requestedToGiveUp;
    }

    public void setRequestedToGiveUp(boolean requestedToGiveUp) {
        this.requestedToGiveUp = requestedToGiveUp;
    }

    public List<LobbyPlayer> getResultList() {
        return resultList;
    }

    public void updateGameRooms(String data) {
        Updater.updateLobby(new GameListPayload(data));
    }
}
