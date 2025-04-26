package cz.vse.pexeso.view;

import cz.vse.pexeso.controller.LobbyController;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameStatus;
import cz.vse.pexeso.service.AppServices;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class ActionCell extends TableCell<GameRoom, Void> {
    private final Button joinButton = new Button("Join");
    private final Button editButton = new Button("Edit");
    private final Button deleteButton = new Button("Delete");
    private final HBox actionBox = new HBox(5, joinButton, editButton, deleteButton);

    public ActionCell(LobbyController controller) {
        joinButton.setStyle("-fx-background-color: #d0ffc0;");
        editButton.setStyle("-fx-background-color: #fff4c0;");
        deleteButton.setStyle("-fx-background-color: #ffc0c0;");


        joinButton.setOnAction(event -> {
            GameRoom gameRoom = getTableView().getItems().get(getIndex());
            controller.joinGameRoom(gameRoom);
        });

        editButton.setOnAction(event -> {
            GameRoom gameRoom = getTableView().getItems().get(getIndex());
            controller.editGameRoom(gameRoom);
        });

        deleteButton.setOnAction(event -> {
            GameRoom gameRoom = getTableView().getItems().get(getIndex());
            controller.deleteGameRoom(gameRoom);
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
        ClientSession clientSession = AppServices.getClientSession();

        boolean isHost = clientSession.getPlayerId().equals(gameRoom.getHost());
        boolean isJoinable = gameRoom.getStatus() == GameStatus.WAITING_FOR_PLAYERS;
        boolean alreadyJoined = clientSession.getCurrentGameRoom() != null &&
                clientSession.getCurrentGameRoom().getGameId().equals(gameRoom.getGameId());

        joinButton.setVisible(isJoinable && !alreadyJoined);
        editButton.setVisible(isHost);
        deleteButton.setVisible(isHost);

        setGraphic(actionBox);
    }
}
