package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.UnaryOperator;

/**
 * Configures sliders, choice boxes, and custom board-size fields for game room creation/editing forms.
 * Binds fields to existing room values and enables save button when changes occur.
 * Unregisters listeners when the window is closed to prevent memory leaks.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameRoomFormConfigurator {
    private static final Logger log = LoggerFactory.getLogger(GameRoomFormConfigurator.class);

    private GameRoomFormConfigurator() {
    }

    /**
     * Sets up the capacity slider, board-size choice box, and custom-size field with appropriate constraints.
     *
     * @param capacitySlider       slider for room capacity
     * @param boardSizeChoiceBox   dropdown for board sizes
     * @param customBoardSizeField text field for custom card count
     */
    public static void configureFields(
            Slider capacitySlider,
            ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
            TextField customBoardSizeField
    ) {
        configureCapacitySlider(capacitySlider);
        configureBoardSizeChoiceBox(boardSizeChoiceBox);
        configureCustomBoardSizeField(customBoardSizeField);
        log.debug("Configured GameRoom form fields");
    }

    /**
     * Binds form fields to the current game room values and enables the save button when any change is detected.
     *
     * @param gameRoomModel        model containing current room data
     * @param nameField            text field for room name
     * @param capacitySlider       slider for room capacity
     * @param boardSizeChoiceBox   dropdown for board sizes
     * @param customBoardSizeField text field for custom card count
     * @param saveChangesButton    button to save edits
     */
    public static void bindFields(
            cz.vse.pexeso.model.model.GameRoomModel gameRoomModel,
            TextField nameField,
            Slider capacitySlider,
            ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
            TextField customBoardSizeField,
            javafx.scene.control.Button saveChangesButton
    ) {
        Integer capacity = gameRoomModel.getCurrentRoomCapacity();
        if (capacity == null) {
            log.warn("Current room capacity is null; cannot bind fields");
            return;
        }

        String originalName = gameRoomModel.getCurrentGameName();
        double originalCapacity = capacity;
        GameRoom.BoardSize originalBoardSize = GameRoom.BoardSize.fromValue(gameRoomModel.getCurrentRoomCardCount());
        int originalCardCount = gameRoomModel.getCurrentRoomCardCount();

        if (originalName == null) {
            originalName = "";
        }

        nameField.setText(originalName);
        capacitySlider.setValue(originalCapacity);
        if (originalBoardSize != null) {
            boardSizeChoiceBox.getSelectionModel().select(originalBoardSize);
        }
        customBoardSizeField.setText(String.valueOf(originalCardCount));

        customBoardSizeField.setVisible(originalBoardSize == GameRoom.BoardSize.CUSTOM);

        final String finalOriginalName = originalName;
        Runnable updateSaveButton = () -> {
            boolean nameChanged = !finalOriginalName.equals(nameField.getText().trim());
            //comparing doubles - capacity changed if difference is larger than 0.0001
            boolean capacityChanged = Math.abs(originalCapacity - capacitySlider.getValue()) > 0.0001;
            boolean boardSizeChanged = originalBoardSize != boardSizeChoiceBox.getValue();
            boolean cardCountChanged;
            try {
                cardCountChanged = originalCardCount != Integer.parseInt(customBoardSizeField.getText().trim());
            } catch (NumberFormatException ignored) {
                cardCountChanged = true;
            }
            saveChangesButton.setDisable(!(nameChanged || capacityChanged || boardSizeChanged || cardCountChanged));
        };

        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateSaveButton.run());
        capacitySlider.valueProperty().addListener((obs, oldVal, newVal) -> updateSaveButton.run());
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateSaveButton.run());
        customBoardSizeField.textProperty().addListener((obs, oldVal, newVal) -> updateSaveButton.run());

        log.debug("Bound GameRoom form fields to model values");
    }

    /**
     * Sets up a listener for board-size changes to show/hide the custom size field,
     * and unregisters the resultHandler when the window is closed.
     *
     * @param boardSizeChoiceBox   dropdown for board sizes
     * @param customBoardSizeField text field for custom card count
     * @param resultHandler        handler for server responses
     */
    public static void setupOnCloseRequest(
            ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
            TextField customBoardSizeField,
            GameRoomResultHandler resultHandler
    ) {
        ChangeListener<GameRoom.BoardSize> sizeListener = (obs, oldSize, newSize) -> {
            customBoardSizeField.setVisible(newSize == GameRoom.BoardSize.CUSTOM);
            log.debug("Board size selection changed to {}; custom field visible={}", newSize, newSize == GameRoom.BoardSize.CUSTOM);
        };
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener(sizeListener);

        if (boardSizeChoiceBox.getScene().getWindow() instanceof Stage stage) {
            stage.setOnCloseRequest(event -> {
                boardSizeChoiceBox.getSelectionModel().selectedItemProperty().removeListener(sizeListener);
                resultHandler.unregister();
                log.debug("Stage closed; unregistered GameRoomResultHandler");
            });
        }
    }

    /**
     * Configures the capacity slider’s minimum and maximum values.
     */
    private static void configureCapacitySlider(Slider capacitySlider) {
        capacitySlider.setMin(Variables.MIN_PLAYERS);
        capacitySlider.setMax(Variables.MAX_PLAYERS);
        log.debug("Configured capacity slider min={} max={}", Variables.MIN_PLAYERS, Variables.MAX_PLAYERS);
    }

    /**
     * Populates the board-size choice box with predefined enum values.
     */
    private static void configureBoardSizeChoiceBox(ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox) {
        ObservableList<GameRoom.BoardSize> sizes = FXCollections.observableArrayList(
                GameRoom.BoardSize.SMALL,
                GameRoom.BoardSize.MEDIUM,
                GameRoom.BoardSize.LARGE,
                GameRoom.BoardSize.CUSTOM
        );
        boardSizeChoiceBox.setItems(sizes);
        log.debug("Configured board size choice box with enum values");
    }

    /**
     * Configures the custom board-size field to accept only digits and hides it initially.
     */
    private static void configureCustomBoardSizeField(TextField customBoardSizeField) {
        UnaryOperator<TextFormatter.Change> digitFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };
        customBoardSizeField.setTextFormatter(new TextFormatter<>(digitFilter));
        customBoardSizeField.setVisible(false);
        log.debug("Configured custom board size field with digit-only filter");
    }
}