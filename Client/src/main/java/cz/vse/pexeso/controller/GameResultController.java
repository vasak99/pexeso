package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.view.helper.GameUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        GameUIHelper.setupResult(vBox, gameModel.getResultList());

        Platform.runLater(() -> {
            Stage stage = (Stage) vBox.getScene().getWindow();
            stage.setOnCloseRequest(event -> navigator.goToLobby());
        });
    }

    @FXML
    private void handleGoToLobbyClick() {
        navigator.closeWindow();
        navigator.goToLobby();
    }

    @FXML
    private void handleExitAppClick() {
        Platform.exit();
    }
}

