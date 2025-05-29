package cz.vse.pexeso.model;

public class LobbyPlayer {
    private long playerId;
    private String username;
    private PlayerStatus status;
    private int score;

    public LobbyPlayer(long playerId, String username, LobbyPlayer.PlayerStatus status, int score) {
        this.playerId = playerId;
        this.username = username;
        this.status = status;
        this.score = score;
    }

    public LobbyPlayer(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public enum PlayerStatus {
        READY("Ready"),
        NOT_READY("Not ready");

        private final String value;

        PlayerStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static PlayerStatus fromBoolean(boolean statusBoolean) {
            if (statusBoolean) {
                return READY;
            } else {
                return NOT_READY;
            }
        }
    }
}
