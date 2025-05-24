package cz.vse.pexeso.view;

import javafx.scene.layout.GridPane;

public final class GameUIHelper {
    private GameUIHelper() {
    }

    public static void setupGameBoard(GridPane gameBoardGridPane, Board gameBoard) {
        gameBoardGridPane.add(gameBoard, 1, 1);
    }

    public static void setTurn(boolean playersTurn, Board board) {
        for (GameCard gameCard : board.getGameCardList()) {
            gameCard.setDisable(!playersTurn || gameCard.isCompleted());
        }
    }
}
