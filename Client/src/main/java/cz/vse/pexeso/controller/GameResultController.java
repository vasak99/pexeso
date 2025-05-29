package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class GameResultController {
    private final Navigator navigator;
    private final GameModel gameModel;
    @FXML
    private VBox vBox;


    public GameResultController(Navigator navigator, GameModel gameModel) {
        this.navigator = navigator;
        this.gameModel = gameModel;
    }

    @FXML
    private void initialize() {
        for (LobbyPlayer lobbyPlayer : gameModel.getGame().getResultList()) {
            GameUIHelper.addPlayerRow(vBox, lobbyPlayer);
        }
    }

    @FXML
    private void handleGoToLobbyClick() {
        gameModel.redirectToLobby();
        navigator.closeWindow();
        navigator.goToLobby();
    }

    @FXML
    private void handleExitAppClick() {
        Platform.exit();
    }
}

