package cz.vse.pexeso.model;

public class ClientSession {
    private final long playerId;
    private final UserCredentials userCredentials;
    private GameRoom currentGameRoom;
    private boolean hostingAGameRoom;
    private boolean ready;

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

    public boolean isHostingAGameRoom() {
        return hostingAGameRoom;
    }

    public void setHostingAGameRoom(boolean hostingAGameRoom) {
        this.hostingAGameRoom = hostingAGameRoom;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
