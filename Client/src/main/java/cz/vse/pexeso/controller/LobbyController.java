package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameStatsPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.LobbyResultHandler;
import cz.vse.pexeso.model.result.LobbyResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.util.GameRoomManager;
import cz.vse.pexeso.view.helper.GameRoomFormHelper;
import cz.vse.pexeso.view.helper.LobbyTableInitializer;
import cz.vse.pexeso.view.helper.LobbyUIUpdater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the lobby screen. Manages the table of available game rooms,
 * handles join/leave actions, ready button toggles, and starts games. Updates UI
 * based on server messages via LobbyResultListener.
 *
 * @author kott10
 * @version June 2025
 */
public class LobbyController implements LobbyResultListener {
    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private final Navigator navigator;
    private final LobbyModel lobbyModel;
    private final LobbyResultHandler resultHandler;

    @FXML
    private Label gamesPlayedLabel;
    @FXML
    private Label totalPairsLabel;

    @FXML
    private Label tableTitle;
    @FXML
    private TableView<GameRoom> lobbyTable;
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

    private enum LastAction {
        NONE,
        JOIN,
        LEAVE
    }

    /**
     * Constructs a LobbyController with necessary dependencies.
     *
     * @param navigator  for screen navigation
     * @param lobbyModel for lobby logic
     * @param injector   to obtain LobbyResultHandler
     */
    public LobbyController(Navigator navigator, LobbyModel lobbyModel, Injector injector) {
        this.navigator = navigator;
        this.lobbyModel = lobbyModel;
        this.resultHandler = injector.getHandlerFactory().createLobbyResultHandler(this);
    }

    /**
     * Initializes the lobby screen: registers listeners, sets up the table,
     * clears any existing session room, and updates UI labels/buttons.
     */
    @FXML
    private void initialize() {
        lobbyModel.askForStats();


        resultHandler.initialRegister();

        LobbyTableInitializer.initialize(lobbyTable, roomStatusColumn, gameNameColumn,
                hostNameColumn, boardSizeColumn, roomCapacityColumn, actionsColumn, this, lobbyModel);
        lobbyModel.clearCurrentGameRoom();
        updateUI();

        tableTitle.setText(String.format("Available rooms for %s", lobbyModel.getPlayerName()));

        log.info("LobbyController initialized.");
    }

    /**
     * Handles click on the manage room button. Opens either the room manager or creator depending on session state.
     * Unregisters the result handler.
     */
    @FXML
    private void handleManageRoomClick() {
        resultHandler.unregister();

        Stage stage;
        if (lobbyModel.isInRoom()) {
            stage = navigator.openGameRoomManager(this);
        } else {
            stage = navigator.openGameRoomCreator(this);
        }

        if (stage != null) {
            GameRoomFormHelper.setupSetOnHidden(stage, lobbyModel, resultHandler);
            log.info("Opened game room form (manage or create) successfully");
        } else {
            log.warn("Stage returned by navigator is null");
        }
    }

    /**
     * Handles click on the ready button. Updates UI state to reflect ready status and sends ready request to server.
     */
    @FXML
    private void handleReadyClick() {
        LobbyUIUpdater.editReadyButton(true, "Ready", UIConstants.GREEN_COLOR, readyButton);
        lobbyModel.attemptReady();
        log.info("Ready request sent");
    }

    /**
     * Initiates joining a game room. Validates gameId and sends join request.
     *
     * @param gameId the ID of the room to join
     */
    public void joinGameRoom(String gameId) {
        lastAction = LastAction.JOIN;
        lobbyModel.attemptJoin(gameId);
        log.info("Attempted to join gameId={}", gameId);
    }

    /**
     * Initiates leaving the current game room. Shows confirmation alert and sends leave request on confirmation.
     */
    public void leaveGameRoom() {
        if (navigator.showConfirmation("Are you sure you want to leave this game room?")) {
            lastAction = LastAction.LEAVE;
            lobbyModel.attemptLeave();
            log.info("Leave request sent");
        }
    }

    /**
     * Handles redirect message based on last action - either join or leaves room, updates UI
     *
     * @param parameters redirect data
     */
    @Override
    public void onRedirect(RedirectParameters parameters) {
        switch (lastAction) {
            case JOIN -> {
                lobbyModel.finalizeJoin(parameters);
                log.info("Join finalized with redirectData={}", parameters);
            }
            case LEAVE -> {
                lobbyModel.finalizeLeave(parameters);
                log.info("Leave finalized with redirectData={}", parameters);
            }
            case NONE -> {
                lobbyModel.finalizeLeave(parameters);
                Platform.runLater(() -> {
                    navigator.closeConfirmationAlert();
                    navigator.showError("You got kicked from the game room");
                });
                log.warn("Received redirect with NONE action: treated as kick");
            }
        }
        lastAction = LastAction.NONE;
        updateUI();
    }

    /**
     * Handles error messages from the lobby server. Displays an error alert with the provided description.
     *
     * @param errorDescription the error message to display
     */
    @Override
    public void onError(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
        log.warn("Lobby error received: {}", errorDescription);
    }

    /**
     * Handles game server updates by updating the lobby model and refreshing the UI.
     *
     * @param glp update payload
     */
    @Override
    public void onGameServerUpdate(GameListPayload glp) {
        lobbyModel.updateLobby(glp);
        log.info("Processed game server update in lobby");
        updateUI();
    }

    @Override
    public void onPlayerStats(GameStatsPayload gsp) {
        int totalGames = gsp.games.size();
        int totalScore = gsp.games.stream().mapToInt(gameStat -> gameStat.score).sum();

        Platform.runLater(() -> {
            gamesPlayedLabel.setText("Total games played: " + totalGames);
            totalPairsLabel.setText("Total pairs found: " + totalScore);

            if (totalGames == 0) {
                gamesPlayedLabel.setVisible(false);
                totalPairsLabel.setVisible(false);
            }
        });
    }

    /**
     * Handles updates to the lobby room data.
     *
     * @param lup update payload
     */
    @Override
    public void onLobbyUpdate(LobbyUpdatePayload lup) {
        lobbyModel.updateGameRoom(lup);
        log.debug("Lobby room data updated");
    }

    /**
     * Refreshes the lobby UI: populates the table, updates button states.
     */
    public void updateUI() {
        lobbyTable.setItems(GameRoomManager.gameRooms);
        lobbyTable.refresh();

        Platform.runLater(() -> {
            LobbyUIUpdater.updateManageRoomButton(lobbyModel, manageRoomButton);
            LobbyUIUpdater.updateReadyButton(lobbyModel, readyButton);
        });

        log.debug("Lobby UI updated");
    }

    /**
     * Handles identity requests from the server. Sends the player's identity to the server.
     */
    @Override
    public void onRequestIdentity(String gameId) {
        lobbyModel.sendIdentity();
        log.info("Sent identity to server");
    }

    /**
     * Handles game start requests. Initializes the game model and navigates to the game screen.
     *
     * @param gup game data
     */
    @Override
    public void onStartGame(GameUpdatePayload gup) {
        lobbyModel.setCurrentRoomInProgress(true);
        Platform.runLater(() -> {
            lobbyModel.initializeGame(gup);
            navigator.closeConfirmationAlert();
            navigator.closeWindow();
            navigator.goToGame();
        });
        resultHandler.finalUnregister();
        log.info("Game start initiated");
    }
}