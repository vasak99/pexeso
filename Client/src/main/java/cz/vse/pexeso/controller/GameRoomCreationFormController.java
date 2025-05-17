package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.model.result.GameRoomResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.GameRoomUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomCreationFormController implements GameRoomResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameRoomCreationFormController.class);

    private final Navigator navigator;
    private final GameRoomModel gameRoomModel;

    private final GameRoomResultHandler resultHandler;

    @FXML
    private Slider capacitySlider;
    @FXML
    private ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox;
    @FXML
    private Label warningLabel;

    public GameRoomCreationFormController(Navigator navigator, GameRoomModel gameRoomModel, Injector injector) {
        this.navigator = navigator;
        this.gameRoomModel = gameRoomModel;
        this.resultHandler = injector.createGameRoomResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();

        GameRoomUIHelper.setupCreatorUI(capacitySlider, boardSizeChoiceBox, warningLabel, resultHandler);

        log.info("GameRoomCreationFormController initialized.");
    }

    @FXML
    private void handleSaveClick() {
        if (boardSizeChoiceBox.getValue() == null) {
            editWarningLabel("Choose board size");
            return;
        }

        log.info("Creating game room");
        gameRoomModel.attemptCreateGame((int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
    }

    @Override
    public void onGameRoomSuccess(Object data) {
        gameRoomModel.finalizeGameCreation(data, (int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
        resultHandler.unregister();
        Platform.runLater(navigator::closeWindow);
    }

    @Override
    public void onGameRoomError(String errorDescription) {
        editWarningLabel(errorDescription);
    }

    private void editWarningLabel(String text) {
        Platform.runLater(() -> {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText(text);
        });
    }
}
