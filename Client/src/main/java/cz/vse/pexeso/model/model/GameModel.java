package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.environment.Variables;
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
        gameService.sendRevealCardRequest(card, getRoom(), getSession().getPlayerId());
    }

    public void attemptGiveUp() {
        gameService.sendGiveUpRequest(getRoom(), getPlayerId());
    }

    public void updateGame(String data) {
        Updater.updateGame(getRoom(), new GameUpdatePayload(data));
    }

    public void setResult(String data) {
        Updater.setResult(getRoom(), new ResultPayload(data));
    }

    public void redirectToLobby() {
        redirectService.redirect(Variables.SERVER_ADDR + ":" + Variables.DEFAULT_PORT);
    }

    public boolean isPlayersTurn() {
        long playerId = getSession().getPlayerId();
        long activePlayer = getGame().getActivePlayer();

        return playerId == activePlayer;
    }

    public ClientSession getSession() {
        return sessionService.getSession();
    }

    private GameRoom getRoom() {
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
}
