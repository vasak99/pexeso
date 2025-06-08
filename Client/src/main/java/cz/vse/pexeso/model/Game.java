package cz.vse.pexeso.model;

import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.util.updater.GameUpdater;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Represents the state of an ongoing game, including the board, active player, turn cards,
 * player colors, and final results.
 * Updated from server to reflect real-time changes.
 *
 * @author kott10
 * @version June 2025
 */
public class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private Board gameBoard = null;
    private long activePlayer;
    private final Set<GameCard> currentTurn = new HashSet<>();
    private final Map<Long, String> playerColors = new HashMap<>();
    private final ObservableList<LobbyPlayer> resultList = FXCollections.observableArrayList();

    /**
     * Default constructor (gameBoard remains null until first update).
     */
    public Game() {
    }

    /**
     * @return the current Board, or null if not set
     */
    public Board getGameBoard() {
        return gameBoard;
    }

    /**
     * Sets the game board
     *
     * @param gameBoard the Board
     */
    public void setGameBoard(Board gameBoard) {
        log.debug("Setting game board");
        this.gameBoard = gameBoard;
    }

    /**
     * @return the ID of the player whose turn it is
     */
    public long getActivePlayer() {
        return activePlayer;
    }

    /**
     * Sets which player is active.
     *
     * @param activePlayer the player ID whose turn it is
     */
    public void setActivePlayer(long activePlayer) {
        this.activePlayer = activePlayer;
        log.debug("Active player set to {}", activePlayer);
    }

    /**
     * @return the set of GameCards currently selected in this turn
     */
    public Set<GameCard> getCurrentTurn() {
        return currentTurn;
    }

    /**
     * @return the map of player IDs to assigned colors
     */
    public Map<Long, String> getPlayerColors() {
        return playerColors;
    }

    /**
     * @return an observable list of final results (LobbyPlayer instances with scores)
     */
    public ObservableList<LobbyPlayer> getResultList() {
        return resultList;
    }

    /**
     * Checks if the given player ID corresponds to the active player.
     *
     * @param playerId ID to check
     * @return true if it is that player's turn
     */
    public boolean isPlayersTurn(long playerId) {
        return activePlayer == playerId;
    }

    /**
     * Assigns each player a color from a predefined list in UIConstants.
     *
     * @param players list of players in the game
     */
    public void setupPlayerColors(List<LobbyPlayer> players) {
        playerColors.clear();
        for (int i = 0; i < players.size(); i++) {
            playerColors.put(players.get(i).getPlayerId(), UIConstants.getColors().get(i));
        }
        log.info("Player colors set up for {} players", players.size());
    }

    /**
     * Updates the game state from a payload.
     *
     * @param gup update payload
     */
    public void update(GameUpdatePayload gup) {
        GameUpdater.update(this, gup, currentTurn);
    }
}