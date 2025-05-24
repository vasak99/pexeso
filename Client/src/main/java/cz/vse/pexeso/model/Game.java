package cz.vse.pexeso.model;

import cz.vse.pexeso.common.message.payload.SendablePlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Game {
    private String GameBoard;
    private long activePlayer;
    private final ObservableList<SendablePlayer> resultList = FXCollections.observableArrayList();
    private SendablePlayer winningPlayer;

    public Game() {
    }

    public Game(String gameBoard, long activePlayer) {
        GameBoard = gameBoard;
        this.activePlayer = activePlayer;
    }

    public String getGameBoard() {
        return GameBoard;
    }

    public void setGameBoard(String gameBoard) {
        GameBoard = gameBoard;
    }

    public long getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(long activePlayer) {
        this.activePlayer = activePlayer;
    }

    public ObservableList<SendablePlayer> getResultList() {
        return resultList;
    }

    public SendablePlayer getWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(SendablePlayer winningPlayer) {
        this.winningPlayer = winningPlayer;
    }
}
