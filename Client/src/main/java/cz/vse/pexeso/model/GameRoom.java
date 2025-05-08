package cz.vse.pexeso.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameRoom {
    public static final ObservableList<GameRoom> gameRooms = FXCollections.observableArrayList();

    private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;
    private String gameId = "";
    private long hostId;
    private int capacity;
    private int cardCount;
    private List<LobbyPlayer> players;

    public GameRoom(int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public GameRoom(long hostId, int capacity, int cardCount) {
        this.hostId = hostId;
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public GameRoom(String gameId, long hostId, int capacity, int cardCount) {
        this.gameId = gameId;
        this.hostId = hostId;
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public GameRoom(GameStatus status, String gameId, long hostId, int capacity, int cardCount) {
        this.status = status;
        this.gameId = gameId;
        this.hostId = hostId;
        this.capacity = capacity;
        this.cardCount = cardCount;

    }

    public static void editGameRoom(String gameId, int capacity, int cardCount) {
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getGameId().equals(gameId)) {
                gameRoom.setCapacity(capacity);
                gameRoom.setCardCount(cardCount);
                break;
            }
        }
    }

    public static GameRoom findById(String gameId) {
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getGameId().equals(gameId)) {
                return gameRoom;
            }
        }
        return null;
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

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

    public List<LobbyPlayer> getPlayers() {
        return players;
    }
}
