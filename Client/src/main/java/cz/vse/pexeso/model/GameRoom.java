package cz.vse.pexeso.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameRoom {
    public static final ObservableList<GameRoom> gameRooms = FXCollections.observableArrayList();

    private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;
    private String gameId;
    private String name;
    private long hostId;
    private String hostName;
    private int capacity;
    private int cardCount;
    private final ObservableList<LobbyPlayer> players = FXCollections.observableArrayList();

    private Game game;

    // Create/Edit attempt message
    public GameRoom(String gameId, String name, int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
        this.gameId = gameId;
        this.name = name;
    }

    // finalize Creation
    public GameRoom(String gameId, String name, long hostId, String hostName, int capacity, int cardCount) {
        this.hostId = hostId;
        this.capacity = capacity;
        this.cardCount = cardCount;
        this.gameId = gameId;
        this.name = name;
        this.hostName = hostName;
    }

    // updater
    public GameRoom(GameStatus status, String gameId, String name, long hostId, String hostName, int capacity, int cardCount) {
        this.status = status;
        this.gameId = gameId;
        this.name = name;
        this.hostId = hostId;
        this.hostName = hostName;
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public static void editGameRoom(String gameId, String name, int capacity, int cardCount) {
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getGameId().equals(gameId)) {
                gameRoom.setName(name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservableList<LobbyPlayer> getPlayers() {
        return players;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public enum GameStatus {
        WAITING_FOR_PLAYERS("Waiting for players"),
        IN_PROGRESS("In progress");

        private final String value;

        GameStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static GameStatus fromBoolean(boolean statusBoolean) {
            if (statusBoolean) {
                return IN_PROGRESS;
            } else {
                return WAITING_FOR_PLAYERS;
            }
        }
    }

    public enum BoardSize {
        SMALL(16),
        MEDIUM(36),
        LARGE(64),
        CUSTOM(0);

        public final int value;

        BoardSize(int value) {
            this.value = value;
        }

        public static BoardSize fromValue(int value) {
            for (BoardSize boardSize : BoardSize.values()) {
                if (boardSize.value == value) {
                    return boardSize;
                }
            }
            return BoardSize.CUSTOM;
        }

        public static String returnDisplayText(int value) {
            BoardSize boardSize = fromValue(value);
            return boardSize + "(" + value + ")";
        }
    }
}
