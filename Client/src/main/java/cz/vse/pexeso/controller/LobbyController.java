package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
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
    @FXML
    private Button readyButton;
    @FXML
    private TableView<GameRoom> gameRoomTable;
    @FXML
    private TableColumn<GameRoom, String> gameIdColumn;
    @FXML
    private TableColumn<GameRoom, String> RoomHostColumn;
    @FXML
    private TableColumn<GameRoom, String> roomNameColumn;
    @FXML
    private TableColumn<GameRoom, String> boardSizeColumn;
    @FXML
    private TableColumn<GameRoom, String> roomPlayersColumn;
    @FXML
    private TableColumn<GameRoom, String> roomStatusColumn;

    @FXML
    private void initialize() {
        gameRoomTable.setPlaceholder(new Label("No game room has been created yet"));
        log.info("LobbyController initialized");

        readyButton.setDisable(true);

        gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        RoomHostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        boardSizeColumn.setCellValueFactory(new PropertyValueFactory<>("boardSize"));
        roomPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("players"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    private void clickReady() {
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
        SceneManager.openWindow(UIConstants.NEW_ROOM_FXML, "Create new room");
    }
}
