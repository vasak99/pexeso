package cz.vse.pexeso.controller;

import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @FXML
    private void initialize() {
        log.info("GameController initialized");
    }
}
