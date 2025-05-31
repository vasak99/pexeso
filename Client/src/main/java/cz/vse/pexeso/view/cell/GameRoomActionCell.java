package cz.vse.pexeso.view.cell;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.util.Strings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class GameRoomActionCell extends TableCell<GameRoom, Void> {

    private final LobbyModel lobbyModel;
    private final Button joinButton = new Button(Strings.JOIN);
    private final Button leaveButton = new Button(Strings.LEAVE);
    private final HBox actionBox = new HBox(5, joinButton, leaveButton);

    public GameRoomActionCell(LobbyController controller, LobbyModel lobbyModel) {
        this.lobbyModel = lobbyModel;

        actionBox.setAlignment(Pos.CENTER);

        joinButton.setStyle(UIConstants.GREEN_COLOR);
        leaveButton.setStyle(UIConstants.RED_COLOR);

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
        boolean isInAGame = lobbyModel.isInARoom();

        joinButton.setVisible(!isInAGame && isJoinable);
        leaveButton.setVisible(!isHost && alreadyJoined);

        setGraphic(actionBox);
    }

}
