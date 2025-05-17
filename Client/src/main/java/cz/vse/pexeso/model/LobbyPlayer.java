package cz.vse.pexeso.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class LobbyPlayer {
    public static final ObservableList<LobbyPlayer> lobbyPlayers = FXCollections.observableArrayList(new ArrayList<>());

    private long playerId;
    private String username;
    private PlayerStatus status;
    private String currentGameId;

    public LobbyPlayer(long playerId, String username, String currentGameId) {
        this.playerId = playerId;
        this.username = username;
        this.currentGameId = currentGameId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
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
    }
}
