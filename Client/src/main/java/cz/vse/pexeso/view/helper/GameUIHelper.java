package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for configuring the in-game UI: sizing the window based on board dimensions,
 * setting up card click handlers, and updating turn labels. Also configures result window close behavior.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameUIHelper {
    private static final Logger log = LoggerFactory.getLogger(GameUIHelper.class);

    private GameUIHelper() {
    }

    /**
     * Adjusts the window size based on the number of rows and columns in the game board,
     * and the scoreboard width.
     *
     * @param mainGridPane    container for the board and scoreboard
     * @param scoreboardTable table displaying player scores
     * @param gameModel       model containing the board
     */
    public static void setWindowSize(
            GridPane mainGridPane,
            TableView<?> scoreboardTable,
            GameModel gameModel
    ) {
        Board board = gameModel.getGameBoard();

        int rows = board.getNumberOfRows();
        int cols = board.getNumberOfColumns();
        double cardSize = GameCard.size;
        double gap = Board.gap;

        mainGridPane.setPrefHeight(rows * (gap + cardSize));
        mainGridPane.setPrefWidth(scoreboardTable.getMaxWidth() + cols * (gap + cardSize));
        log.debug("Set window size for {}x{} board", rows, cols);
    }

    /**
     * Updates the turn label text to indicate whether it is the player or opponent turn.
     *
     * @param turnLabel label showing turn status
     * @param gameModel model containing turn information
     */
    public static void updateTurnLabel(Label turnLabel, GameModel gameModel) {
        if (gameModel.isPlayersTurn()) {
            turnLabel.setText("Your turn");
        } else {
            turnLabel.setText("Opponent's turn");
        }
        log.debug("Updated turn label to '{}'", turnLabel.getText());
    }

    /**
     * Registers mouse-click handlers on each card in the board to call attemptRevealCard on click.
     *
     * @param gameModel model containing the board and click logic
     */
    public static void setupOnClick(GameModel gameModel) {
        for (GameCard gameCard : gameModel.getGameBoard().getGameCardList()) {
            gameCard.setOnMouseClicked(mouseEvent -> {
                gameModel.attemptRevealCard(gameCard);
                log.debug("Card clicked at ({}, {})", gameCard.getRow(), gameCard.getColumn());
            });
        }
        log.debug("Set up on-click handlers for all GameCards");
    }
}