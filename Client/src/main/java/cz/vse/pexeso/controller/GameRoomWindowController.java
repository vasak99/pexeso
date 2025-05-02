package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.PlayerStatus;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.service.GameRoomWindowService;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import cz.vse.pexeso.view.PlayerActionCell;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomWindowController {
    private static final Logger log = LoggerFactory.getLogger(GameRoomWindowController.class);

    private final AppServices appServices = AppServices.getInstance();
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final GameRoomWindowService gameRoomWindowService = new GameRoomWindowService();

    private final ObserverWithData errorObserver = this::handleErrorMessage;
    private final ObserverWithData successObserver = this::handleSuccess;
    public Button startGameButton;

    @FXML
    private Slider capacitySlider;
    @FXML
    private Slider cardCountSlider;
    @FXML
    private Button deleteRoomButton;
    @FXML
    private Button saveChangesButton;
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
    private boolean askedToDelete = false;
    private Mode mode;

    public void setMode(Mode mode) {
        this.mode = mode;
        setup();
    }

    private void setup() {
        register();

        capacitySlider.setMin(Variables.MIN_PLAYERS);
        capacitySlider.setMax(Variables.MAX_PLAYERS);
        cardCountSlider.setMin(Variables.MIN_CARDS);
        cardCountSlider.setMax(Variables.MAX_CARDS);

        if (mode == Mode.MANAGE) {
            configurePlayerTable();
            updatePlayerTable();
        }

        Platform.runLater(() -> {
            Stage stage = (Stage) warningLabel.getScene().getWindow();
            stage.setOnCloseRequest(event -> unregister());
        });
        log.info("GameRoomWindowController initialized");
    }

    private void updatePlayerTable() {
        currentlyDisplayedPlayers.setAll(gameRoomWindowService.filterPlayers());
        playerTable.setItems(currentlyDisplayedPlayers);
    }

    private void configurePlayerTable() {
        playerTable.setPlaceholder(new Label("No other players in this game room"));
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        playerStatusColumn.setCellValueFactory(cellData -> {
            LobbyPlayer player = cellData.getValue();
            if (player == null) {
                return new ReadOnlyStringWrapper("");
            }
            PlayerStatus status = player.getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });
        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new PlayerActionCell(this));
    }

    @FXML
    private void handleDeleteClick() {
        askedToDelete = true;
        gameRoomWindowService.delete();
    }

    @FXML
    private void handleSaveClick() {
        if (mode == Mode.CREATE) {
            log.info("Creating game room");
            gameRoomWindowService.create((int) capacitySlider.getValue(), (int) cardCountSlider.getValue());
        } else {
            log.info("Editing game room");
            gameRoomWindowService.edit((int) capacitySlider.getValue(), (int) cardCountSlider.getValue());
        }
    }

    @FXML
    private void handleCloseClick() {
        closeWindow();
    }

    @FXML
    private void handleStartGameClick() {
        closeWindow();
        sceneManager.switchScene(UIConstants.GAME_FXML);

    }

    public void kickPlayer(LobbyPlayer lobbyPlayer) {
        gameRoomWindowService.kick(lobbyPlayer);
    }

    private void handleSuccess(Object data) {
        if (mode == Mode.CREATE) {
            gameRoomWindowService.handleCreateSuccess(data, (int) capacitySlider.getValue(), (int) cardCountSlider.getValue());
            closeWindow();
        } else if (!askedToDelete) {
            gameRoomWindowService.handleEditSuccess((int) capacitySlider.getValue(), (int) cardCountSlider.getValue());
            warningLabel.setTextFill(Color.GREEN);
            warningLabel.setText("Changes saved");
        } else {
            gameRoomWindowService.handleDeleteSuccess();
            disableEverything();
            warningLabel.setTextFill(Color.GREEN);
            warningLabel.setText("Room deleted, you may now close the window");
        }
    }

    private void handleErrorMessage(Object message) {
        Platform.runLater(() -> warningLabel.setText((String) message));
    }

    private void closeWindow() {
        unregister();
        Platform.runLater(sceneManager::closeWindow);
    }

    private void disableEverything() {
        capacitySlider.setDisable(true);
        cardCountSlider.setDisable(true);
        deleteRoomButton.setDisable(true);
        saveChangesButton.setDisable(true);
        playerTable.setDisable(true);
    }

    private void register() {
        appServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }

    private void unregister() {
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }

    public enum Mode {
        CREATE,
        MANAGE
    }
}
