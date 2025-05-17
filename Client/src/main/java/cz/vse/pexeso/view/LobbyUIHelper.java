package cz.vse.pexeso.view;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.LobbyResultHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public final class LobbyUIHelper {

    private LobbyUIHelper() {
    }

    public static void setup(TableView<GameRoom> gameRoomTable,
                             TableColumn<GameRoom, String> roomStatusColumn,
                             TableColumn<GameRoom, String> gameIdColumn,
                             TableColumn<GameRoom, Long> hostColumn,
                             TableColumn<GameRoom, GameRoom.BoardSize> boardSizeColumn,
                             TableColumn<GameRoom, Integer> roomCapacityColumn,
                             TableColumn<GameRoom, Void> actionsColumn,
                             LobbyController controller,
                             LobbyModel lobbyModel,
                             LobbyResultHandler resultHandler) {
        initializeGameRoomTable(gameRoomTable, roomStatusColumn, gameIdColumn, hostColumn, boardSizeColumn, roomCapacityColumn, actionsColumn, controller, lobbyModel);
        setupWindowCloseEvent(gameRoomTable, resultHandler);
    }

    private static void setupWindowCloseEvent(TableView<GameRoom> gameRoomTable, LobbyResultHandler resultHandler) {
        Platform.runLater(() -> {
            Stage stage = (Stage) gameRoomTable.getScene().getWindow();
            stage.setOnCloseRequest(event -> resultHandler.finalUnregister());
        });
    }

    private static void initializeGameRoomTable(TableView<GameRoom> gameRoomTable,
                                                TableColumn<GameRoom, String> roomStatusColumn,
                                                TableColumn<GameRoom, String> gameIdColumn,
                                                TableColumn<GameRoom, Long> hostColumn,
                                                TableColumn<GameRoom, GameRoom.BoardSize> boardSizeColumn,
                                                TableColumn<GameRoom, Integer> roomCapacityColumn,
                                                TableColumn<GameRoom, Void> actionsColumn,
                                                LobbyController controller,
                                                LobbyModel lobbyModel) {
        gameRoomTable.setPlaceholder(new Label("No game room has been created yet"));

        roomStatusColumn.setCellValueFactory(cellData -> {
            GameRoom room = cellData.getValue();
            if (room == null) {
                return new ReadOnlyStringWrapper("");
            }
            GameRoom.GameStatus status = room.getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });

        gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));

        hostColumn.setCellValueFactory(new PropertyValueFactory<>("hostId"));

        boardSizeColumn.setCellValueFactory(cellData -> {
            GameRoom room = cellData.getValue();
            if (room == null) {
                return new ReadOnlyObjectWrapper<>(null);
            }
            GameRoom.BoardSize boardSize = GameRoom.BoardSize.fromValue(room.getCardCount());
            return new ReadOnlyObjectWrapper<>(boardSize);
        });
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button joinButton = new Button("Join");
            private final Button leaveButton = new Button("Leave");
            private final HBox actionBox = new HBox(5, joinButton, leaveButton);

            {
                joinButton.setStyle("-fx-background-color: #d0ffc0;");
                leaveButton.setStyle("-fx-background-color: #ffc0c0;");

                joinButton.setOnAction(event -> {
                    GameRoom gameRoom = getTableView().getItems().get(getIndex());
                    controller.joinGameRoom(gameRoom);
                });

                leaveButton.setOnAction(event -> controller.leaveGameRoom());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                GameRoom gameRoom = getTableView().getItems().get(getIndex());
                if (gameRoom == null) {
                    setGraphic(null);
                    return;
                }

                boolean isHost = lobbyModel.isHost(gameRoom);
                boolean isJoinable = gameRoom.getStatus() == GameRoom.GameStatus.WAITING_FOR_PLAYERS;
                boolean alreadyJoined = lobbyModel.getCurrentGameRoomId() != null &&
                        lobbyModel.getCurrentGameRoomId().equals(gameRoom.getGameId());

                joinButton.setVisible(isJoinable && !alreadyJoined);
                leaveButton.setVisible(!isHost && alreadyJoined);

                setGraphic(actionBox);
            }
        });

        gameRoomTable.setRowFactory(tableView -> {
            TableRow<GameRoom> row = new TableRow<>();

            //Highlight user's room
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && lobbyModel.getCurrentGameRoomId() != null) {
                    if (newItem.getGameId().equals(lobbyModel.getCurrentGameRoomId())) {
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
}
