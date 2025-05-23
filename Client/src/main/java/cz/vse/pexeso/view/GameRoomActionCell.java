package cz.vse.pexeso.view;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class GameRoomActionCell extends TableCell<GameRoom, Void> {

    private final LobbyModel lobbyModel;
    private final Button joinButton = new Button("Join");
    private final Button leaveButton = new Button("Leave");
    private final HBox actionBox = new HBox(5, joinButton, leaveButton);

    public GameRoomActionCell(LobbyController controller, LobbyModel lobbyModel) {
        this.lobbyModel = lobbyModel;

        actionBox.setAlignment(Pos.CENTER);

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
        boolean isInAGame = lobbyModel.isInARoom();

        joinButton.setVisible(!isInAGame && isJoinable);
        leaveButton.setVisible(!isHost && alreadyJoined);

        setGraphic(actionBox);
    }

}
