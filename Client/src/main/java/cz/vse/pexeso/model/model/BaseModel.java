package cz.vse.pexeso.model.model;

import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;

/**
 * Abstract base for all models. Provides utility methods.
 *
 * @author kott10
 * @version June 2025
 */
public abstract class BaseModel {
    protected final SessionService sessionService;
    protected final RedirectService redirectService;

    /**
     * Constructs a BaseModel with the given services.
     *
     * @param sessionService  service to manage client sessions
     * @param redirectService service to handle redirects
     */
    protected BaseModel(SessionService sessionService, RedirectService redirectService) {
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    /**
     * Returns the current client session.
     *
     * @return the current ClientSession
     */
    protected ClientSession getSession() {
        return sessionService.getSession();
    }

    /**
     * Redirects to a new page using the provided parameters.
     *
     * @param parameters the parameters for the redirect
     */
    public void redirect(RedirectParameters parameters) {
        redirectService.redirect(parameters);
    }

    /**
     * Returns the ID of the player associated with the current session.
     *
     * @return the player ID
     */
    public Long getPlayerId() {
        return getSession().getPlayerId();
    }

    /**
     * Returns the name of the player associated with the current session.
     *
     * @return the player name
     */
    public String getPlayerName() {
        return getSession().getPlayerName();
    }

    /**
     * Returns the current game room associated with the session.
     *
     * @return the current GameRoom, or null if not in a room
     */
    protected GameRoom getCurrentGameRoom() {
        return getSession().getCurrentGameRoom();
    }

    /**
     * Returns current game room ID
     *
     * @return game id if current room exists and gameId is not null or blank, null otherwise
     */
    public String getCurrentGameRoomId() {
        if (!isInRoom()) {
            return null;
        }
        String gameId = getCurrentGameRoom().getGameId();
        if (gameId == null || gameId.isBlank()) {
            return null;
        }
        return gameId;
    }

    /**
     * Checks if the current session is in a game room.
     *
     * @return true if in a game room, false otherwise
     */
    public boolean isInRoom() {
        return getSession().isInRoom();
    }

    /**
     * Clears the current game room from the session.
     */
    public void clearCurrentGameRoom() {
        getSession().setCurrentGameRoom(null);
    }

    /**
     * Checks if the current game room ID is null.
     *
     * @return true if the current game room ID is null, false otherwise
     */
    protected boolean isCurrentRoomIdNull() {
        return getCurrentGameRoomId() == null;
    }

}