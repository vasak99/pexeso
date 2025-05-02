package cz.vse.pexeso.controller;

import cz.vse.pexeso.controller.GameRoomWindowController.Mode;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameStatus;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.service.LobbyService;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import cz.vse.pexeso.view.GameRoomActionCell;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyController {
    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private final AppServices appServices = AppServices.getInstance();
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final LobbyService lobbyService = new LobbyService();

    private final ObserverWithData errorObserver = this::showErrorMessage;
    private final ObserverWithData successObserver = this::handleRoomChangeSuccess;
    private final Observer gameTableChange = this::updateGameRoomTable;

    @FXML
    private TableView<GameRoom> gameRoomTable;
    @FXML
    private TableColumn<GameRoom, String> roomStatusColumn;
    @FXML
    private TableColumn<GameRoom, String> gameIdColumn;
    @FXML
    private TableColumn<GameRoom, Long> hostColumn;
    @FXML
    private TableColumn<GameRoom, Integer> cardCountColumn;
    @FXML
    private TableColumn<GameRoom, Integer> roomCapacityColumn;
    @FXML
    private TableColumn<GameRoom, Void> actionsColumn;
    @FXML
    private Button manageRoomButton;
    @FXML
    private Button readyButton;

    @FXML
    private void initialize() {
        initialRegister();

        configureGameRoomTable();
        updateGameRoomTable();
        log.info("LobbyController initialized");
    }

    private void configureGameRoomTable() {
        gameRoomTable.setPlaceholder(new Label("No game room has been created yet"));
        roomStatusColumn.setCellValueFactory(cellData -> {
            GameRoom room = cellData.getValue();
            if (room == null) {
                return new ReadOnlyStringWrapper("");
            }
            GameStatus status = room.getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });
        gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("hostId"));
        cardCountColumn.setCellValueFactory(new PropertyValueFactory<>("cardCount"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        setupActionsColumn();

        gameRoomTable.setRowFactory(tableView -> {
            TableRow<GameRoom> row = new TableRow<>();

            //Highlight user's room
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && lobbyService.getCurrentGameRoomId() != null) {
                    if (newItem.getGameId().equals(lobbyService.getCurrentGameRoomId())) {
                        row.setStyle("-fx-background-color: #e6e6e6;");
                    } else {
                        row.setStyle("");
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new GameRoomActionCell(this));
    }

    @FXML
    private void handleManageRoomClick() {
        if (lobbyService.getCurrentGameRoomId() != null) {
            GameRoomWindowController controller = sceneManager.openWindow(UIConstants.GAME_ROOM_MANAGER_FXML, "Manage game room");
            if (controller != null) {
                unregister();
                controller.setMode(Mode.MANAGE);
                sceneManager.getOpenedWindow().setOnHidden(event -> register());
            }
        } else {
            GameRoomWindowController controller = sceneManager.openWindow(UIConstants.GAME_ROOM_FORM_FXML, "Create game room");
            if (controller != null) {
                unregister();
                controller.setMode(Mode.CREATE);
                sceneManager.getOpenedWindow().setOnHidden(event -> register());
            }
        }
    }

    public void joinGameRoom(GameRoom gameRoom) {
        lobbyService.join(gameRoom);
    }

    public void leaveGameRoom(GameRoom gameRoom) {
        lobbyService.leave(gameRoom);
    }

    private void handleRoomChangeSuccess(Object o) {
        lobbyService.handleSuccess((String) o);
    }

    private void updateGameRoomTable() {
        gameRoomTable.setItems(GameRoom.gameRooms);

        if (lobbyService.getCurrentGameRoomId() == null) {
            Platform.runLater(() -> manageRoomButton.setText("Create new room"));
            manageRoomButton.setDisable(false);
        } else if (lobbyService.isHosting()) {
            Platform.runLater(() -> manageRoomButton.setText("Manage my room"));
            manageRoomButton.setDisable(false);
        } else {
            Platform.runLater(() -> manageRoomButton.setText("Create new room"));
            manageRoomButton.setDisable(true);
        }

        if (lobbyService.getCurrentGameRoomId() == null) {
            readyButton.setText("Not ready");
            readyButton.setStyle("-fx-background-color: #ffc0c0;");
            readyButton.setDisable(true);
        } else if (!lobbyService.isReady()) {
            readyButton.setDisable(false);
        }
    }

    @FXML
    private void handleReadyClick() {
        lobbyService.setReady(true);
        readyButton.setText("Ready");
        readyButton.setStyle("-fx-background-color: #d0ffc0;");
        readyButton.setDisable(true);

        lobbyService.ready();
    }

    private void showErrorMessage(Object errorDescription) {
        Platform.runLater(() -> sceneManager.showErrorAlert((String) errorDescription));
    }

    private void initialRegister() {
        appServices.getMessageHandler().register(MessageTypeClient.GAME_TABLE_CHANGE, gameTableChange);
        register();
    }

    private void register() {
        appServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }

    private void unregister() {
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }

    private void finalUnregister() {
        appServices.getMessageHandler().unregister(MessageTypeClient.GAME_TABLE_CHANGE, gameTableChange);
        unregister();
    }
}
