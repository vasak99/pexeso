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

public final class GameUIHelper {
    private GameUIHelper() {
    }

    public static void setup(GridPane mainGridPane,
                             GameModel gameModel,
                             TableView<LobbyPlayer> scoreboardTable,
                             TableColumn<LobbyPlayer, String> playerColumn,
                             TableColumn<LobbyPlayer, Integer> scoreColumn,
                             Label titleLabel) {
        setupGameBoard(mainGridPane, gameModel.getGameBoard());
        setupOnClick(gameModel);
        setupScoreboard(scoreboardTable, playerColumn, scoreColumn, gameModel);
        titleLabel.setText(gameModel.getSession().getPlayerName() + " " + gameModel.getPlayerId());
    }

    private static void setupGameBoard(GridPane gameBoardGridPane, Board gameBoard) {
        gameBoardGridPane.add(gameBoard, 1, 0);
    }

    private static void setupScoreboard(TableView<LobbyPlayer> scoreboardTable, TableColumn<LobbyPlayer, String> playerColumn, TableColumn<LobbyPlayer, Integer> scoreColumn, GameModel gameModel) {
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

        scoreboardTable.setMaxHeight(30 + gameModel.getPlayers().size() * 24);
    }

    private static void setupOnClick(GameModel gameModel) {
        for (GameCard gameCard : gameModel.getGameBoard().getGameCardList()) {
            gameCard.setOnMouseClicked(mouseEvent -> gameModel.attemptRevealCard(gameCard));
        }
    }

    public static void addPlayerRow(VBox vBox, LobbyPlayer lobbyPlayer) {
        int index = vBox.getChildren().size();
        Label rank = new Label((index + 1) + ".");
        Label name = new Label(lobbyPlayer.getUsername());
        Label score = new Label(String.valueOf(lobbyPlayer.getScore()));

        HBox row = new HBox(30, rank, name, score);
        row.setPadding(new Insets(8));
        row.setStyle("-fx-font-size: 16px; -fx-background-radius: 8;");
        row.setAlignment(Pos.CENTER_LEFT);

        switch (index) {
            case 0 -> row.setStyle(row.getStyle() + UIConstants.GOLD_COLOR + "-fx-font-weight: bold;");
            case 1 -> row.setStyle(row.getStyle() + UIConstants.SILVER_COLOR + "-fx-font-weight: bold;");
            case 2 -> row.setStyle(row.getStyle() + UIConstants.BRONZE_COLOR + "-fx-font-weight: bold;");
        }

        vBox.getChildren().add(row);
    }
}
