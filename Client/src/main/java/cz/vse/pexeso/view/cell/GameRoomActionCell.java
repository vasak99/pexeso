package cz.vse.pexeso.view.cell;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.navigation.UIConstants;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TableCell for displaying Join and Leave buttons in the lobby’s GameRoom table.
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoomActionCell extends TableCell<GameRoom, Void> {
    private static final Logger log = LoggerFactory.getLogger(GameRoomActionCell.class);

    private final LobbyModel lobbyModel;
    private final Button joinButton = new Button("Join");
    private final Button leaveButton = new Button("Leave");
    private final HBox actionBox = new HBox(5, joinButton, leaveButton);

    /**
     * Constructs the action cell with buttons and handlers.
     *
     * @param controller LobbyController handling join/leave
     * @param lobbyModel Model providing session and room info
     */
    public GameRoomActionCell(LobbyController controller, LobbyModel lobbyModel) {
        this.lobbyModel = lobbyModel;

        actionBox.setAlignment(Pos.CENTER);
        joinButton.setStyle(UIConstants.GREEN_COLOR);
        leaveButton.setStyle(UIConstants.RED_COLOR);

        joinButton.setOnAction(event -> {
            GameRoom gameRoom = getTableView().getItems().get(getIndex());
            if (gameRoom == null || gameRoom.getGameId() == null || gameRoom.getGameId().isEmpty()) {
                return;
            }
            controller.joinGameRoom(gameRoom.getGameId());
            log.debug("Join button pressed for gameId={}", gameRoom.getGameId());
        });

        leaveButton.setOnAction(event -> {
            if (lobbyModel.isInRoom()) {
                controller.leaveGameRoom();
                log.debug("Leave button pressed");
            }
        });
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

        Long playerId = lobbyModel.getPlayerId();
        String currentRoomId = lobbyModel.getCurrentGameRoomId();

        boolean isHost = (playerId != null && gameRoom.isHost(playerId));
        boolean isJoinable = gameRoom.getStatus() == GameRoom.GameStatus.WAITING_FOR_PLAYERS;
        boolean alreadyJoined = currentRoomId != null && currentRoomId.equals(gameRoom.getGameId());
        boolean isInAGame = lobbyModel.isInRoom();

        joinButton.setVisible(!isInAGame && isJoinable);
        leaveButton.setVisible(!isHost && alreadyJoined);

        setGraphic(actionBox);
    }
}