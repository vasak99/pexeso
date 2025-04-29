package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.service.GameRoomWindowService;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.view.PlayerActionCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRoomWindowController {
    private static final Logger log = LoggerFactory.getLogger(GameRoomWindowController.class);
    private final AppServices appServices = AppServices.getInstance();
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final GameRoomWindowService gameRoomWindowService = new GameRoomWindowService();
    private final ObservableList<LobbyPlayer> currentlyDisplayedPlayers = FXCollections.observableArrayList();
    private boolean askedToDelete = false;
    @FXML
    private TableView<LobbyPlayer> playerTable;
    @FXML
    private TableColumn<LobbyPlayer, String> playerNameColumn;
    @FXML
    private TableColumn<LobbyPlayer, Void> actionColumn;
    @FXML
    private Slider capacitySlider;
    @FXML
    private Slider cardCountSlider;
    @FXML
    private Label warningLabel;
    private final ObserverWithData errorObserver = this::handleErrorMessage;
    private Mode mode;
    private final ObserverWithData successObserver = this::handleSuccess;

    public void setMode(Mode mode) {
        this.mode = mode;
        setup();
    }

    private void setup() {
        appServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);

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
        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new PlayerActionCell(this));
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
    private void handleDeleteClick() {
        askedToDelete = true;
        gameRoomWindowService.delete();
    }

    public void kickPlayer(LobbyPlayer lobbyPlayer) {
        gameRoomWindowService.kick(lobbyPlayer);
    }

    private void handleSuccess(Object data) {
        if (mode == Mode.CREATE) {
            gameRoomWindowService.handleCreateSuccess(data, (int) capacitySlider.getValue(), (int) cardCountSlider.getValue());
        } else if (!askedToDelete) {
            gameRoomWindowService.handleEditSuccess((int) capacitySlider.getValue(), (int) cardCountSlider.getValue());
        } else {
            gameRoomWindowService.handleDeleteSuccess();
        }
        Platform.runLater(sceneManager::closeWindow);
    }

    private void handleErrorMessage(Object message) {
        Platform.runLater(() -> warningLabel.setText((String) message));
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
