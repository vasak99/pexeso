package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.view.cell.GameRoomActionCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the lobby’s game room table by setting up cell factories for displaying
 * status, name, host, board size, capacity, and action buttons. Applies row highlighting
 * based on whether the room matches the current session’s room ID.
 *
 * @author kott10
 * @version June 2025
 */
public final class LobbyTableInitializer {
    private static final Logger log = LoggerFactory.getLogger(LobbyTableInitializer.class);

    private LobbyTableInitializer() {
    }

    /**
     * Configures all columns of the game room table and sets up row highlighting.
     *
     * @param gameRoomTable      table to configure
     * @param roomStatusColumn   column showing room status
     * @param gameNameColumn     column showing room name
     * @param hostNameColumn     column showing host username
     * @param boardSizeColumn    column showing board-size text
     * @param roomCapacityColumn column showing capacity
     * @param actionsColumn      column for action buttons
     * @param controller         controller handling actions
     * @param lobbyModel         model providing current room ID
     */
    public static void initialize(
            TableView<GameRoom> gameRoomTable,
            TableColumn<GameRoom, String> roomStatusColumn,
            TableColumn<GameRoom, String> gameNameColumn,
            TableColumn<GameRoom, String> hostNameColumn,
            TableColumn<GameRoom, String> boardSizeColumn,
            TableColumn<GameRoom, Integer> roomCapacityColumn,
            TableColumn<GameRoom, Void> actionsColumn,
            cz.vse.pexeso.controller.LobbyController controller,
            LobbyModel lobbyModel
    ) {
        gameRoomTable.setPlaceholder(new Label("No game room has been created yet"));

        roomStatusColumn.setCellValueFactory(cellData -> {
            GameRoom room = cellData.getValue();
            if (room == null) {
                return new ReadOnlyStringWrapper("");
            }
            String statusText = room.getStatus().getValue();
            return new ReadOnlyStringWrapper(statusText);
        });

        gameNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        hostNameColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));

        boardSizeColumn.setCellValueFactory(cellData -> {
            GameRoom room = cellData.getValue();
            if (room == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            String boardSizeText = GameRoom.BoardSize.returnDisplayText(room.getCardCount());
            return new ReadOnlyStringWrapper(boardSizeText);
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

        log.debug("Initialized lobby table columns and row highlighting");
    }
}