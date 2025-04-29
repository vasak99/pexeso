package cz.vse.pexeso.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class LobbyPlayer {
    public static final ObservableList<LobbyPlayer> lobbyPlayers = FXCollections.observableArrayList(new ArrayList<>());
    private long playerId;
    private String username;
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

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }
}
