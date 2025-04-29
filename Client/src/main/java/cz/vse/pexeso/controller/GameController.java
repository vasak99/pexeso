package cz.vse.pexeso.controller;

import cz.vse.pexeso.util.SceneManager;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final SceneManager sceneManager = SceneManager.getInstance();

    @FXML
    private void initialize() {
        log.info("GameController initialized");
    }
}
