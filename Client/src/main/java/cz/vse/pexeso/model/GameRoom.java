package cz.vse.pexeso.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class GameRoom {
    public static ObservableList<GameRoom> gameRooms = FXCollections.observableArrayList(new ArrayList<>());
    private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;
    private String gameId = "";
    private String host;
    private int cardCount;
    private int capacity;

    public GameRoom(int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public GameRoom(GameStatus status, String gameId, String host, int cardCount, int capacity) {
        this.status = status;
        this.gameId = gameId;
        this.host = host;
        this.cardCount = cardCount;
        this.capacity = capacity;
    }

    public static void editGameRoom(String gameId, int cardCount, int capacity) {
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getGameId().equals(gameId)) {
                gameRoom.setCardCount(cardCount);
                gameRoom.setCapacity(capacity);
                break;
            }
        }
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
