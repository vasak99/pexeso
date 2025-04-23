package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.service.AppServices;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NewRoomFormController {
    @FXML
    private Label warningLabel;
    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<String> boardSizeChoiceBox;
    @FXML
    private ChoiceBox<String> capacityChoiceBox;

    @FXML
    private void initialize() {

    }

    @FXML
    private void clickCreateButton() {
        if (nameField.getText().isEmpty() || boardSizeChoiceBox.getValue().isEmpty() || capacityChoiceBox.getValue().isEmpty()) {
            warningLabel.setText("Please fill in all fields.");
        } else {
            GameRoom gameRoom = new GameRoom();
            gameRoom.setHost(AppServices.getClientSession().getUser().username());
            gameRoom.setName(nameField.getText());
            gameRoom.setBoardSize(boardSizeChoiceBox.getValue());
            gameRoom.setMaxPlayers(Integer.parseInt(capacityChoiceBox.getValue()));

            //save the room, send it to server
            //close window
        }
    }
}
