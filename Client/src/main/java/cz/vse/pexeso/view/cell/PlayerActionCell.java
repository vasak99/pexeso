package cz.vse.pexeso.view.cell;

import cz.vse.pexeso.controller.GameRoomManagerController;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.util.Strings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class PlayerActionCell extends TableCell<LobbyPlayer, Void> {
    private final GameRoomModel gameRoomModel;
    private final Button kickButton = new Button(Strings.KICK);
    private final HBox actionBox = new HBox(5, kickButton);

    public PlayerActionCell(GameRoomManagerController controller, GameRoomModel gameRoomModel) {
        this.gameRoomModel = gameRoomModel;

        actionBox.setAlignment(Pos.CENTER);

        kickButton.setOnAction(event -> {
            LobbyPlayer lobbyPlayer = getTableView().getItems().get(getIndex());
            controller.kickPlayer(lobbyPlayer);
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
        boolean isHost = gameRoomModel.getCurrentGameHostId() == lobbyPlayer.getPlayerId();

        kickButton.setVisible(!isHost);

        setGraphic(actionBox);
    }
}
