package cz.vse.pexeso.model;

import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.view.Board;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Game {
    private Board gameBoard = null;
    private long activePlayer;
    private final ObservableList<SendablePlayer> resultList = FXCollections.observableArrayList();
    private SendablePlayer winningPlayer;

    public Game() {
    }

    public Game(Board gameBoard, long activePlayer) {
        this.gameBoard = gameBoard;
        this.activePlayer = activePlayer;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
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
