package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.model.CardCoordinates;
import cz.vse.pexeso.model.Game;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.service.GameService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.GameRoomManager;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Manages in-game actions such as revealing cards, giving up, and receiving game updates.
 * Retrieves game state and board for the UI to render.
 *
 * @author kott10
 * @version June 2025
 */
public class GameModel extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(GameModel.class);

    private final GameService gameService;

    /**
     * Constructs a GameModel with the given services.
     *
     * @param gameService     service to send in-game requests
     * @param sessionService  session manager
     * @param redirectService redirect handler
     */
    public GameModel(GameService gameService, SessionService sessionService, RedirectService redirectService) {
        super(sessionService, redirectService);
        this.gameService = gameService;
    }

    /**
     * Attempts to reveal a card on the board. Only proceeds if:
     * - card is non-null,
     * - it is the player’s turn,
     * - card status is HIDDEN,
     * - fewer than two cards are currently selected.
     * Sends a reveal request to the server if conditions are met.
     *
     * @param card the GameCard to reveal
     */
    public void attemptRevealCard(GameCard card) {
        if (card == null) {
            log.warn("attemptRevealCard called with null card");
            return;
        }
        if (!isPlayersTurn()) {
            log.debug("Not player’s turn; ignoring reveal for card at ({}, {})", card.getRow(), card.getColumn());
            return;
        }
        if (card.getStatus() != GameCard.Status.HIDDEN) {
            log.debug("Card at ({}, {}) is not hidden; status={}", card.getRow(), card.getColumn(), card.getStatus());
            return;
        }
        Set<GameCard> turn = getCurrentTurn();
        if (turn == null) {
            log.warn("Current turn set is null; cannot reveal card");
            return;
        }
        if (turn.size() >= 2) {
            log.debug("Already two cards revealed in turn; ignoring additional reveal");
            return;
        }
        turn.add(card);
        log.info("Sending reveal request for card at ({}, {}) by playerId={}", card.getRow(), card.getColumn(), getPlayerId());
        sendReveal(new CardCoordinates(card.getRow(), card.getColumn()));
    }

    /**
     * Sends a reveal-card request to the server.
     */
    private void sendReveal(CardCoordinates coordinates) {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot send reveal: missing gameId");
            return;
        }
        gameService.sendRevealCardRequest(coordinates, getCurrentGameRoomId(), getPlayerId());
    }

    /**
     * Attempts to send a give-up request to the server if in a valid game room.
     */
    public void attemptGiveUp() {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot give up: missing currentGameId");
            return;
        }
        log.info("Sending give-up request for gameId={}", getCurrentGameRoomId());
        gameService.sendGiveUpRequest(getCurrentGameRoomId(), getPlayerId());
    }

    /**
     * Leaves the game room by redirecting and marking readiness false.
     *
     * @param parameters redirect payload
     */
    public void leaveRoom(RedirectParameters parameters) {
        log.info("Leaving room and redirecting with data={}", parameters);
        redirect(parameters);
        setReady(false);
    }

    /**
     * Updates the lobby’s game room listings based on payload data.
     *
     * @param glp update payload
     */
    public void updateGameRooms(GameListPayload glp) {
        GameRoomManager.update(glp);
        log.info("Game rooms updated via GameModel");
    }

    /**
     * Updates the current game’s state based on a JSON payload.
     *
     * @param gup update payload
     */
    public void updateGame(GameUpdatePayload gup) {
        if (getGame() == null) {
            log.warn("Cannot update game: no Game instance available");
            return;
        }
        getGame().update(gup);
        log.info("Game state updated for GameRoom");
    }

    /**
     * Retrieves the Board for rendering.
     *
     * @return the Board instance, or null if none
     */
    public Board getGameBoard() {
        if (getGame() != null) {
            return getGame().getGameBoard();
        }
        return null;
    }

    /**
     * Retrieves the map of player IDs to their assigned colors.
     *
     * @return the player-colors map, or null if none
     */
    public Map<Long, String> getPlayerColors() {
        if (getGame() != null) {
            return getGame().getPlayerColors();
        }
        return null;
    }

    /**
     * @return true if it is the current player’s turn, false otherwise (and false if no game or room)
     */
    public boolean isPlayersTurn() {
        if (getGame() != null) {
            return getGame().isPlayersTurn(getPlayerId());
        }
        return false;
    }

    /**
     * @return an observable list of players in the current game room, or null if no room is active
     */
    public ObservableList<LobbyPlayer> getPlayers() {
        if (isInRoom()) {
            return getCurrentGameRoom().getPlayers();
        }
        return null;
    }

    /**
     * Sets the in-progress flag on the current game room if valid.
     *
     * @param b true to mark in-progress, false otherwise
     */
    public void setInProgress(boolean b) {
        if (isInRoom()) {
            getCurrentGameRoom().setInProgress(b);
            log.info("Set inProgress={} for GameRoom", b);
        } else {
            log.warn("Cannot set inProgress: no current GameRoom");
        }
    }

    /**
     * @return the Game instance for the current room, or null if none
     */
    private Game getGame() {
        if (isInRoom()) {
            return getCurrentGameRoom().getGame();
        }
        return null;
    }

    /**
     * @return the set of currently revealed GameCards, or null if no game exists
     */
    private Set<GameCard> getCurrentTurn() {
        if (getGame() != null) {
            return getGame().getCurrentTurn();
        }
        return null;
    }

    /**
     * @return an observable list of final results (scores) for the current game, or null if no game
     */
    public ObservableList<LobbyPlayer> getResultList() {
        if (getGame() != null) {
            return getGame().getResultList();
        }
        return null;
    }

    /**
     * Sets the ready flag on the session.
     *
     * @param b true if player is ready, false otherwise
     */
    public void setReady(boolean b) {
        getSession().setReady(b);
    }
}