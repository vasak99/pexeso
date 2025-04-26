package cz.vse.pexeso.service;

import cz.vse.pexeso.controller.GameRoomFormController;
import cz.vse.pexeso.controller.GameRoomFormMode;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameStatus;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;

public class GameRoomService {

    public boolean join(GameRoom gameRoom) {
        if (gameRoom.getStatus() == GameStatus.WAITING_FOR_PLAYERS) {
            String message = MessageBuilder.buildJoinGameMessage(gameRoom);
            AppServices.getConnection().sendMessage(message);
            AppServices.getClientSession().setCurrentGameRoom(gameRoom);
            return true;
        } else {
            SceneManager.showErrorAlert("Game room is not available");
            return false;
        }
    }

    public void edit(GameRoom gameRoom) {
        if (AppServices.getClientSession().getPlayerId().equals(gameRoom.getHost())) {
            GameRoomFormController controller = SceneManager.openWindow(UIConstants.GAME_ROOM_FORM_FXML, "Edit game room");
            if (controller != null) {
                controller.setMode(GameRoomFormMode.EDIT);
            }
        } else {
            SceneManager.showErrorAlert("Only the host can edit the game room");
        }
    }

    public void delete(GameRoom gameRoom) {
        if (AppServices.getClientSession().getPlayerId().equals(gameRoom.getHost())) {
            String message = MessageBuilder.buildDeleteGameMessage(gameRoom);
            AppServices.getConnection().sendMessage(message);
            // back to default port
        } else {
            SceneManager.showErrorAlert("Only the host can delete the game room");
        }
    }
}
