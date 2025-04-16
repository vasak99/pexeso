package cz.vse.pexeso.model;

public class ClientSession {
    private final String playerId;
    private final User user;

    public ClientSession(String playerId, User user) {
        this.playerId = playerId;
        this.user = user;
    }

    public String getPlayerId() {
        return playerId;
    }

    public User getUser() {
        return user;
    }
}
