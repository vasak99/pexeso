package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.model.result.GameResultHandler;
import cz.vse.pexeso.model.result.GameResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameScoreboardHelper;
import cz.vse.pexeso.view.helper.GameUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controller for the main game screen. Handles rendering the game board, scoreboard updates,
 * giving up, and reacts to server events via GameResultListener. Sets up click handlers on cards
 * through helper classes.
 *
 * @author kott10
 * @version June 2025
 */
public class GameController implements GameResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final Navigator navigator;
    private final GameModel gameModel;
    private final GameResultHandler resultHandler;

    @FXML
    private TableView<LobbyPlayer> scoreboardTable;
    @FXML
    private TableColumn<LobbyPlayer, String> playerColumn;
    @FXML
    private TableColumn<LobbyPlayer, Integer> scoreColumn;
    @FXML
    private GridPane mainGridPane;
    @FXML
    private Label turnLabel;

    /**
     * Constructs a GameController with necessary dependencies.
     *
     * @param navigator for navigation callbacks
     * @param gameModel model handling game state
     * @param injector  to obtain GameResultHandler
     */
    public GameController(Navigator navigator, GameModel gameModel, Injector injector) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.resultHandler = injector.getHandlerFactory().createGameResultHandler(this);
    }

    /**
     * Initializes the game screen: registers result handler, sets up the board and scoreboard,
     * and updates UI labels. Logs any exceptions during setup.
     */
    @FXML
    private void initialize() {
        try {
            resultHandler.register();

            setupGameBoard();
            GameScoreboardHelper.initialize(scoreboardTable, playerColumn, scoreColumn, gameModel);
            updateUI();
            log.info("GameController initialized");
        } catch (Exception e) {
            log.error("Exception during GameController initialization", e);
        }
    }

    /**
     * Sets up the game board UI by adding the Board node to the grid and configuring click handlers.
     */
    private void setupGameBoard() {
        mainGridPane.add(gameModel.getGameBoard(), 1, 0);
        GameUIHelper.setupOnClick(gameModel);
        GameUIHelper.setWindowSize(mainGridPane, scoreboardTable, gameModel);
        log.info("Game board added to UI");
    }

    /**
     * Handles the Give Up button click: shows confirmation and sends give-up request if confirmed.
     */
    @FXML
    private void handleGiveUpClick() {
        if (navigator.showConfirmation("Are you sure you want to give up?")) {
            gameModel.attemptGiveUp();
            log.info("Give-up request sent");
        }
    }

    /**
     * Handles invalid move message, displays error
     */
    @Override
    public void onInvalidMove(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
        log.warn("Invalid move received: {}", errorDescription);
    }

    /**
     * Handles game update events by updating the game model and refreshing the UI.
     *
     * @param gup update payload
     */
    @Override
    public void onGameUpdate(GameUpdatePayload gup) {
        Platform.runLater(() -> {
            gameModel.updateGame(gup);
            updateUI();
            log.debug("Game updated with data={}", gup);
        });
    }

    /**
     * Refreshes UI elements: turn label and scoreboard.
     */
    private void updateUI() {
        Platform.runLater(() -> {
            GameUIHelper.updateTurnLabel(turnLabel, gameModel);
            GameScoreboardHelper.updateScoreboard(scoreboardTable, gameModel);
            GameScoreboardHelper.sortScoreboard(scoreboardTable, scoreColumn);
            log.debug("GameController UI updated");
        });
    }

    /**
     * Handles the game result by setting the game as not in progress and opening the result window.
     */
    @Override
    public void onResult() {
        gameModel.setInProgress(false);
        Platform.runLater(() -> {
            navigator.openGameResultWindow();
            log.info("Result window opened");
        });
    }

    /**
     * Handles game error messages by displaying them in the UI.
     *
     * @param errorDescription description of the error
     */
    @Override
    public void onError(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
        log.warn("Game error received: {}", errorDescription);
    }

    /**
     * Handles redirection requests by unregistering the result handler and leaving the room.
     *
     * @param parameters redirect parameters
     */
    @Override
    public void onRedirect(RedirectParameters parameters) {
        resultHandler.unregister();
        gameModel.leaveRoom(parameters);
        Platform.runLater(() -> {
            navigator.goToLobby();
            log.info("Redirected to lobby with data={}", parameters);
        });
    }

    /**
     * Handles game server updates by updating the game model with new room data.
     *
     * @param glp update payload
     */
    @Override
    public void onGameServerUpdate(GameListPayload glp) {
        gameModel.updateGameRooms(glp);
        log.info("Game server rooms updated via GameController");
    }
}