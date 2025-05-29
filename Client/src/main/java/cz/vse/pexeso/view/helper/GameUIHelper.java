package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public final class GameUIHelper {
    private GameUIHelper() {
    }

    public static void setupGameBoard(GridPane gameBoardGridPane, Board gameBoard) {
        gameBoardGridPane.add(gameBoard, 1, 1);
    }

    public static void setupScoreboard(TableView<LobbyPlayer> scoreboardTable, TableColumn<LobbyPlayer, String> playerColumn, TableColumn<LobbyPlayer, Integer> scoreColumn, GameModel gameModel) {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreboardTable.setRowFactory(tableView -> {
            TableRow<LobbyPlayer> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && gameModel.getPlayerColors() != null) {
                    if (gameModel.getPlayerColors().get(newItem.getPlayerId()) != null) {
                        row.setStyle(gameModel.getPlayerColors().get(newItem.getPlayerId()));
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }

    public static void setupOnClick(GameModel gameModel) {
        for (GameCard gameCard : gameModel.getGameBoard().getGameCardList()) {
            gameCard.setOnMouseClicked(mouseEvent -> gameModel.attemptRevealCard(gameCard));
        }
    }
}
