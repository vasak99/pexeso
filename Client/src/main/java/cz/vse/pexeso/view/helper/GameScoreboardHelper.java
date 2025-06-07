package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes and updates the in-game scoreboard table.
 * Sets up cell factories, row coloring based on player colors, and sorts by score descending.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameScoreboardHelper {
    private static final Logger log = LoggerFactory.getLogger(GameScoreboardHelper.class);

    private GameScoreboardHelper() {
        // Prevent instantiation
    }

    /**
     * Configures the scoreboard table with columns for player names and scores,
     * and applies row styling based on player colors.
     *
     * @param scoreboardTable table to display scores
     * @param playerColumn    column to show player usernames
     * @param scoreColumn     column to show player scores
     * @param gameModel       model providing player list and color mapping
     */
    public static void initialize(
            TableView<LobbyPlayer> scoreboardTable,
            TableColumn<LobbyPlayer, String> playerColumn,
            TableColumn<LobbyPlayer, Integer> scoreColumn,
            GameModel gameModel
    ) {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreboardTable.setRowFactory(tv -> {
            TableRow<LobbyPlayer> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && gameModel.getPlayerColors() != null) {
                    String style = gameModel.getPlayerColors().get(newItem.getPlayerId());
                    row.setStyle(style != null ? style : "");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        int numPlayers = gameModel.getPlayers().size();
        scoreboardTable.setMaxHeight(30 + numPlayers * 24);
        log.debug("Initialized scoreboard with {} players", numPlayers);
    }

    /**
     * Updates the scoreboard’s items to reflect the current result list and refreshes the view.
     *
     * @param scoreboardTable table to update
     * @param gameModel       model providing the result list
     */
    public static void updateScoreboard(
            TableView<LobbyPlayer> scoreboardTable,
            GameModel gameModel
    ) {
        scoreboardTable.setItems(gameModel.getResultList());
        scoreboardTable.refresh();
        log.debug("Scoreboard updated with result list");
    }

    /**
     * Sorts the scoreboard by the score column in descending order.
     *
     * @param scoreboardTable table to sort
     * @param scoreColumn     column used for sorting
     */
    public static void sortScoreboard(
            TableView<LobbyPlayer> scoreboardTable,
            TableColumn<LobbyPlayer, Integer> scoreColumn
    ) {
        scoreColumn.setSortType(TableColumn.SortType.DESCENDING);
        scoreboardTable.getSortOrder().clear();
        scoreboardTable.getSortOrder().add(scoreColumn);
        scoreboardTable.sort();
        log.debug("Scoreboard sorted by score descending");
    }
}