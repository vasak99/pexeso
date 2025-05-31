package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.model.result.GameRoomResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.util.FormValidator;
import cz.vse.pexeso.util.Strings;
import cz.vse.pexeso.view.helper.GameRoomUIHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomManagerController implements GameRoomResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameRoomManagerController.class);

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
    private Button saveChangesButton;
    @FXML
    private Button startGameButton;
    @FXML
    private TableView<LobbyPlayer> playerTable;
    @FXML
    private TableColumn<LobbyPlayer, String> playerNameColumn;
    @FXML
    private TableColumn<LobbyPlayer, String> playerStatusColumn;
    @FXML
    private TableColumn<LobbyPlayer, Void> actionColumn;
    @FXML
    private Label warningLabel;

    private final ChangeListener<GameRoom.BoardSize> listener;

    private LastAction lastAction = LastAction.NONE;

    public GameRoomManagerController(Navigator navigator, GameRoomModel gameRoomModel, Injector injector) {
        this.navigator = navigator;
        this.gameRoomModel = gameRoomModel;
        this.resultHandler = injector.createGameRoomResultHandler(this);

        this.listener = (observableValue, s, t1) -> customBoardSizeField.setVisible(boardSizeChoiceBox.getValue() == GameRoom.BoardSize.CUSTOM);
    }

    @FXML
    private void initialize() {
        resultHandler.register();
        GameRoomUIHelper.setupManagerUI(playerTable, playerNameColumn, playerStatusColumn, actionColumn, this, gameRoomModel, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField, warningLabel, resultHandler, saveChangesButton, listener);

        onGameRoomUIUpdate();
        log.info("GameRoomManagerController initialized");
    }

    @FXML
    private void handleSaveClick() {
        String warning = FormValidator.validateGameRoomForm(nameField.getText().trim(), boardSizeChoiceBox.getValue(), customBoardSizeField.getText().trim());
        if (warning != null) {
            editWarningLabel(warning);
            return;
        }
        editWarningLabel("");

        disableFields(true);
        log.info("Editing game room");
        lastAction = LastAction.EDIT;
        if (boardSizeChoiceBox.getValue() == GameRoom.BoardSize.CUSTOM) {
            gameRoomModel.attemptEditGame(nameField.getText().trim(), (int) capacitySlider.getValue(), Integer.parseInt(customBoardSizeField.getText().trim()));
        } else {
            gameRoomModel.attemptEditGame(nameField.getText().trim(), (int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
        }
    }

    @FXML
    private void handleDeleteClick() {
        if (navigator.showConfirmation(Strings.DELETE_ROOM_CONFIRMATION)) {
            lastAction = LastAction.DELETE;
            gameRoomModel.attemptDeleteGame();
        }
    }

    @FXML
    private void handleStartGameClick() {
        lastAction = LastAction.START;
        gameRoomModel.attemptStartGame();
    }

    public void kickPlayer(LobbyPlayer lobbyPlayer) {
        if (navigator.showConfirmation(String.format(Strings.KICK_PLAYER_CONFIRMATION, lobbyPlayer.getUsername()))) {
            lastAction = LastAction.KICK;
            gameRoomModel.attemptKickPlayer(lobbyPlayer);
        }
    }

    @Override
    public void onGameRoomSuccess(Object data) {
        if (lastAction != null && lastAction == LastAction.DELETE) {
            deleteGameSuccess((String) data);
        } else {
            log.warn("Invalid success");
        }
        lastAction = LastAction.NONE;
    }

    @Override
    public void onGameRoomError(String errorDescription) {
        editWarningLabel(errorDescription);
        disableFields(false);
    }

    @Override
    public void onPlayerUpdate(String data) {
        gameRoomModel.updateRoom(data);
        onGameRoomUIUpdate();
        disableFields(false);
    }

    @Override
    public void onGameRoomUIUpdate() {
        Platform.runLater(() -> {
            playerTable.setItems(gameRoomModel.getFilteredPlayers());
            playerTable.refresh();

            GameRoomUIHelper.updateFields(gameRoomModel, nameField, capacitySlider, boardSizeChoiceBox, saveChangesButton, customBoardSizeField);
            saveChangesButton.setDisable(true);

            startGameButton.setDisable(!gameRoomModel.areAllPlayersReady(playerTable.getItems()));
            navigator.closeConfirmationAlert();
        });
    }

    private void deleteGameSuccess(String redirectData) {
        gameRoomModel.finalizeGameDeletion(redirectData);
        resultHandler.unregister();
        Platform.runLater(navigator::closeWindow);
    }

    @FXML
    private void handleCloseClick() {
        resultHandler.unregister();
        Platform.runLater(navigator::closeWindow);
    }

    private void editWarningLabel(String text) {
        Platform.runLater(() -> {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText(text);
        });
    }

    private enum LastAction {
        NONE,
        EDIT,
        DELETE,
        KICK,
        START
    }

    private void disableFields(boolean disable) {
        nameField.setDisable(disable);
        capacitySlider.setDisable(disable);
        boardSizeChoiceBox.setDisable(disable);
        customBoardSizeField.setDisable(disable);
    }
}