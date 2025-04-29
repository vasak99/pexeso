package cz.vse.pexeso.view;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameStatus;
import cz.vse.pexeso.service.AppServices;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class GameRoomActionCell extends TableCell<GameRoom, Void> {
    private final Button joinButton = new Button("Join");
    private final Button leaveButton = new Button("Leave");
    private final HBox actionBox = new HBox(5, joinButton, leaveButton);

    public GameRoomActionCell(LobbyController controller) {
        joinButton.setStyle("-fx-background-color: #d0ffc0;");
        leaveButton.setStyle("-fx-background-color: #ffc0c0;");

        joinButton.setOnAction(event -> {
            GameRoom gameRoom = getTableView().getItems().get(getIndex());
            controller.joinGameRoom(gameRoom);
        });

        leaveButton.setOnAction(event -> {
            GameRoom gameRoom = getTableView().getItems().get(getIndex());
            controller.leaveGameRoom(gameRoom);
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

        ClientSession clientSession = AppServices.getInstance().getClientSession();

        boolean isHost = clientSession.getPlayerId() == gameRoom.getHostId();
        boolean isJoinable = gameRoom.getStatus() == GameStatus.WAITING_FOR_PLAYERS;
        boolean alreadyJoined = clientSession.getCurrentGameRoom() != null &&
                clientSession.getCurrentGameRoom().getGameId().equals(gameRoom.getGameId());

        joinButton.setVisible(isJoinable && !alreadyJoined);
        leaveButton.setVisible(!isHost && alreadyJoined);

        setGraphic(actionBox);
    }
}
