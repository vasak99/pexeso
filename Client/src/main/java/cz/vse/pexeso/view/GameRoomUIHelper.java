package cz.vse.pexeso.view;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.controller.GameRoomManagerController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public final class GameRoomUIHelper {
    private GameRoomUIHelper() {
    }

    public static void setupManagerUI(TableView<LobbyPlayer> playerTable,
                                      TableColumn<LobbyPlayer, String> playerNameColumn,
                                      TableColumn<LobbyPlayer, String> playerStatusColumn,
                                      TableColumn<LobbyPlayer, Void> actionColumn,
                                      GameRoomManagerController controller,
                                      GameRoomModel gameRoomModel,
                                      Slider capacitySlider,
                                      ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
                                      Label warningLabel,
                                      GameRoomResultHandler resultHandler,
                                      Button saveChangesButton) {
        setupCreatorUI(capacitySlider, boardSizeChoiceBox, warningLabel, resultHandler);

        initializePlayerTable(playerTable, playerNameColumn, playerStatusColumn, actionColumn, controller, gameRoomModel);
        updateFields(gameRoomModel, capacitySlider, boardSizeChoiceBox, saveChangesButton);
        saveChangesButton.setDisable(true);
    }

    public static void setupCreatorUI(Slider capacitySlider,
                                      ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
                                      Label warningLabel,
                                      GameRoomResultHandler resultHandler) {
        initializeCapacitySlider(capacitySlider);
        initializeBoardSizeChoiceBox(boardSizeChoiceBox);
        setupWindowCloseEvent(warningLabel, resultHandler);
    }

    private static void initializePlayerTable(TableView<LobbyPlayer> playerTable,
                                              TableColumn<LobbyPlayer, String> playerNameColumn,
                                              TableColumn<LobbyPlayer, String> playerStatusColumn,
                                              TableColumn<LobbyPlayer, Void> actionColumn,
                                              GameRoomManagerController controller,
                                              GameRoomModel gameRoomModel
    ) {
        playerTable.setPlaceholder(new Label("No other players in this game room"));

        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        playerStatusColumn.setCellValueFactory(cellData -> {
            LobbyPlayer player = cellData.getValue();
            if (player == null) {
                return new ReadOnlyStringWrapper("");
            }
            LobbyPlayer.PlayerStatus status = player.getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button kickButton = new Button("Kick");
            private final HBox actionBox = new HBox(5, kickButton);

            {
                kickButton.setOnAction(event -> {
                    LobbyPlayer lobbyPlayer = getTableView().getItems().get(getIndex());
                    controller.kickPlayer(lobbyPlayer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                LobbyPlayer lobbyPlayer = getTableView().getItems().get(getIndex());
                if (lobbyPlayer == null) {
                    setGraphic(null);
                    return;
                }
                boolean isHost = false;
                GameRoom currentGameRoom = GameRoom.findById(lobbyPlayer.getCurrentGameId());

                if (currentGameRoom != null) {
                    isHost = lobbyPlayer.getPlayerId() == currentGameRoom.getHostId();
                }

                kickButton.setVisible(!isHost);

                setGraphic(actionBox);
            }
        });

        playerTable.setRowFactory(tableView -> {
            TableRow<LobbyPlayer> row = new TableRow<>();

            //Highlight host
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && gameRoomModel.getCurrentGameHostId() != null) {
                    if (newItem.getPlayerId() == gameRoomModel.getCurrentGameHostId()) {
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

    private static void initializeCapacitySlider(Slider capacitySlider) {
        capacitySlider.setMin(Variables.MIN_PLAYERS);
        capacitySlider.setMax(Variables.MAX_PLAYERS);
    }

    private static void initializeBoardSizeChoiceBox(ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox) {
        ObservableList<GameRoom.BoardSize> sizes = FXCollections.observableArrayList();
        sizes.setAll(GameRoom.BoardSize.SMALL, GameRoom.BoardSize.MEDIUM, GameRoom.BoardSize.LARGE);
        boardSizeChoiceBox.setItems(sizes);
    }

    public static void updateFields(GameRoomModel gameRoomModel, Slider capacitySlider, ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox, Button saveChangesButton) {
        double originalCapacity = gameRoomModel.getCurrentRoomCapacity();
        GameRoom.BoardSize originalBoardSize = GameRoom.BoardSize.fromValue(gameRoomModel.getCurrentRoomCardCount());

        capacitySlider.setValue(originalCapacity);
        boardSizeChoiceBox.getSelectionModel().select(originalBoardSize);

        capacitySlider.setOnMouseClicked(mouseEvent -> {
            boolean newCapacity = originalCapacity != capacitySlider.getValue();
            boolean newBoardSize = originalBoardSize != boardSizeChoiceBox.getValue();
            updateSaveChangesButton(saveChangesButton, newCapacity, newBoardSize);
        });
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, boardSize, t1) -> {
            boolean newCapacity = originalCapacity != capacitySlider.getValue();
            boolean newBoardSize = originalBoardSize != boardSizeChoiceBox.getValue();
            updateSaveChangesButton(saveChangesButton, newCapacity, newBoardSize);
        });
    }

    private static void updateSaveChangesButton(Button saveChangesButton, boolean newCapacity, boolean newBoardSize) {
        saveChangesButton.setDisable(!newCapacity && !newBoardSize);
    }

    private static void setupWindowCloseEvent(Label warningLabel, GameRoomResultHandler resultHandler) {
        Platform.runLater(() -> {
            Stage stage = (Stage) warningLabel.getScene().getWindow();
            stage.setOnCloseRequest(event -> resultHandler.unregister());
        });
    }
}
