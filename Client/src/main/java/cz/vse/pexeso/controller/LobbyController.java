package cz.vse.pexeso.controller;

import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyController {
    //TODO: Implement matchmaking
    public static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private void joinGame() {
        log.info("Joining game");
        SceneManager.switchScene(UIConstants.GAME_FXML);
    }
}
