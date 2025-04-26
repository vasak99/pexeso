package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomFormController {
    public static final Logger log = LoggerFactory.getLogger(GameRoomFormController.class);
    private GameRoomFormMode mode;

    private GameRoom createdGameRoom;
    private GameRoom editedGameRoom;

    @FXML
    private Button actionButton;
    @FXML
    private Slider cardCountSlider;
    @FXML
    private Slider capacitySlider;
    @FXML
    private Label warningLabel;

    public void setMode(GameRoomFormMode mode) {
        this.mode = mode;
        setup();
    }

    private void setup() {
        if (mode == GameRoomFormMode.CREATE) {
            actionButton.setText("Create");
            actionButton.setOnMouseClicked(e -> handleCreate());
            AppServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR_CREATE_GAME, this::handleErrorMessage);
            AppServices.getMessageHandler().register(MessageTypeClient.CREATE_GAME_SUCCESS, this::handleSuccessfulCreation);
        } else {
            actionButton.setText("Edit");
            actionButton.setOnMouseClicked(e -> handleEdit());
            AppServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR_EDIT_GAME, this::handleErrorMessage);
            AppServices.getMessageHandler().register(MessageTypeClient.EDIT_GAME_SUCCESS, this::handleSuccessfulEdit);
        }

        cardCountSlider.setMin(Variables.MIN_CARDS);
        cardCountSlider.setMax(Variables.MAX_CARDS);
        capacitySlider.setMin(Variables.MIN_PLAYERS);
        capacitySlider.setMax(Variables.MAX_PLAYERS);

        log.info("GameRoomFormController initialized");
    }

    private void handleCreate() {
        log.info("Creating game room");
        int cardCount = (int) cardCountSlider.getValue();
        int capacity = (int) capacitySlider.getValue();

        createdGameRoom = new GameRoom(capacity, cardCount);
        createdGameRoom.setHost(AppServices.getClientSession().getPlayerId());

        String message = MessageBuilder.buildCreateGameMessage(createdGameRoom);
        AppServices.getConnection().sendMessage(message);
    }

    private void handleEdit() {
        log.info("Editing game room");
        int cardCount = (int) cardCountSlider.getValue();
        int capacity = (int) capacitySlider.getValue();

        editedGameRoom = AppServices.getClientSession().getCurrentGameRoom();
        editedGameRoom.setCardCount(cardCount);
        editedGameRoom.setCapacity(capacity);

        String message = MessageBuilder.buildEditGameMessage(editedGameRoom);
        AppServices.getConnection().sendMessage(message);
    }

    private void handleSuccessfulCreation() {
        log.info("Game room created successfully");
        GameRoom.gameRooms.add(createdGameRoom);
        AppServices.getClientSession().setCurrentGameRoom(createdGameRoom);
        Platform.runLater(SceneManager::closeWindow);
    }

    private void handleSuccessfulEdit() {
        log.info("Game room edited successfully");
        GameRoom.editGameRoom(editedGameRoom.getGameId(), editedGameRoom.getCardCount(), editedGameRoom.getCapacity());
        Platform.runLater(SceneManager::closeWindow);
    }

    private void handleErrorMessage(Object message) {
        Platform.runLater(() -> warningLabel.setText((String) message));
    }
}
