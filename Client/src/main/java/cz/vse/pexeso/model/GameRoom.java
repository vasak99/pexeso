package cz.vse.pexeso.model;

public class GameRoom {
    private String gameId;
    private String hostId;
    private int capacity;
    private int cardCount;
    private int currentNumberOfPlayers;
    private GameStatus status;

    public GameRoom(int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public GameRoom(String gameId, String hostId, int cardCount, int capacity, int currentNumberOfPlayers, GameStatus status) {
        this.gameId = gameId;
        this.hostId = hostId;
        this.cardCount = cardCount;
        this.capacity = capacity;
        this.currentNumberOfPlayers = currentNumberOfPlayers;
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
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

    public int getCurrentNumberOfPlayers() {
        return currentNumberOfPlayers;
    }

    public void setCurrentNumberOfPlayers(int currentNumberOfPlayers) {
        this.currentNumberOfPlayers = currentNumberOfPlayers;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}
