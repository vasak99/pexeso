package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.LobbyResultHandler;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.view.cell.GameRoomActionCell;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public final class LobbyUIHelper {

    private LobbyUIHelper() {
    }

    public static void setup(TableView<GameRoom> gameRoomTable,
                             TableColumn<GameRoom, String> roomStatusColumn,
                             TableColumn<GameRoom, String> gameNameColumn,
                             TableColumn<GameRoom, String> hostNameColumn,
                             TableColumn<GameRoom, String> boardSizeColumn,
                             TableColumn<GameRoom, Integer> roomCapacityColumn,
                             TableColumn<GameRoom, Void> actionsColumn,
                             LobbyController controller,
                             LobbyModel lobbyModel,
                             LobbyResultHandler resultHandler) {
        initializeGameRoomTable(gameRoomTable, roomStatusColumn, gameNameColumn, hostNameColumn, boardSizeColumn, roomCapacityColumn, actionsColumn, controller, lobbyModel);
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
                                                TableColumn<GameRoom, String> gameNameColumn,
                                                TableColumn<GameRoom, String> hostNameColumn,
                                                TableColumn<GameRoom, String> boardSizeColumn,
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

        gameNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        hostNameColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));

        boardSizeColumn.setCellValueFactory(cellData -> {
            GameRoom room = cellData.getValue();
            if (room == null) {
                return new ReadOnlyObjectWrapper<>(null);
            }
            String boardSize = GameRoom.BoardSize.returnDisplayText(room.getCardCount());
            return new ReadOnlyStringWrapper(boardSize);
        });

        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        actionsColumn.setCellFactory(col -> new GameRoomActionCell(controller, lobbyModel));

        gameRoomTable.setRowFactory(tableView -> {
            TableRow<GameRoom> row = new TableRow<>();

            //Highlight user's room
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && lobbyModel.getCurrentGameRoomId() != null) {
                    if (newItem.getGameId().equals(lobbyModel.getCurrentGameRoomId())) {
                        row.setStyle(UIConstants.GRAY_COLOR);
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
