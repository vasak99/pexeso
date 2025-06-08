package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.navigation.UIConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Helper for constructing the game result UI. Builds a VBox of HBoxes showing rank, name, and score.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameResultHelper {
    private static final Logger log = LoggerFactory.getLogger(GameResultHelper.class);

    private GameResultHelper() {
    }

    /**
     * Populates the VBox with rows representing each player's rank, username, and score.
     *
     * @param resultVBox container for result rows
     * @param resultList list of LobbyPlayer results in descending score order
     */
    public static void setupResult(VBox resultVBox, List<LobbyPlayer> resultList) {
        resultVBox.getChildren().clear();
        int rank = 1;

        for (int i = 0; i < resultList.size(); i++) {
            //If players have the same score, they share the same rank
            if (i != 0 && resultList.get(i).getScore() != resultList.get(i - 1).getScore()) {
                rank++;
            }

            HBox row = getRow(resultList, rank, i);
            resultVBox.getChildren().add(row);
            log.debug("Added result row for player={} with score={}, rank={}",
                    resultList.get(i).getUsername(), resultList.get(i).getScore(), rank);
        }
    }

    /**
     * Creates an HBox for a single player's result row, including rank, name, and score.
     * Applies special styling for ranks 1 (gold), 2 (silver), and 3 (bronze).
     *
     * @param resultList list of LobbyPlayer results
     * @param rank       rank of the current player
     * @param i          index in resultList
     * @return a configured HBox
     */
    private static HBox getRow(List<LobbyPlayer> resultList, int rank, int i) {
        LobbyPlayer player = resultList.get(i);

        Label rankLabel = new Label(rank + ".");
        Label nameLabel = new Label(player.getUsername());
        Label scoreLabel = new Label(String.valueOf(player.getScore()));

        HBox row = new HBox(30, rankLabel, nameLabel, scoreLabel);
        row.setPadding(new Insets(8));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-font-size: 16px;");

        switch (rank) {
            case 1 -> row.setStyle(row.getStyle() + UIConstants.GOLD_COLOR + "-fx-font-weight: bold;");
            case 2 -> row.setStyle(row.getStyle() + UIConstants.SILVER_COLOR + "-fx-font-weight: bold;");
            case 3 -> row.setStyle(row.getStyle() + UIConstants.BRONZE_COLOR + "-fx-font-weight: bold;");
            default -> {
            }
        }
        return row;
    }
}