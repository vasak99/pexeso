package cz.vse.pexeso.controller;

import cz.vse.pexeso.helper.SceneManager;

public class LobbyController {
    //TODO: Implement matchmaking

    private void joinGame() {
        SceneManager.switchScene("/cz/vse/pexeso/fxml/game.fxml");
    }
}
