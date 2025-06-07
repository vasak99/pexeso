package cz.vse.pexeso.model;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.util.updater.GameRoomUpdater;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a game room in the lobby. Allows updating from server payloads.
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoom {
    private static final Logger log = LoggerFactory.getLogger(GameRoom.class);

    private GameStatus status;
    private final String gameId;
    private String name;
    private long hostId;
    private String hostName;
    private int capacity;
    private int cardCount;
    private final ObservableList<LobbyPlayer> players = FXCollections.observableArrayList();
    private boolean inProgress = false;
    private Game game;

    /**
     * Constructs a GameRoom with the given parameters.
     *
     * @param status    initial game status
     * @param gameId    unique identifier for the room
     * @param name      display name of the room
     * @param hostId    player ID of the host
     * @param hostName  username of the host
     * @param capacity  maximum number of players
     * @param cardCount number of cards in the game
     */
    public GameRoom(GameStatus status,
                    String gameId,
                    String name,
                    long hostId,
                    String hostName,
                    int capacity,
                    int cardCount) {
        this.status = status;
        this.gameId = gameId;
        this.name = name;
        this.hostId = hostId;
        this.hostName = hostName;
        this.capacity = capacity;
        this.cardCount = cardCount;
        log.info("Created GameRoom[id={}, name={}]", gameId, name);
    }

    /**
     * @return the current status of the game room
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Sets the game room status.
     *
     * @param status new status
     */
    public void setStatus(GameStatus status) {
        this.status = status;
        log.debug("GameRoom[{}] status set to {}", gameId, status);
    }

    /**
     * @return the unique identifier for this game room
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @return the display name of this game room
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the game room name.
     *
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
        log.debug("GameRoom[{}] name set to {}", gameId, name);
    }

    /**
     * @return the player ID of the host
     */
    public long getHostId() {
        return hostId;
    }

    /**
     * Sets the host player ID.
     *
     * @param hostId new host ID
     */
    public void setHostId(long hostId) {
        this.hostId = hostId;
        log.debug("GameRoom[{}] hostId set to {}", gameId, hostId);
    }

    /**
     * @return the username of the host, needed by gameRoomTable in lobby
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the host's username.
     *
     * @param hostName new host username
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
        log.debug("GameRoom[{}] hostName set to {}", gameId, hostName);
    }

    /**
     * @return the maximum number of players allowed
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the maximum number of players.
     *
     * @param capacity new capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        log.debug("GameRoom[{}] capacity set to {}", gameId, capacity);
    }

    /**
     * @return the number of cards in this game
     */
    public int getCardCount() {
        return cardCount;
    }

    /**
     * Sets the number of cards for this game.
     *
     * @param cardCount new card count
     */
    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
        log.debug("GameRoom[{}] cardCount set to {}", gameId, cardCount);
    }

    /**
     * @return an observable list of players currently in the room
     */
    public ObservableList<LobbyPlayer> getPlayers() {
        return players;
    }

    /**
     * @return true if the game is in progress
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * Marks the game as in progress or not.
     *
     * @param inProgress true if game has started
     */
    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
        log.debug("GameRoom[{}] inProgress set to {}", gameId, inProgress);
    }

    /**
     * @return the Game instance associated once the game starts, or null if not started
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the game.
     *
     * @param game the Game to set
     */
    public void setGame(Game game) {
        this.game = game;
        log.info("Set new game");
    }

    /**
     * Checks if a given player ID matches the host.
     *
     * @param playerId ID to check
     * @return true if playerId equals hostId
     */
    public boolean isHost(long playerId) {
        return this.hostId == playerId;
    }

    /**
     * Returns a list of players excluding the host.
     *
     * @return filtered list of non-host players
     */
    public ObservableList<LobbyPlayer> getNonHostPlayers() {
        return players.filtered(p -> p.getPlayerId() != hostId);
    }

    /**
     * Checks if all non-host players are ready. At least two players must be present to start the game.
     *
     * @return true if every non-host player’s status is READY and at least 2 players are in the room
     */
    public boolean areAllPlayersReady() {
        if (players.size() < 2) {
            return false;
        }
        for (LobbyPlayer lobbyPlayer : players) {
            if (lobbyPlayer.getStatus() == LobbyPlayer.PlayerStatus.NOT_READY && !isHost(lobbyPlayer.getPlayerId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates this GameRoom using server data.
     *
     * @param lup payload from the server
     */
    public void update(LobbyUpdatePayload lup) {
        GameRoomUpdater.update(this, lup);
    }

    /**
     * Status of a GameRoom: either waiting or in progress.
     */
    public enum GameStatus {
        WAITING_FOR_PLAYERS("Waiting for players"),
        IN_PROGRESS("In progress");

        private final String value;

        GameStatus(String value) {
            this.value = value;
        }

        /**
         * @return display text for this status
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Converts a boolean flag into a status enum.
         *
         * @param statusBoolean true if in progress
         * @return IN_PROGRESS if true, otherwise WAITING_FOR_PLAYERS
         */
        public static GameStatus fromBoolean(boolean statusBoolean) {
            return statusBoolean ? IN_PROGRESS : WAITING_FOR_PLAYERS;
        }
    }

    /**
     * Default board size options or custom
     */
    public enum BoardSize {
        SMALL(16),
        MEDIUM(32),
        LARGE(48),
        CUSTOM(0);

        public final int value;

        BoardSize(int value) {
            this.value = value;
        }

        /**
         * Finds the matching BoardSize for a given value, or returns CUSTOM if none match.
         *
         * @param value number of cards
         * @return corresponding BoardSize
         */
        public static BoardSize fromValue(int value) {
            for (BoardSize boardSize : BoardSize.values()) {
                if (boardSize.value == value) {
                    return boardSize;
                }
            }
            return BoardSize.CUSTOM;
        }

        /**
         * Returns a display string, e.g. “SMALL(16)” or “CUSTOM(10)”.
         *
         * @param value number of cards
         * @return display text
         */
        public static String returnDisplayText(int value) {
            BoardSize boardSize = fromValue(value);
            return String.format("%s(%d)", boardSize, value);
        }
    }
}