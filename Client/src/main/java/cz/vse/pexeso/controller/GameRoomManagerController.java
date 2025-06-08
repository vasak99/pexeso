package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.result.GameRoomResultHandler;
import cz.vse.pexeso.model.result.GameRoomResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameRoomFormConfigurator;
import cz.vse.pexeso.view.helper.GameRoomFormHelper;
import cz.vse.pexeso.view.helper.PlayerTableInitializer;
import cz.vse.pexeso.view.helper.UIFormUpdater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the game room management form. Allows editing, deleting, kicking players, and starting the game.
 * Validates user actions, updates UI fields
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoomManagerController implements GameRoomResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameRoomManagerController.class);

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
    private Button saveChangesButton;
    @FXML
    private Button startGameButton;
    @FXML
    private TableView<LobbyPlayer> playerTable;
    @FXML
    private TableColumn<LobbyPlayer, String> playerNameColumn;
    @FXML
    private TableColumn<LobbyPlayer, String> playerStatusColumn;
    @FXML
    private TableColumn<LobbyPlayer, Void> actionColumn;
    @FXML
    private Label warningLabel;

    private LastAction lastAction = LastAction.NONE;

    private enum LastAction {
        NONE,
        EDIT,
        DELETE,
        KICK,
        START
    }

    /**
     * Constructs a GameRoomManagerController with dependencies injected.
     *
     * @param navigator     for navigation callbacks
     * @param gameRoomModel model handling game room logic
     * @param injector      to obtain GameRoomResultHandler
     */
    public GameRoomManagerController(Navigator navigator, GameRoomModel gameRoomModel, Injector injector) {
        this.navigator = navigator;
        this.gameRoomModel = gameRoomModel;
        this.resultHandler = injector.getHandlerFactory().createGameRoomResultHandler(this);
    }

    /**
     * Initializes the manager form: registers server listeners, configures form fields and player table,
     * and binds UI elements.
     */
    @FXML
    private void initialize() {
        resultHandler.register();
        GameRoomFormConfigurator.configureFields(capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        PlayerTableInitializer.initialize(playerTable, playerNameColumn, playerStatusColumn, actionColumn, this, gameRoomModel);

        Platform.runLater(() -> GameRoomFormConfigurator.setupOnCloseRequest(boardSizeChoiceBox, customBoardSizeField, resultHandler));

        updateUI();
        log.info("GameRoomManagerController initialized and form configured");
    }

    /**
     * Handles click on the Save Changes button. Validates new parameters, disables fields,
     * and sends an edit request.
     */
    @FXML
    private void handleSaveClick() {
        GameRoomFormHelper.disableFields(true, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        try {
            gameRoomModel.setParameters(GameRoomFormHelper.extractValidParameters(
                    nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField));
            UIFormUpdater.showError("", warningLabel);
            lastAction = LastAction.EDIT;
            gameRoomModel.attemptEditGame();
            log.info("Edit game request sent");
        } catch (IllegalStateException e) {
            log.warn("Validation failed on save: {}", e.getMessage());
            UIFormUpdater.showError(e.getMessage(), warningLabel);
            GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        } catch (Exception e) {
            log.error("Unexpected error during handleSaveClick: ", e);
            UIFormUpdater.showError("Unexpected error occurred. Try again later.", warningLabel);
            GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        }
    }

    /**
     * Handles click on the Delete button. Confirms via alert and sends delete request if confirmed.
     */
    @FXML
    private void handleDeleteClick() {
        if (navigator.showConfirmation("Are you sure you want to delete this game room?")) {
            lastAction = LastAction.DELETE;
            gameRoomModel.attemptDeleteGame();
            log.info("Delete game request sent");
        }
    }

    /**
     * Handles click on the Start Game button. Sends start game request and sets lastAction accordingly.
     */
    @FXML
    private void handleStartGameClick() {
        lastAction = LastAction.START;
        gameRoomModel.attemptStartGame();
        log.info("Start game request sent");
    }

    /**
     * Kicks a specified player from the game room after confirmation.
     *
     * @param lobbyPlayer the player to kick
     */
    public void kickPlayer(LobbyPlayer lobbyPlayer) {
        if (navigator.showConfirmation(String.format("Are you sure you want to kick %s?", lobbyPlayer.getUsername()))) {
            lastAction = LastAction.KICK;
            gameRoomModel.attemptKickPlayer(lobbyPlayer.getPlayerId());
            log.info("Kick player request sent for {}", lobbyPlayer.getUsername());
        }
    }

    @Override
    public void onRequestIdentity(String gameId) {
        // No operation needed for creation form on request identity
    }

    /**
     * Handles the redirect after a successful action, such as deletion.
     *
     * @param parameters the redirect parameters containing necessary data
     */
    @Override
    public void onRedirect(RedirectParameters parameters) {
        if (lastAction == LastAction.DELETE) {
            deleteGameSuccess(parameters);
        } else {
            log.warn("Received redirect for unexpected action: {}", lastAction);
        }

        lastAction = LastAction.NONE;
    }

    /**
     * Handles errors by clearing parameters, showing an error message, and re-enabling fields.
     *
     * @param errorDescription the error message to display
     */
    @Override
    public void onError(String errorDescription) {
        gameRoomModel.clearParameters();
        Platform.runLater(() -> UIFormUpdater.showError(errorDescription, warningLabel));
        GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        log.warn("GameRoomManagerController error received: {}", errorDescription);
    }

    /**
     * Updates the game room with new data from the lobby update payload.
     * Re-enables form fields and updates the UI.
     *
     * @param lup update payload
     */
    @Override
    public void onLobbyUpdate(LobbyUpdatePayload lup) {
        gameRoomModel.updateRoom(lup);
        updateUI();
        GameRoomFormHelper.disableFields(false, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField);
        log.debug("Game room updated with data={}", lup);
    }

    /**
     * Deletes the game successfully: finalizes deletion, updates lobby, and closes window.
     */
    private void deleteGameSuccess(RedirectParameters parameters) {
        try {
            gameRoomModel.finalizeGameDeletion(parameters);
            log.info("Game deleted successfully, redirectData={}", parameters);

            LobbyController lobbyController = (LobbyController) navigator.getPrimaryController();
            lobbyController.updateUI();
        } catch (Exception e) {
            log.error("Error in deleteGameSuccess", e);
        } finally {
            resultHandler.unregister();
            Platform.runLater(navigator::closeWindow);
        }
    }

    /**
     * Handles the close button click by unregistering observers and closing the window.
     */
    @FXML
    private void handleCloseClick() {
        resultHandler.unregister();
        Platform.runLater(navigator::closeWindow);
        log.info("Closed GameRoomManager window");
    }

    /**
     * Updates the UI elements: player table, form fields, and start button state based on readiness.
     */
    public void updateUI() {
        Platform.runLater(() -> {
            playerTable.setItems(gameRoomModel.getFilteredPlayers());
            playerTable.refresh();

            GameRoomFormConfigurator.bindFields(gameRoomModel, nameField, capacitySlider, boardSizeChoiceBox, customBoardSizeField, saveChangesButton);

            saveChangesButton.setDisable(true);
            startGameButton.setDisable(!gameRoomModel.areAllPlayersReady());
            navigator.closeConfirmationAlert();
            log.debug("GameRoomManager UI updated");
        });
    }
}