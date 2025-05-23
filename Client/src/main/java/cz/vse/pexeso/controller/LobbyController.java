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
import javafx.scene.control.Label;
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
    private Label tableTitle;
    @FXML
    private TableView<GameRoom> gameRoomTable;
    @FXML
    private TableColumn<GameRoom, String> roomStatusColumn;
    @FXML
    private TableColumn<GameRoom, String> gameNameColumn;
    @FXML
    private TableColumn<GameRoom, String> hostNameColumn;
    @FXML
    private TableColumn<GameRoom, String> boardSizeColumn;
    @FXML
    private TableColumn<GameRoom, Integer> roomCapacityColumn;
    @FXML
    private TableColumn<GameRoom, Void> actionsColumn;
    @FXML
    private Button manageRoomButton;
    @FXML
    private Button readyButton;

    private LastAction lastAction = LastAction.NONE;

    public LobbyController(Navigator navigator, LobbyModel lobbyModel, Injector injector) {
        this.navigator = navigator;
        this.lobbyModel = lobbyModel;
        this.resultHandler = injector.createLobbyResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.initialRegister();
        LobbyUIHelper.setup(gameRoomTable, roomStatusColumn, gameNameColumn, hostNameColumn, boardSizeColumn, roomCapacityColumn, actionsColumn, this, lobbyModel, resultHandler);

        onLobbyUIUpdate();

        tableTitle.setText("Available rooms for " + lobbyModel.getPlayerName());

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
        lastAction = LastAction.JOIN;
        lobbyModel.attemptJoin(gameRoom);
    }

    public void leaveGameRoom() {
        if (navigator.showConfirmation("Are you sure you want to leave this game room?")) {
            lastAction = LastAction.LEAVE;
            lobbyModel.attemptLeave();
        }
    }

    @Override
    public void onLobbySuccess(String redirectData) {
        switch (lastAction) {
            case JOIN -> lobbyModel.finalizeJoin(redirectData);
            case LEAVE -> lobbyModel.finalizeLeave(redirectData);
            case NONE -> {
                lobbyModel.finalizeLeave(redirectData);
                Platform.runLater(navigator::closeConfirmationAlert);
                Platform.runLater(() -> navigator.showError("You got kicked from the game room"));
            }
        }
        lastAction = LastAction.NONE;

        onLobbyUIUpdate();
    }

    @Override
    public void onLobbyError(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
    }

    @Override
    public void onGameRoomUpdate(String data) {
        lobbyModel.updateGameRooms(data);

        onLobbyUIUpdate();
    }

    @Override
    public void onPlayerUpdate(String data) {
        lobbyModel.updatePlayers(data);
    }

    @Override
    public void onLobbyUIUpdate() {
        System.out.println("ON LOBBY UI UPDATE");
        gameRoomTable.setItems(GameRoom.gameRooms);
        gameRoomTable.refresh();

        updateManageRoomButon();
        updateReadyButton();
    }

    @Override
    public void onIdentityRequested() {
        lobbyModel.sendIdentity();
    }

    @Override
    public void onStartGame() {
        resultHandler.finalUnregister();
        Platform.runLater(navigator::closeWindow);
        Platform.runLater(navigator::goToGame);
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
        boolean isHost = lobbyModel.isHosting();

        if (currentRoomId == null) {
            editReadyButton(true, "Not ready", "#ffc0c0");
        } else if (!isReady) {
            editReadyButton(false, null, null);
        } else if (isHost) {
            editReadyButton(true, "Ready", "#d0ffc0");
        }
    }

    private void editManageRoomButton(boolean disabled, String text) {
        manageRoomButton.setDisable(disabled);
        Platform.runLater(() -> manageRoomButton.setText(text));
    }

    private void editReadyButton(boolean disabled, String text, String style) {
        readyButton.setDisable(disabled);

        if (text != null) {
            Platform.runLater(() -> readyButton.setText(text));
        }
        if (style != null) {
            Platform.runLater(() -> readyButton.setStyle("-fx-background-color:" + style + ";"));
        }
    }

    private enum LastAction {
        NONE,
        JOIN,
        LEAVE,
    }
}
