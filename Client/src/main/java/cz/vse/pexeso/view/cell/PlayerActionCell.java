package cz.vse.pexeso.view.cell;

import cz.vse.pexeso.controller.GameRoomManagerController;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameRoomModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TableCell for displaying a Kick button next to each LobbyPlayer in the game room manager.
 *
 * @author kott10
 * @version June 2025
 */
public class PlayerActionCell extends TableCell<LobbyPlayer, Void> {
    private static final Logger log = LoggerFactory.getLogger(PlayerActionCell.class);

    private final GameRoomModel gameRoomModel;
    private final Button kickButton = new Button("Kick");
    private final HBox actionBox = new HBox(5, kickButton);

    /**
     * Constructs a PlayerActionCell with a kick button.
     *
     * @param controller    GameRoomManagerController handling kicks
     * @param gameRoomModel Model providing current host ID
     */
    public PlayerActionCell(GameRoomManagerController controller, GameRoomModel gameRoomModel) {
        this.gameRoomModel = gameRoomModel;

        actionBox.setAlignment(Pos.CENTER);

        kickButton.setOnAction(event -> {
            LobbyPlayer lobbyPlayer = getTableView().getItems().get(getIndex());
            if (lobbyPlayer == null || lobbyPlayer.getPlayerId() == null || lobbyPlayer.getUsername() == null) {
                return;
            }
            controller.kickPlayer(lobbyPlayer);
            log.debug("Kick button pressed for playerId={}", lobbyPlayer.getPlayerId());
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

        Long hostId = gameRoomModel.getCurrentGameHostId();
        Long playerId = lobbyPlayer.getPlayerId();
        boolean isHost = (hostId != null && hostId.equals(playerId));

        kickButton.setVisible(!isHost);
        setGraphic(actionBox);
    }
}