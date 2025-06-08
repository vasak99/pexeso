package cz.vse.pexeso.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a player in a lobby or game, including ID, username, readiness status, and score.
 *
 * @author kott10
 * @version June 2025
 */
public class LobbyPlayer {
    private static final Logger log = LoggerFactory.getLogger(LobbyPlayer.class);

    private final long playerId;
    private final String username;
    private PlayerStatus status;
    private final int score;

    /**
     * Constructs a LobbyPlayer with ID, username, readiness status, and score.
     *
     * @param playerId unique player ID
     * @param username player's username
     * @param status   readiness status
     * @param score    initial score
     */
    public LobbyPlayer(long playerId, String username, PlayerStatus status, int score) {
        this.playerId = playerId;
        this.username = username;
        this.status = status;
        this.score = score;
        log.info("Created LobbyPlayer[id={}, username={}]", playerId, username);
    }

    /**
     * Constructs a LobbyPlayer with ID, username, and score. Defaults status to NOT_READY.
     *
     * @param playerId unique player ID
     * @param username player's username
     * @param score    initial score (must be ≥ 0)
     */
    public LobbyPlayer(long playerId, String username, int score) {
        this(playerId, username, PlayerStatus.NOT_READY, score);
    }

    /**
     * @return the unique player ID
     */
    public Long getPlayerId() {
        return playerId;
    }

    /**
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the player’s readiness status
     */
    public PlayerStatus getStatus() {
        return status;
    }

    /**
     * Sets the player's readiness status.
     *
     * @param status new status
     */
    public void setStatus(PlayerStatus status) {
        this.status = status;
        log.debug("LobbyPlayer[{}] status set to {}", playerId, status);
    }

    /**
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Readiness status for a lobby player: READY or NOT_READY.
     */
    public enum PlayerStatus {
        READY("Ready"),
        NOT_READY("Not ready");

        private final String value;

        PlayerStatus(String value) {
            this.value = value;
        }

        /**
         * @return display text for this status
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Converts a boolean to PlayerStatus: READY if true, NOT_READY if false.
         *
         * @param statusBoolean readiness flag
         * @return corresponding PlayerStatus
         */
        public static PlayerStatus fromBoolean(boolean statusBoolean) {
            return statusBoolean ? READY : NOT_READY;
        }
    }
}