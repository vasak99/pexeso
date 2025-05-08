package cz.vse.pexeso.view;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.controller.GameRoomManagerController;
import cz.vse.pexeso.model.BoardSize;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.PlayerStatus;
import cz.vse.pexeso.model.model.GameRoomModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public final class GameRoomUIHelper {
    private GameRoomUIHelper() {
    }

    public static void initializePlayerTable(TableView<LobbyPlayer> playerTable,
                                             TableColumn<LobbyPlayer, String> playerNameColumn,
                                             TableColumn<LobbyPlayer, String> playerStatusColumn,
                                             TableColumn<LobbyPlayer, Void> actionColumn,
                                             GameRoomManagerController controller,
                                             GameRoomModel gameRoomModel
    ) {
        playerTable.setPlaceholder(new Label("No other players in this game room"));

        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        playerStatusColumn.setCellValueFactory(cellData -> {
            LobbyPlayer player = cellData.getValue();
            if (player == null) {
                return new ReadOnlyStringWrapper("");
            }
            PlayerStatus status = player.getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button kickButton = new Button("Kick");
            private final HBox actionBox = new HBox(5, kickButton);

            {
                //kickButton.setStyle("");

                kickButton.setOnAction(event -> {
                    LobbyPlayer lobbyPlayer = getTableView().getItems().get(getIndex());
                    controller.kickPlayer(lobbyPlayer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                LobbyPlayer lobbyPlayer = getTableView().getItems().get(getIndex());
                if (lobbyPlayer == null) {
                    setGraphic(null);
                    return;
                }
                boolean isHost = false;
                GameRoom currentGameRoom = GameRoom.findById(lobbyPlayer.getCurrentGameId());

                if (currentGameRoom != null) {
                    isHost = lobbyPlayer.getPlayerId() == currentGameRoom.getHostId();
                }

                kickButton.setVisible(!isHost);

                setGraphic(actionBox);
            }
        });

        playerTable.setRowFactory(tableView -> {
            TableRow<LobbyPlayer> row = new TableRow<>();

            //Highlight host
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && gameRoomModel.getCurrentGameHostId() != null) {
                    if (newItem.getPlayerId() == gameRoomModel.getCurrentGameHostId()) {
                        row.setStyle("-fx-background-color: #e6e6e6;");
                    } else {
                        row.setStyle("");
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }

    public static void setupCapacitySlider(Slider capacitySlider) {
        capacitySlider.setMin(Variables.MIN_PLAYERS);
        capacitySlider.setMax(Variables.MAX_PLAYERS);
    }

    public static void setupBoardSizeChoiceBox(ChoiceBox<BoardSize> boardSizeChoiceBox) {
        ObservableList<BoardSize> sizes = FXCollections.observableArrayList();
        sizes.setAll(BoardSize.SMALL, BoardSize.MEDIUM, BoardSize.LARGE);
        boardSizeChoiceBox.setItems(sizes);
    }
}
