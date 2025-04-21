package cz.vse.pexeso.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private Button clickedCard;

    @FXML
    private Label playerScoreLabel;
    @FXML
    private Label opponentScoreLabel;
    @FXML
    private Button card01;
    @FXML
    private Button card02;
    @FXML
    private Button card03;
    @FXML
    private Button card04;
    @FXML
    private Button card05;
    @FXML
    private Button card06;
    @FXML
    private Button card07;
    @FXML
    private Button card08;
    @FXML
    private Button card09;
    @FXML
    private Button card10;
    @FXML
    private Button card11;
    @FXML
    private Button card12;
    @FXML
    private Button card13;
    @FXML
    private Button card14;
    @FXML
    private Button card15;
    @FXML
    private Button card16;

    @FXML
    private void initialize() {
        log.info("GameController initialized");
    }

    public void handleCardClicked(MouseEvent mouseEvent) {
        clickedCard = (Button) mouseEvent.getSource();
    }
}
