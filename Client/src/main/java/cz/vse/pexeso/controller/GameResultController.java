package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameResultHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the game results window. Displays final scores and provides options to return to lobby or exit.
 *
 * @author kott10
 * @version June 2025
 */
public class GameResultController {
    private static final Logger log = LoggerFactory.getLogger(GameResultController.class);

    private final Navigator navigator;
    private final GameModel gameModel;

    @FXML
    private VBox resultVBox;

    /**
     * Constructs a GameResultController with required dependencies.
     *
     * @param navigator for navigation
     * @param gameModel for accessing result list
     */
    public GameResultController(Navigator navigator, GameModel gameModel) {
        this.navigator = navigator;
        this.gameModel = gameModel;
    }

    /**
     * Initializes the result window by populating scores and setting a close handler.
     */
    @FXML
    private void initialize() {
        GameResultHelper.setupResult(resultVBox, gameModel.getResultList());
        log.info("GameResultController initialized with result data");
    }

    /**
     * Handles click on the “Go to Lobby” button by closing this window.
     */
    @FXML
    private void handleGoToLobbyClick() {
        log.info("Navigation to lobby from result window.");
        navigator.closeWindow();
    }

    /**
     * Handles click on the “Exit App” button by exiting the JavaFX platform.
     */
    @FXML
    private void handleExitAppClick() {
        log.info("Exiting application from result window.");
        Platform.exit();
    }
}