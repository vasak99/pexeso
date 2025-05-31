package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.controller.GameRoomManagerController;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.util.Strings;
import cz.vse.pexeso.view.cell.PlayerActionCell;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public final class GameRoomUIHelper {
    private GameRoomUIHelper() {
    }

    public static void setupManagerUI(TableView<LobbyPlayer> playerTable,
                                      TableColumn<LobbyPlayer, String> playerNameColumn,
                                      TableColumn<LobbyPlayer, String> playerStatusColumn,
                                      TableColumn<LobbyPlayer, Void> actionColumn,
                                      GameRoomManagerController controller,
                                      GameRoomModel gameRoomModel,
                                      TextField nameField,
                                      Slider capacitySlider,
                                      ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
                                      TextField customBoardSizeField,
                                      Label warningLabel,
                                      GameRoomResultHandler resultHandler,
                                      Button saveChangesButton,
                                      ChangeListener<GameRoom.BoardSize> listener) {
        setupCreatorUI(capacitySlider, boardSizeChoiceBox, customBoardSizeField, warningLabel, resultHandler, listener);

        initializePlayerTable(playerTable, playerNameColumn, playerStatusColumn, actionColumn, controller, gameRoomModel);
        updateFields(gameRoomModel, nameField, capacitySlider, boardSizeChoiceBox, saveChangesButton, customBoardSizeField);
        saveChangesButton.setDisable(true);
    }

    public static void setupCreatorUI(Slider capacitySlider,
                                      ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
                                      TextField customBoardSizeField,
                                      Label warningLabel,
                                      GameRoomResultHandler resultHandler,
                                      ChangeListener<GameRoom.BoardSize> listener) {
        initializeCapacitySlider(capacitySlider);
        initializeBoardSizeChoiceBox(boardSizeChoiceBox);
        setupOnCloseRequest(boardSizeChoiceBox, listener, warningLabel, resultHandler);
        setupCustomBoardSizeField(customBoardSizeField);
        customBoardSizeField.setVisible(false);
    }

    private static void initializePlayerTable(TableView<LobbyPlayer> playerTable,
                                              TableColumn<LobbyPlayer, String> playerNameColumn,
                                              TableColumn<LobbyPlayer, String> playerStatusColumn,
                                              TableColumn<LobbyPlayer, Void> actionColumn,
                                              GameRoomManagerController controller,
                                              GameRoomModel gameRoomModel
    ) {
        playerTable.setPlaceholder(new Label(Strings.NO_PLAYERS));

        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        playerStatusColumn.setCellValueFactory(cellData -> {
            LobbyPlayer player = cellData.getValue();
            if (player == null) {
                return new ReadOnlyStringWrapper("");
            }
            LobbyPlayer.PlayerStatus status = player.getStatus();
            return new ReadOnlyStringWrapper(status.getValue());
        });

        actionColumn.setCellFactory(col -> new PlayerActionCell(controller, gameRoomModel));

        playerTable.setRowFactory(tableView -> {
            TableRow<LobbyPlayer> row = new TableRow<>();

            //Highlight host
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && gameRoomModel.getCurrentGameHostId() != null) {
                    if (newItem.getPlayerId() == gameRoomModel.getCurrentGameHostId()) {
                        row.setStyle(UIConstants.GRAY_COLOR);
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
        sizes.setAll(GameRoom.BoardSize.SMALL, GameRoom.BoardSize.MEDIUM, GameRoom.BoardSize.LARGE, GameRoom.BoardSize.CUSTOM);
        boardSizeChoiceBox.setItems(sizes);
    }

    public static void updateFields(GameRoomModel gameRoomModel, TextField nameField, Slider capacitySlider, ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox, Button saveChangesButton, TextField customBoardSizeField) {
        if (gameRoomModel.getCurrentRoomCapacity() == null) {
            return;
        }

        String originalName = gameRoomModel.getCurrentGameName();
        double originalCapacity = gameRoomModel.getCurrentRoomCapacity();
        GameRoom.BoardSize originalBoardSize = GameRoom.BoardSize.fromValue(gameRoomModel.getCurrentRoomCardCount());
        int originalCardCount = gameRoomModel.getCurrentRoomCardCount();

        nameField.setText(originalName);
        capacitySlider.setValue(originalCapacity);
        boardSizeChoiceBox.getSelectionModel().select(originalBoardSize);
        customBoardSizeField.setText(String.valueOf(originalCardCount));

        if (originalBoardSize == GameRoom.BoardSize.CUSTOM) {
            customBoardSizeField.setVisible(true);
        }

        Runnable update = () -> {
            boolean newName = !originalName.equals(nameField.getText().trim());
            boolean newCapacity = originalCapacity != capacitySlider.getValue();
            boolean newBoardSize = originalBoardSize != boardSizeChoiceBox.getValue();
            boolean newCardCount = false;
            try {
                newCardCount = originalCardCount != Integer.parseInt(customBoardSizeField.getText().trim());
            } catch (NumberFormatException e) {
            }

            updateSaveChangesButton(saveChangesButton, newName, newCapacity, newBoardSize, newCardCount);
        };

        nameField.textProperty().addListener((a, b, c) -> update.run());
        capacitySlider.setOnMouseClicked(a -> update.run());
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> update.run());
        customBoardSizeField.textProperty().addListener((a, b, c) -> update.run());
    }

    private static void updateSaveChangesButton(Button saveChangesButton, boolean newName, boolean newCapacity, boolean newBoardSize, boolean newCardCount) {
        saveChangesButton.setDisable(!newCapacity && !newBoardSize && !newName && !newCardCount);
    }

    private static void setupOnCloseRequest(ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox, ChangeListener<GameRoom.BoardSize> listener, Label warningLabel, GameRoomResultHandler resultHandler) {
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener(listener);

        Platform.runLater(() -> {
            Stage stage = (Stage) warningLabel.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                boardSizeChoiceBox.getSelectionModel().selectedItemProperty().removeListener(listener);
                resultHandler.unregister();
            });
        });
    }

    private static void setupCustomBoardSizeField(TextField customBoardSizeField) {
        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        customBoardSizeField.setTextFormatter(new TextFormatter<>(digitFilter));
    }
}
