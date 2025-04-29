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
    @FXML
    private Button manageRoomButton;
    private boolean ready = false;
    private boolean ownRoom = false;
    @FXML
    private Button readyButton;
    @FXML
    private TableView<GameRoom> gameRoomTable;
    private final Observer gameTableChange = this::updateGameRoomTable;
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
    private void initialize() {
        appServices.getMessageHandler().register(MessageTypeClient.GAME_TABLE_CHANGE, gameTableChange);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);

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
                if (newItem != null && lobbyService.getCurrentGameRoom() != null) {
                    if (newItem.getGameId().equals(lobbyService.getCurrentGameRoom().getGameId())) {
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
        if (ownRoom) {
            GameRoomWindowController controller = sceneManager.openWindow(UIConstants.GAME_ROOM_MANAGER_FXML, "Manage game room");
            if (controller != null) {
                controller.setMode(Mode.MANAGE);
            }
        } else {
            GameRoomWindowController controller = sceneManager.openWindow(UIConstants.GAME_ROOM_FORM_FXML, "Create game room");
            if (controller != null) {
                controller.setMode(Mode.CREATE);
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

        if (lobbyService.getCurrentGameRoom() == null) {
            manageRoomButton.setText("Create new room");
            ownRoom = false;
        } else {
            Platform.runLater(() -> manageRoomButton.setText("Manage my room"));
            ownRoom = true;
        }

        if (lobbyService.getCurrentGameRoom() == null) {
            readyButton.setText("Not ready");
            readyButton.setStyle("-fx-background-color: #ffc0c0;");
            readyButton.setDisable(true);
        } else if (!ready) {
            readyButton.setDisable(false);
        }
    }

    @FXML
    private void handleReadyClick() {
        ready = true;
        readyButton.setText("Ready");
        readyButton.setStyle("-fx-background-color: #d0ffc0;");
        readyButton.setDisable(true);

        lobbyService.ready();
    }

    private void startGame() {
        unregister();
        sceneManager.switchScene(UIConstants.GAME_FXML);
    }

    private void showErrorMessage(Object errorDescription) {
        Platform.runLater(() -> sceneManager.showErrorAlert((String) errorDescription));
    }

    private void unregister() {
        appServices.getMessageHandler().unregister(MessageTypeClient.GAME_TABLE_CHANGE, gameTableChange);
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.GAME_ROOM_SUCCESS, successObserver);
    }
}
