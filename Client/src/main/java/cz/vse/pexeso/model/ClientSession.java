package cz.vse.pexeso.model;

public class ClientSession {
    private final long playerId;
    private final UserCredentials userCredentials;
    private GameRoom currentGameRoom;

    public ClientSession(long playerId, UserCredentials userCredentials) {
        this.playerId = playerId;
        this.userCredentials = userCredentials;
    }

    public long getPlayerId() {
        return playerId;
    }

    public GameRoom getCurrentGameRoom() {
        return currentGameRoom;
    }

    public void setCurrentGameRoom(GameRoom currentGameRoom) {
        this.currentGameRoom = currentGameRoom;
    }
}
