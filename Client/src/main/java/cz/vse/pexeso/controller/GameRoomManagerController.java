package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.BoardSize;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.PlayerStatus;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.model.result.GameRoomResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.GameRoomUIHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomManagerController implements GameRoomResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameRoomManagerController.class);

    private final Navigator navigator;
    private final GameRoomModel gameRoomModel;

    private final GameRoomResultHandler resultHandler;

    @FXML
    private Slider capacitySlider;
    @FXML
    private ChoiceBox<BoardSize> boardSizeChoiceBox;
    @FXML
    private Button deleteRoomButton;
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

    private final ObservableList<LobbyPlayer> currentlyDisplayedPlayers = FXCollections.observableArrayList();
    private LastAction lastAction = LastAction.NONE;

    public GameRoomManagerController(Navigator navigator, GameRoomModel gameRoomModel, Injector injector) {
        this.navigator = navigator;
        this.gameRoomModel = gameRoomModel;
        this.resultHandler = injector.createGameRoomResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();

        GameRoomUIHelper.setupCapacitySlider(capacitySlider);
        GameRoomUIHelper.setupBoardSizeChoiceBox(boardSizeChoiceBox);

        GameRoomUIHelper.initializePlayerTable(playerTable, playerNameColumn, playerStatusColumn, actionColumn, this, gameRoomModel);
        boardSizeChoiceBox.getSelectionModel().select(BoardSize.fromValue(gameRoomModel.getCurrentRoomCardCount()));
        capacitySlider.setValue(gameRoomModel.getCurrentRoomCapacity());

        setupWindowCloseEvent();

        log.info("GameRoomManagerController initialized");
    }

    private void setupWindowCloseEvent() {
        Platform.runLater(() -> {
            Stage stage = (Stage) warningLabel.getScene().getWindow();
            stage.setOnCloseRequest(event -> resultHandler.unregister());
        });
    }

    @FXML
    private void handleSaveClick() {
        if (boardSizeChoiceBox.getValue() == null) {
            editWarningLabel(Color.RED, "Choose board size");
            return;
        }

        log.info("Editing game room");
        lastAction = LastAction.EDIT;
        gameRoomModel.attemptEditGame((int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
    }

    @FXML
    private void handleDeleteClick() {
        if (navigator.showConfirmation("Are you sure you want to delete this game room?")) {
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
        if (navigator.showConfirmation("Are you sure you want to kick " + lobbyPlayer.getUsername() + "?")) {
            lastAction = LastAction.KICK;
            gameRoomModel.attemptKickPlayer(lobbyPlayer);
        }
    }

    @Override
    public void onGameRoomSuccess(Object data) {
        switch (lastAction) {
            case EDIT -> editGameSuccess();
            case DELETE -> deleteGameSuccess();
            case START -> startGameSuccess();
            case KICK -> kickPlayerSuccess();
            default -> log.warn("Invalid success");
        }
        lastAction = LastAction.NONE;
    }

    @Override
    public void onGameRoomError(String errorDescription) {
        editWarningLabel(Color.RED, errorDescription);
    }

    private void editGameSuccess() {
        gameRoomModel.finalizeGameEdit((int) capacitySlider.getValue(), boardSizeChoiceBox.getValue().value);
        editWarningLabel(Color.GREEN, "Changes saved");
    }

    private void deleteGameSuccess() {
        gameRoomModel.finalizeGameDeletion();
        disableEverything();
        editWarningLabel(Color.GREEN, "Room deleted, you may now close the window");
    }

    private void disableEverything() {
        capacitySlider.setDisable(true);
        boardSizeChoiceBox.setDisable(true);
        deleteRoomButton.setDisable(true);
        saveChangesButton.setDisable(true);
        playerTable.setDisable(true);
    }

    private void startGameSuccess() {
        closeWindow();
        navigator.goToGame();
    }

    private void kickPlayerSuccess() {
        updatePlayerTable();
    }

    @FXML
    private void handleCloseClick() {
        closeWindow();
    }

    private void closeWindow() {
        resultHandler.unregister();
        Platform.runLater(navigator::closeWindow);
    }

    private void editWarningLabel(Paint paint, String text) {
        Platform.runLater(() -> {
            warningLabel.setTextFill(paint);
            warningLabel.setText(text);
        });
    }

    private void updatePlayerTable() {
        currentlyDisplayedPlayers.setAll(LobbyPlayer.lobbyPlayers.stream()
                .filter(player -> player.getCurrentGameId().equals(gameRoomModel.getCurrentGameId()))
                .toList());
        playerTable.setItems(currentlyDisplayedPlayers);

        for (LobbyPlayer lobbyPlayer : currentlyDisplayedPlayers) {
            if (lobbyPlayer.getStatus() == PlayerStatus.NOT_READY) {
                startGameButton.setDisable(true);
                return;
            }
        }
        startGameButton.setDisable(false);
    }

    private enum LastAction {
        NONE,
        EDIT,
        DELETE,
        KICK,
        START
    }
}