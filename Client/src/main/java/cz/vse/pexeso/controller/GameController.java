package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.model.result.GameResultHandler;
import cz.vse.pexeso.model.result.GameResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.util.Strings;
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
        gameModel.getGame().setupPlayerColors(gameModel.getPlayers());
        GameUIHelper.setup(mainGridPane, gameModel, scoreboardTable, playerColumn, scoreColumn);
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
        if (gameModel.getGame() == null) {
            return;
        }

        Platform.runLater(() -> {
            if (gameModel.isPlayersTurn()) {
                turnLabel.setText(Strings.YOUR_TURN);
            } else {
                turnLabel.setText(Strings.OPP_TURN);
            }
        });

        scoreboardTable.setItems(gameModel.getPlayers());
        scoreboardTable.refresh();
        GameUIHelper.setMainGridPaneSize(mainGridPane, gameModel.getGameBoard(), scoreboardTable);

        scoreColumn.setSortType(TableColumn.SortType.DESCENDING);

        Platform.runLater(() -> {
            scoreboardTable.getSortOrder().clear();
            scoreboardTable.getSortOrder().add(scoreColumn);
            scoreboardTable.sort();
        });
    }

    @Override
    public void onGameResult(String data) {
        mainGridPane.setDisable(true);
        gameModel.setInProgress(false);
        gameModel.setResult(data);
        Platform.runLater(navigator::openGameResultWindow);
    }

    @Override
    public void onGameError(String errorDescription) {
        Platform.runLater(() -> navigator.showError(errorDescription));
    }

    @Override
    public void onRedirect(String redirectData) {
        resultHandler.unregister();
        gameModel.redirect(redirectData);
        gameModel.getSession().setCurrentGameRoom(null);
        gameModel.getSession().setHostingAGameRoom(false);
        gameModel.getSession().setReady(false);
        if (gameModel.isRequestedToGiveUp()) {
            gameModel.setRequestedToGiveUp(false);
            Platform.runLater(navigator::goToLobby);
        } else {
            GameRoom gameRoom = gameModel.getSession().getCurrentGameRoom();
            GameRoom.gameRooms.remove(gameRoom);
        }
    }

    @Override
    public void onGameRoomUpdate(String data) {
        gameModel.updateGameRooms(data);
    }

    @FXML
    private void handleGiveUpClick() {
        if (navigator.showConfirmation(Strings.GIVE_UP_CONFIRMATION)) {
            gameModel.attemptGiveUp();
        }
    }
}
