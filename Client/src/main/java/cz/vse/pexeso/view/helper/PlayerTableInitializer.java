package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.controller.GameRoomManagerController;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.view.cell.PlayerActionCell;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the player table within a game room manager: sets up columns for name, status, and kick action.
 *
 * @author kott10
 * @version June 2025
 */
public final class PlayerTableInitializer {
    private static final Logger log = LoggerFactory.getLogger(PlayerTableInitializer.class);

    private PlayerTableInitializer() {
    }

    /**
     * Configures the playerTable with columns for username and status, and a kick action column.
     *
     * @param playerTable        TableView for LobbyPlayer entries
     * @param playerNameColumn   Column showing usernames
     * @param playerStatusColumn Column showing ready/not-ready status
     * @param actionColumn       Column for kick buttons
     * @param controller         Controller handling kick actions
     * @param gameRoomModel      Model providing host ID
     */
    public static void initialize(
            TableView<LobbyPlayer> playerTable,
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
            return new ReadOnlyStringWrapper(player.getStatus().getValue());
        });

        actionColumn.setCellFactory(col -> new PlayerActionCell(controller, gameRoomModel));

        log.debug("Initialized player table with name, status, and action columns");
    }
}