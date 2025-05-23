package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.model.result.GameRoomResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.util.FormValidator;
import cz.vse.pexeso.view.GameRoomUIHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomCreationFormController implements GameRoomResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameRoomCreationFormController.class);

    private final Navigator navigator;
    private final GameRoomModel gameRoomModel;

    private final GameRoomResultHandler resultHandler;
    @FXML
    private TextField nameField;
    @FXML
    private Slider capacitySlider;
    @FXML
    private ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox;
    @FXML
    private TextField customBoardSizeField;
    @FXML
    private Label warningLabel;

    private final ChangeListener<GameRoom.BoardSize> listener;

    public GameRoomCreationFormController(Navigator navigator, GameRoomModel gameRoomModel, Injector injector) {
        this.navigator = navigator;
        this.gameRoomModel = gameRoomModel;
        this.resultHandler = injector.createGameRoomResultHandler(this);

        this.listener = (observableValue, s, t1) -> customBoardSizeField.setVisible(boardSizeChoiceBox.getValue() == GameRoom.BoardSize.CUSTOM);
    }

    @FXML
    private void initialize() {
        resultHandler.register();
        GameRoomUIHelper.setupCreatorUI(capacitySlider, boardSizeChoiceBox, customBoardSizeField, warningLabel, resultHandler, listener);
        log.info("GameRoomCreationFormController initialized.");
    }

    @FXML
    private void handleSaveClick() {
        String warning = FormValidator.validateGameRoomForm(nameField.getText(), boardSizeChoiceBox.getValue(), customBoardSizeField.getText());
        if (warning != null) {
            editWarningLabel(warning);
            return;
        }

        disableFields(true);
        log.info("Creating game room");
        if (boardSizeChoiceBox.getValue() == GameRoom.BoardSize.CUSTOM) {
            gameRoomModel.attemptCreateGame(nameField.getText().trim(), (int) capacitySlider.getValue(), Integer.parseInt(customBoardSizeField.getText()));
        } else {
            gameRoomModel.attemptCreateGame(nameField.getText().trim(), (int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
        }
    }

    @Override
    public void onGameRoomSuccess(Object data) {
        if (boardSizeChoiceBox.getValue() == GameRoom.BoardSize.CUSTOM) {
            gameRoomModel.finalizeGameCreation(data, nameField.getText().trim(), (int) capacitySlider.getValue(), Integer.parseInt(customBoardSizeField.getText()));
        } else {
            gameRoomModel.finalizeGameCreation(data, nameField.getText().trim(), (int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
        }

        resultHandler.unregister();
        Platform.runLater(navigator::closeWindow);
    }

    @Override
    public void onGameRoomError(String errorDescription) {
        editWarningLabel(errorDescription);
        disableFields(false);
    }

    @Override
    public void onPlayerUpdate(String data) {

    }

    @Override
    public void onGameRoomUIUpdate() {
    }

    private void editWarningLabel(String text) {
        Platform.runLater(() -> {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText(text);
        });
    }

    private void disableFields(boolean disable) {
        nameField.setDisable(disable);
        capacitySlider.setDisable(disable);
        boardSizeChoiceBox.setDisable(disable);
        customBoardSizeField.setDisable(disable);
    }
}
