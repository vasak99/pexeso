package cz.vse.pexeso.view;

import cz.vse.pexeso.controller.GameRoomWindowController;
import cz.vse.pexeso.model.LobbyPlayer;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class PlayerActionCell extends TableCell<LobbyPlayer, Void> {
    private final Button kickButton = new Button("Kick");
    private final HBox actionBox = new HBox(5, kickButton);

    public PlayerActionCell(GameRoomWindowController controller) {
        //kickButton.setStyle("");

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

        setGraphic(actionBox);
    }
}
