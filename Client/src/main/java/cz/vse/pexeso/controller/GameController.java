package cz.vse.pexeso.controller;

import cz.vse.pexeso.helper.AppServices;
import cz.vse.pexeso.model.observer.MessageType;
import cz.vse.pexeso.network.MessageBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    Button clickedCard;
    String firstCardID;
    String secondCardID;
    Button firstCard;
    Button secondCard;
    int playerScore = 0;
    int opponentScore = 0;
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
    private boolean playerTurn;
    private boolean isFirstCardFlipped = false;

    @FXML
    private void initialize() {
        //TODO: set card pairs, decide first player turn
        playerTurn = true;
        AppServices.getMessageHandler().register(MessageType.CARD_PAIR_OK, this::handleSuccessfulMatch);
        AppServices.getMessageHandler().register(MessageType.CARD_PAIR_INVALID, this::handleInvalidMatch);
        log.info("GameController initialized");
    }

    /**
     * Handles the card click event, flips the cards and checks for matches.
     *
     * @param mouseEvent the mouse event triggered by clicking a card
     */
    @FXML
    private void handleCardClicked(MouseEvent mouseEvent) {
        if (!playerTurn) {
            return;
        }

        clickedCard = (Button) mouseEvent.getSource();

        if (!isFirstCardFlipped) {
            firstCard = clickedCard;
            firstCardID = firstCard.getId().substring(4);

            firstCard.setText("Flipped1");
            isFirstCardFlipped = true;
            log.debug("First card flipped: {}", firstCardID);
        } else {
            secondCard = clickedCard;
            secondCardID = secondCard.getId().substring(4);
            if (firstCardID.equals(secondCardID)) {
                return;
            }
            secondCard.setText("Flipped2");
            log.debug("Second card flipped: {}", secondCardID);
            submitCardPair(firstCardID, secondCardID);
        }
    }

    /**
     * Submits the selected card pair to the server for validation.
     *
     * @param firstCardID  the ID of the first card
     * @param secondCardID the ID of the second card
     */
    private void submitCardPair(String firstCardID, String secondCardID) {
        String message = MessageBuilder.getInstance().buildSubmitCardPairMessage(firstCardID, secondCardID);
        AppServices.getConnection().sendMessage(message);
        log.debug("Submitted card pair: {} and {}", firstCardID, secondCardID);
    }

    /**
     * Handles a successful match by updating the UI and player score, ends player's turn.
     */
    private void handleSuccessfulMatch() {
        firstCard.setStyle("-fx-background-color: blue;");
        secondCard.setStyle("-fx-background-color: blue;");
        firstCard.setDisable(true);
        secondCard.setDisable(true);

        playerScore++;
        playerScoreLabel.setText(String.valueOf(playerScore));
        log.info("Successful match, player score: {}", playerScore);
        endPlayerTurn();
    }

    /**
     * Handles an invalid match by flipping the cards back, ends player's turn.
     */
    private void handleInvalidMatch() {
        firstCard.setText("");
        secondCard.setText("");
        log.info("Invalid match, cards flipped back");
        endPlayerTurn();
    }

    /**
     * Ends the player's turn, resetting the game state for the next turn.
     */
    private void endPlayerTurn() {
        clickedCard = null;

        firstCard = null;
        firstCardID = null;

        secondCard = null;
        secondCardID = null;

        isFirstCardFlipped = false;
        playerTurn = false;

        log.info("End of player turn, waiting for opponent's move");
    }

    //TODO: handle opponent's turn


}
