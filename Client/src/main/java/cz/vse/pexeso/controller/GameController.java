package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.model.result.GameResultHandler;
import cz.vse.pexeso.model.result.GameResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameController implements GameResultListener {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final Navigator navigator;
    private final GameModel gameModel;

    private final GameResultHandler resultHandler;
    @FXML
    private Label titleLabel;
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

    public GameController(Navigator navigator, GameModel gameModel, Injector injector) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.resultHandler = injector.createGameResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();

        titleLabel.setText(gameModel.getPlayerName() + " " + gameModel.getPlayerId());

        gameModel.getGame().setupPlayerColors(gameModel.getPlayers());

        GameUIHelper.setupGameBoard(mainGridPane, gameModel.getGameBoard());

        GameUIHelper.setupOnClick(gameModel);

        GameUIHelper.setupScoreboard(scoreboardTable, playerColumn, scoreColumn, gameModel);

        updateUI();

        log.info("GameController initialized");
    }

    @Override
    public void onInvalidMove(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
    }

    @Override
    public void onGameUpdate(String data) {
        gameModel.updateGame(data);

        updateUI();
    }

    private void updateUI() {
        Platform.runLater(() -> {
            if (gameModel.isPlayersTurn()) {
                turnLabel.setText("Your turn");
            } else {
                turnLabel.setText("Opponent's turn");
            }
        });

        scoreboardTable.setItems(gameModel.getPlayers());
        scoreboardTable.refresh();

        scoreColumn.setSortType(TableColumn.SortType.DESCENDING);

        Platform.runLater(() -> {
            scoreboardTable.getSortOrder().clear();
            scoreboardTable.getSortOrder().add(scoreColumn);
            scoreboardTable.sort();
        });
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
