package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.model.result.GameResultHandler;
import cz.vse.pexeso.model.result.GameResultListener;
import cz.vse.pexeso.navigation.Navigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameController implements GameResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final Navigator navigator;
    private final GameModel gameModel;

    private final GameResultHandler resultHandler;

    public GameController(Navigator navigator, GameModel gameModel, Injector injector) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.resultHandler = injector.createGameResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();
        log.info("GameController initialized");
    }

    @Override
    public void onInvalidMove(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
    }

    @Override
    public void onGameUpdate(String data) {
        gameModel.updateGame(data);
    }

    @Override
    public void onGameResult(String data) {
        gameModel.setResult(data);
        // open result window
    }

    @Override
    public void onGameError(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
    }
}
