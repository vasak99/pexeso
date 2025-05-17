package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.LobbyResultHandler;
import cz.vse.pexeso.model.result.LobbyResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.LobbyUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyController implements LobbyResultListener {
    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private final Navigator navigator;
    private final LobbyModel lobbyModel;

    private final LobbyResultHandler resultHandler;

    @FXML
    private TableView<GameRoom> gameRoomTable;
    @FXML
    private TableColumn<GameRoom, String> roomStatusColumn;
    @FXML
    private TableColumn<GameRoom, String> gameIdColumn;
    @FXML
    private TableColumn<GameRoom, Long> hostColumn;
    @FXML
    private TableColumn<GameRoom, GameRoom.BoardSize> boardSizeColumn;
    @FXML
    private TableColumn<GameRoom, Integer> roomCapacityColumn;
    @FXML
    private TableColumn<GameRoom, Void> actionsColumn;
    @FXML
    private Button manageRoomButton;
    @FXML
    private Button readyButton;

    public LobbyController(Navigator navigator, LobbyModel lobbyModel, Injector injector) {
        this.navigator = navigator;
        this.lobbyModel = lobbyModel;
        this.resultHandler = injector.createLobbyResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.initialRegister();
        LobbyUIHelper.setup(gameRoomTable, roomStatusColumn, gameIdColumn, hostColumn, boardSizeColumn, roomCapacityColumn, actionsColumn, this, lobbyModel, resultHandler);
        log.info("LobbyController initialized");
    }

    @FXML
    private void handleManageRoomClick() {
        resultHandler.unregister();
        Stage stage;
        if (lobbyModel.isInARoom()) {
            stage = navigator.openGameRoomManager();
        } else {
            stage = navigator.openGameRoomCreator();
        }
        if (stage != null) {
            stage.setOnHidden(windowEvent -> resultHandler.register());
        }
    }

    @FXML
    private void handleReadyClick() {
        editReadyButton(true, "Ready", "#d0ffc0");
        lobbyModel.attemptReady();
    }

    public void joinGameRoom(GameRoom gameRoom) {
        lobbyModel.attemptJoin(gameRoom);
    }

    public void leaveGameRoom() {
        if (navigator.showConfirmation("Are you sure you want to leave this game room?")) {
            lobbyModel.attemptLeave();
        }
    }

    @Override
    public void onLobbySuccess(String gameId) {
        lobbyModel.finalizeSuccess(gameId);
    }

    @Override
    public void onLobbyError(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
    }

    @Override
    public void onLobbyUpdate() {
        gameRoomTable.setItems(GameRoom.gameRooms);

        updateManageRoomButon();
        updateReadyButton();
    }

    private void updateManageRoomButon() {
        String currentRoomId = lobbyModel.getCurrentGameRoomId();
        boolean isHost = lobbyModel.isHosting();

        if (currentRoomId == null) {
            editManageRoomButton(false, "Create new room");
        } else if (isHost) {
            editManageRoomButton(false, "Manage my room");
        } else {
            editManageRoomButton(true, "Create new room");
        }
    }

    private void updateReadyButton() {
        String currentRoomId = lobbyModel.getCurrentGameRoomId();
        boolean isReady = lobbyModel.isReady();

        if (currentRoomId == null) {
            editReadyButton(true, "Not ready", "#ffc0c0");
        } else if (!isReady) {
            editReadyButton(false, null, null);
        }
    }

    private void editManageRoomButton(boolean disabled, String text) {
        manageRoomButton.setDisable(disabled);
        Platform.runLater(() -> manageRoomButton.setText(text));
    }

    private void editReadyButton(boolean disabled, String text, String style) {
        readyButton.setDisable(disabled);

        if (text != null) {
            readyButton.setText(text);
        }
        if (style != null) {
            readyButton.setStyle("-fx-background-color:" + style + ";");
        }
    }
}
