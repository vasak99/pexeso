package cz.vse.pexeso.controller;


import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameStatus;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.service.GameRoomService;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import cz.vse.pexeso.view.ActionCell;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyController {
    public static final Logger log = LoggerFactory.getLogger(LobbyController.class);
    private final GameRoomService gameRoomService = new GameRoomService();

    @FXML
    private Button createNewRoomButton;

    @FXML
    private Button readyButton;
    @FXML
    private TableView<GameRoom> gameRoomTable;

    @FXML
    private TableColumn<GameRoom, String> roomStatusColumn;
    @FXML
    private TableColumn<GameRoom, String> gameIdColumn;
    @FXML
    private TableColumn<GameRoom, String> hostColumn;
    @FXML
    private TableColumn<GameRoom, Integer> cardCountColumn;
    @FXML
    private TableColumn<GameRoom, Integer> roomCapacityColumn;
    @FXML
    private TableColumn<GameRoom, Void> actionsColumn;


    @FXML
    private void initialize() {
        AppServices.getMessageHandler().registerWithData(MessageTypeClient.REDIRECT, this::handleRoomCreation);
        configureGameRoomTable();
        updateGameRoomTable();
        log.info("LobbyController initialized");
    }

    private void configureGameRoomTable() {
        gameRoomTable.setPlaceholder(new Label("No game room has been created yet"));
        roomStatusColumn.setCellValueFactory(cellData -> {
            GameStatus status = cellData.getValue().getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });
        gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        cardCountColumn.setCellValueFactory(new PropertyValueFactory<>("cardCount"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        setupActionsColumn();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new ActionCell(this));
    }

    public void joinGameRoom(GameRoom gameRoom) {
        if (gameRoomService.join(gameRoom)) {
            readyButton.setDisable(false);
        }
    }

    public void editGameRoom(GameRoom gameRoom) {
        gameRoomService.edit(gameRoom);
    }

    public void deleteGameRoom(GameRoom gameRoom) {
        gameRoomService.delete(gameRoom);
    }

    @FXML
    private void handleReadyClick() {
        if (readyButton.getText().equals("Ready up")) {
            readyButton.setText("Unready");
            readyButton.setStyle("-fx-background-color: #ffc0c0;");
            //send update to server
        } else {
            readyButton.setText("Ready up");
            readyButton.setStyle("-fx-background-color: #d0ffc0;");
            //send update to server
        }
    }

    @FXML
    private void clickCreateNewRoom() {
        GameRoomFormController controller = SceneManager.openWindow(UIConstants.GAME_ROOM_FORM_FXML, "Create new game room");
        if (controller != null) {
            controller.setMode(GameRoomFormMode.CREATE);
        }
    }

    private void handleRoomCreation(Object data) {
        Platform.runLater(this::updateGameRoomTable);
        createNewRoomButton.setDisable(true);
        // connect to the given port
    }

    private void updateGameRoomTable() {
        gameRoomTable.setItems(GameRoom.gameRooms);
    }
}
