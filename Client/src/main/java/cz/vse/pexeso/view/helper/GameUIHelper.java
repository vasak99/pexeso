package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public final class GameUIHelper {
    private GameUIHelper() {
    }

    public static void setup(GridPane mainGridPane,
                             GameModel gameModel,
                             TableView<LobbyPlayer> scoreboardTable,
                             TableColumn<LobbyPlayer, String> playerColumn,
                             TableColumn<LobbyPlayer, Integer> scoreColumn) {
        setupGameBoard(mainGridPane, gameModel.getGameBoard());
        setupOnClick(gameModel);
        setupScoreboard(scoreboardTable, playerColumn, scoreColumn, gameModel);
    }

    private static void setupGameBoard(GridPane mainGridPane, Board gameBoard) {
        mainGridPane.add(gameBoard, 1, 0);
    }

    public static void setMainGridPaneSize(GridPane mainGridPane, Board gameBoard, TableView<LobbyPlayer> scoreboardTable) {
        mainGridPane.setPrefHeight(gameBoard.getNumberOfRows() * (Board.gap + GameCard.size));
        mainGridPane.setPrefWidth(scoreboardTable.getMaxWidth() + gameBoard.getNumberOfColumns() * (Board.gap + GameCard.size));
    }

    private static void setupScoreboard(TableView<LobbyPlayer> scoreboardTable, TableColumn<LobbyPlayer, String> playerColumn, TableColumn<LobbyPlayer, Integer> scoreColumn, GameModel gameModel) {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreboardTable.setRowFactory(tableView -> {
            TableRow<LobbyPlayer> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && gameModel.getRoom() != null && gameModel.getPlayerColors() != null) {
                    if (gameModel.getPlayerColors().get(newItem.getPlayerId()) != null) {
                        row.setStyle(gameModel.getPlayerColors().get(newItem.getPlayerId()));
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        scoreboardTable.setMaxHeight(30 + gameModel.getPlayers().size() * 24);
    }

    private static void setupOnClick(GameModel gameModel) {
        for (GameCard gameCard : gameModel.getGameBoard().getGameCardList()) {
            gameCard.setOnMouseClicked(mouseEvent -> gameModel.attemptRevealCard(gameCard));
        }
    }

    public static void setupResult(VBox vBox, List<LobbyPlayer> resultList) {
        int rank = 1;
        for (int i = 0; i < resultList.size(); i++) {
            if (i != 0 && resultList.get(i).getScore() != resultList.get(i - 1).getScore()) {
                rank++;
            }

            Label rankLabel = new Label(rank + ".");
            Label name = new Label(resultList.get(i).getUsername());
            Label score = new Label(String.valueOf(resultList.get(i).getScore()));

            HBox row = new HBox(30, rankLabel, name, score);
            row.setPadding(new Insets(8));
            row.setStyle("-fx-font-size: 16px;");
            row.setAlignment(Pos.CENTER_LEFT);

            switch (rank) {
                case 1 -> row.setStyle(row.getStyle() + UIConstants.GOLD_COLOR + "-fx-font-weight: bold;");
                case 2 -> row.setStyle(row.getStyle() + UIConstants.SILVER_COLOR + "-fx-font-weight: bold;");
                case 3 -> row.setStyle(row.getStyle() + UIConstants.BRONZE_COLOR + "-fx-font-weight: bold;");
            }

            vBox.getChildren().add(row);
        }
    }
}
