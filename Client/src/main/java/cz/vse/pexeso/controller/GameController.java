package cz.vse.pexeso.controller;

import cz.vse.pexeso.helper.AppServices;
import cz.vse.pexeso.helper.SceneManager;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.network.MessageBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private Button clickedCard;
    private String firstCardID;
    private String secondCardID;
    private Button firstCard;
    private Button secondCard;
    private int playerScore = 0;
    private int opponentScore = 0;
    private final List<Button> cardButtons = new ArrayList<>();
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
        cardButtons.add(card01);
        cardButtons.add(card02);
        cardButtons.add(card03);
        cardButtons.add(card04);
        cardButtons.add(card05);
        cardButtons.add(card06);
        cardButtons.add(card07);
        cardButtons.add(card08);
        cardButtons.add(card09);
        cardButtons.add(card10);
        cardButtons.add(card11);
        cardButtons.add(card12);
        cardButtons.add(card13);
        cardButtons.add(card14);
        cardButtons.add(card15);
        cardButtons.add(card16);

        //TODO: set card pairs, decide first player turn
        playerTurn = true;
        AppServices.getMessageHandler().register(MessageTypeClient.CARD_PAIR_OK, this::handleSuccessfulMatch);
        AppServices.getMessageHandler().register(MessageTypeClient.CARD_PAIR_INVALID, this::handleInvalidMatch);
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
            submitCard(1, firstCardID);

            log.debug("First card flipped: {}", firstCardID);
        } else {
            secondCard = clickedCard;
            secondCardID = secondCard.getId().substring(4);
            if (firstCardID.equals(secondCardID)) {
                return;
            }
            secondCard.setText("Flipped2");
            log.debug("Second card flipped: {}", secondCardID);
            submitCard(2, secondCardID);
        }
    }

    private void submitCard(int order, String cardID) {
        String message = MessageBuilder.buildSubmitCard(order, cardID);
        AppServices.getConnection().sendMessage(message);
        log.debug("Submitted card: {} - {}", order, secondCardID);
    }

    /**
     * Handles a successful match by updating the UI and player score, ends player's turn.
     */
    @FXML
    private void handleSuccessfulMatch() {

        if (playerTurn) {
            firstCard.setStyle("-fx-background-color: blue;");
            secondCard.setStyle("-fx-background-color: blue;");
            playerScore++;
            playerScoreLabel.setText(String.valueOf(playerScore));
            log.info("Successful match, player score: {}", playerScore);
        } else {
            firstCard.setStyle("-fx-background-color: red;");
            secondCard.setStyle("-fx-background-color: red;");
            opponentScore++;
            opponentScoreLabel.setText(String.valueOf(opponentScore));
            log.info("Successful match, opponent score: {}", opponentScore);
        }
        firstCard.setDisable(true);
        secondCard.setDisable(true);
        endPlayerTurn();
    }

    /**
     * Handles an invalid match by flipping the cards back, ends player's turn.
     */
    @FXML
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
        playerTurn = !playerTurn;

        log.info("End of player turn, waiting for opponent's move");
    }

    @FXML
    private void handleOpponentMove(String cardID) {
        log.debug("Handling opponent move for card: {}", cardID);
        Button opponentCard = getCardById(cardID);

        if (opponentCard == null) {
            log.error("Card with ID {} not found", cardID);
            return;
        }

        if (!isFirstCardFlipped) {
            firstCard = opponentCard;
            firstCard.setText("Flipped1");
            isFirstCardFlipped = true;
        } else {
            secondCard = opponentCard;
            secondCard.setText("Flipped2");
        }
    }

    private Button getCardById(String cardID) {
        for (Button card : cardButtons) {
            if (card.getId().equals("card" + cardID)) {
                return card;
            }
        }
        return null;
    }

    private void endGame() {
        // return to lobby or quit


        SceneManager.switchScene("/cz/vse/pexeso/fxml/lobby.fxml");
    }
}
