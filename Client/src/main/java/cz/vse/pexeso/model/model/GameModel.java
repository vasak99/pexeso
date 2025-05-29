package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.ResultPayload;
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

import java.util.Map;
import java.util.Set;

public class GameModel {
    private final GameService gameService;
    private final SessionService sessionService;
    private final RedirectService redirectService;

    public GameModel(GameService gameService, SessionService sessionService, RedirectService redirectService) {
        this.gameService = gameService;
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    public void attemptRevealCard(GameCard card) {
        if (!isPlayersTurn()
                || card.getStatus() == GameCard.Status.COMPLETED
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
        gameService.sendRevealCardRequest(card, getRoom(), sessionService.getSession().getPlayerId());
    }

    public void updateGame(String data) {
        Updater.updateGame(getRoom(), new GameUpdatePayload(data));
    }

    public void setResult(String data) {
        Updater.setResult(getRoom(), new ResultPayload(data));
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

    public String getPlayerName() {
        return sessionService.getSession().getPlayerName();
    }

    public long getPlayerId() {
        return sessionService.getSession().getPlayerId();
    }

    public boolean isPlayersTurn() {
        long playerId = sessionService.getSession().getPlayerId();
        long activePlayer = getGame().getActivePlayer();

        return playerId == activePlayer;
    }

    private GameRoom getRoom() {
        return sessionService.getSession().getCurrentGameRoom();
    }

    public Game getGame() {
        return getRoom().getGame();
    }
}
