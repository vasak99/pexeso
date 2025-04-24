package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.service.AppServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class NewRoomFormController {

    @FXML
    private Slider cardCountSlider;
    @FXML
    private Slider capacitySlider;
    @FXML
    private Label warningLabel;

    @FXML
    private void initialize() {
        cardCountSlider.setMin(Variables.MIN_CARDS);
        cardCountSlider.setMax(Variables.MAX_CARDS);

        capacitySlider.setMin(Variables.MIN_PLAYERS);
        capacitySlider.setMax(Variables.MAX_PLAYERS);
    }

    @FXML
    private void clickCreateButton() {
        int cardCount = (int) cardCountSlider.getValue();
        int capacity = (int) capacitySlider.getValue();

        GameRoom gameRoom = new GameRoom(capacity, cardCount);

        String message = MessageBuilder.buildCreateGameMessage(gameRoom);
        AppServices.getConnection().sendMessage(message);

        //close window
    }
}
