package cz.vse.pexeso.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the authenticated client’s session, including player ID, credentials,
 * current game room, and readiness.
 *
 * @author kott10
 * @version June 2025
 */
public class ClientSession {
    private static final Logger log = LoggerFactory.getLogger(ClientSession.class);
    private final long playerId;
    private final UserCredentials userCredentials;
    private GameRoom currentGameRoom;
    private boolean ready;

    /**
     * Constructs a ClientSession with the given playerId and credentials.
     *
     * @param playerId        unique identifier assigned by the server
     * @param userCredentials the credentials used to log in
     */
    public ClientSession(long playerId, UserCredentials userCredentials) {
        this.playerId = playerId;
        this.userCredentials = userCredentials;
        this.currentGameRoom = null;
    }

    /**
     * @return the unique player ID for this session
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * @return the username associated with this session
     */
    public String getPlayerName() {
        return userCredentials.username();
    }

    /**
     * @return the GameRoom the client is currently in, or null if not in any room
     */
    public GameRoom getCurrentGameRoom() {
        return currentGameRoom;
    }

    /**
     * Sets the GameRoom for this session.
     *
     * @param currentGameRoom the GameRoom to set
     */
    public void setCurrentGameRoom(GameRoom currentGameRoom) {
        log.debug("Setting current game room to {}", currentGameRoom);
        this.currentGameRoom = currentGameRoom;
    }

    /**
     * @return true if the client has marked themselves as ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Sets the ready flag for this session.
     *
     * @param ready true if the client is ready for the game
     */
    public void setReady(boolean ready) {
        log.debug("Setting ready state to {}", ready);
        this.ready = ready;
    }

    /**
     * @return true if the client is currently in a GameRoom
     */
    public boolean isInRoom() {
        return currentGameRoom != null;
    }

    /**
     * @return true if the client is hosting the current game room
     */
    public boolean isHostingAGameRoom() {
        return isInRoom() && currentGameRoom.getHostId() == this.playerId;
    }

    /**
     * @return true if the client is in a room and the room is in progress
     */
    public boolean isRoomInProgress() {
        return isInRoom() && currentGameRoom.isInProgress();
    }
}
