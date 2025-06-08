package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameRoomParameters;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.LobbyResultHandler;
import cz.vse.pexeso.util.FormValidator;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract and validates parameters from game room creation/edit forms.
 * Throws IllegalArgumentException on invalid input. Also provides methods to disable fields
 * and re-register the LobbyResultHandler when the form window is closed.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameRoomFormHelper {
    private static final Logger log = LoggerFactory.getLogger(GameRoomFormHelper.class);

    private GameRoomFormHelper() {
    }

    /**
     * Extracts and validates name, capacity, and card-count parameters from form fields.
     *
     * @param nameField            text field for room name
     * @param capacitySlider       slider for capacity
     * @param boardSizeChoiceBox   choice box for board-size
     * @param customBoardSizeField text field for custom size
     * @return a GameRoomParameters object if validation succeeds
     * @throws IllegalArgumentException if any field is invalid (empty name, no board size selected, etc.)
     */
    public static GameRoomParameters extractValidParameters(
            TextField nameField,
            Slider capacitySlider,
            ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
            TextField customBoardSizeField
    ) {
        String name = nameField.getText();
        GameRoom.BoardSize boardSizeType = boardSizeChoiceBox.getValue();
        String customBoardSize = customBoardSizeField.getText();

        String warning = FormValidator.validateGameRoomForm(name, boardSizeType, customBoardSize);
        if (warning != null) {
            log.warn("GameRoomFormHelper: Validation failed - {}", warning);
            throw new IllegalStateException(warning);
        }

        int cardCount;
        if (boardSizeType == GameRoom.BoardSize.CUSTOM) {
            cardCount = Integer.parseInt(customBoardSize);
        } else {
            cardCount = boardSizeType.value;
        }

        int capacity = (int) capacitySlider.getValue();

        log.debug("Extracted parameters: name={}, capacity={}, cardCount={}", name, capacity, cardCount);
        return new GameRoomParameters(name, capacity, cardCount);
    }

    /**
     * Enables or disables all relevant form fields for editing a game room.
     *
     * @param disable              true to disable, false to enable
     * @param nameField            text field for room name
     * @param capacitySlider       slider for capacity
     * @param boardSizeChoiceBox   choice box for board-size
     * @param customBoardSizeField text field for custom size
     */
    public static void disableFields(
            boolean disable,
            TextField nameField,
            Slider capacitySlider,
            ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox,
            TextField customBoardSizeField
    ) {
        UICommonHelper.setDisableAll(disable, nameField, boardSizeChoiceBox, customBoardSizeField);
        capacitySlider.setDisable(disable);
        log.debug("Set disable={} on GameRoom form fields", disable);
    }

    /**
     * Registers the LobbyResultHandler when the form window is hidden (closed).
     * Ensures that real-time updates resume if the user returns to the lobby.
     *
     * @param stage         the Stage containing the form
     * @param lobbyModel    model for lobby actions
     * @param resultHandler handler to re-register on close
     */
    public static void setupSetOnHidden(
            Stage stage,
            LobbyModel lobbyModel,
            LobbyResultHandler resultHandler
    ) {
        stage.setOnHidden(windowEvent -> {
            if (lobbyModel.isInRoom() && !lobbyModel.isRoomInProgress()) {
                resultHandler.register();
                log.debug("Stage hidden: re-registered LobbyResultHandler (in room, not in progress)");
            } else if (!lobbyModel.isInRoom()) {
                resultHandler.register();
                log.debug("Stage hidden: re-registered LobbyResultHandler (not in room)");
            }
        });
    }
}