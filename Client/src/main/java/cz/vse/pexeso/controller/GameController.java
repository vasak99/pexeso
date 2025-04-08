package cz.vse.pexeso.controller;

import cz.vse.pexeso.helper.AppServices;
import cz.vse.pexeso.model.observer.MessageType;
import cz.vse.pexeso.network.MessageBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class GameController {
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
    private boolean isFirstCardFlipped;
    String firstCardID;
    String secondCardID;
    Button firstCard;
    Button secondCard;
    int playerScore = 0;
    int opponentScore = 0;

    @FXML
    private void initialize() {
        //TODO: set card pairs, decide first player turn
        playerTurn = true;
        isFirstCardFlipped = false;
        AppServices.getMessageHandler().register(MessageType.CARD_PAIR_OK, this::handleSuccessfulMatch);
        AppServices.getMessageHandler().register(MessageType.CARD_PAIR_INVALID, this::handleInvalidMatch);
    }

    @FXML
    private void handleCardClicked(MouseEvent mouseEvent) {
        if (!playerTurn) {
            return;
        }

        if (!isFirstCardFlipped) {
            firstCardID = ((Button) mouseEvent.getSource()).getId().substring(4);
            firstCard = (Button) mouseEvent.getSource();
//            firstCard.setText("Flipped1");
            isFirstCardFlipped = true;
        } else {
            secondCardID = ((Button) mouseEvent.getSource()).getId().substring(4);
            if (firstCardID.equals(secondCardID)) {
                return;
            }
            secondCard = (Button) mouseEvent.getSource();
//            secondCard.setText("Flipped2");

            submitCardPair(firstCardID, secondCardID);
        }
    }

    private void submitCardPair(String firstCardID, String secondCardID) {
        String message = MessageBuilder.getInstance().buildSubmitCardPairMessage(firstCardID, secondCardID);
        AppServices.getConnection().sendMessage(message);
    }

    private void handleSuccessfulMatch() {
        firstCard.setStyle("-fx-background-color: blue;");
        secondCard.setStyle("-fx-background-color: blue;");
        firstCard.setDisable(true);
        secondCard.setDisable(true);

        playerScore++;
        playerScoreLabel.setText(String.valueOf(playerScore));
        endOfPlayerTurn();
    }

    private void handleInvalidMatch() {
        firstCard.setText("");
        secondCard.setText("");
        endOfPlayerTurn();
    }


    private void endOfPlayerTurn() {
        firstCardID = null;
        secondCardID = null;
        isFirstCardFlipped = false;
        playerTurn = false;
    }

    //TODO: handle opponent's turn


}
