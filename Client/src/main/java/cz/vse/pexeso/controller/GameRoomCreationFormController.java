package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.model.result.GameRoomResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameRoomFormConfigurator;
import cz.vse.pexeso.view.helper.GameRoomFormHelper;
import cz.vse.pexeso.view.helper.UIFormUpdater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the game room creation form. Handles user input for new room parameters,
 * validates via form helpers, sends creation request.
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoomCreationFormController implements GameRoomResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameRoomCreationFormController.class);

    private final Navigator navigator;
    private final GameRoomModel gameRoomModel;
    private final GameRoomResultHandler resultHandler;

    @FXML
    private TextField nameField;
    @FXML
    private Slider capacitySlider;
    @FXML
    private ChoiceBox<GameRoom.BoardSize> boardSizeChoiceBox;
    @FXML
    private TextField customBoardSizeField;
    @FXML
    private Label warningLabel;

    /**
     * Constructs a GameRoomCreationFormController with required dependencies.
     *
     * @param navigator     for navigation callbacks
     * @param gameRoomModel model handling game room logic
     * @param injector      to obtain GameRoomResultHandler
     */
    public GameRoomCreationFormController(Navigator navigator, GameRoomModel gameRoomModel, Injector injector) {
        this.navigator = navigator;
        this.gameRoomModel = gameRoomModel;
        this.resultHandler = injector.getHandlerFactory().createGameRoomResultHandler(this);
    }

    /**
     * Initializes the creation form by registering server observers, configuring fields,
     * and setting up cleanup on window close.
     */
    @FXML
    private void initialize() {
        resultHandler.register();
        GameRoomFormConfigurator.configureFields(capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        Platform.runLater(() -> GameRoomFormConfigurator.setupOnCloseRequest(boardSizeChoiceBox, customBoardSizeField, resultHandler));
        log.info("GameRoomCreationFormController initialized");
    }

    /**
     * Handles click on the Save button. Validates form input, disables fields, and attempts to create a new game room.
     */
    @FXML
    private void handleSaveClick() {
        GameRoomFormHelper.disableFields(true, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        try {
            gameRoomModel.setParameters(GameRoomFormHelper.extractValidParameters(
                    nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField));
            gameRoomModel.attemptCreateGame();
            log.info("Create game request sent");
        } catch (IllegalStateException e) {
            log.warn("Invalid parameters provided: {}", e.getMessage());
            UIFormUpdater.showError(e.getMessage(), warningLabel);
            GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        } catch (Exception e) {
            log.error("Unexpected error in handleSaveClick: ", e);
            UIFormUpdater.showError("Unexpected error occurred. Please try again.", warningLabel);
            GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        }
    }


    /**
     * Finalizes game creation, updates UI, unregisters and closes the window
     *
     * @param parameters redirect data
     */
    @Override
    public void onRedirect(RedirectParameters parameters) {
        try {
            gameRoomModel.finalizeGameCreation(parameters);
            log.info("Game room creation finalized, redirectData={}", parameters);

            // Update lobby UI
            LobbyController lobbyController = (LobbyController) navigator.getPrimaryController();
            lobbyController.updateUI();

        } catch (Exception e) {
            log.error("Error in onRedirect for GameRoomCreationFormController", e);
        } finally {
            resultHandler.unregister();
            Platform.runLater(navigator::closeWindow);
        }
    }

    /**
     * Handles error during game room creation, updates UI with error message
     *
     * @param errorDescription description of the error
     */
    @Override
    public void onError(String errorDescription) {
        gameRoomModel.clearParameters();
        Platform.runLater(() -> {
            UIFormUpdater.showError(errorDescription, warningLabel);
            GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        });
        log.warn("Game room creation error received: {}", errorDescription);
    }

    @Override
    public void onLobbyUpdate(LobbyUpdatePayload lup) {
        // No operation needed for creation form on lobby update
    }
}