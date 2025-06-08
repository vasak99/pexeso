package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.payload.GameListPayload;

/**
 * Listener interface for auth result events. Implementations will be notified
 * when the server sends identity, game server updates, or error messages during authentication.
 *
 * @author kott10
 * @version June 2025
 */
public interface AuthResultListener {
    /**
     * Called when the server returns the player's (ID).
     *
     * @param playerId the ID assigned by the server
     */
    void onIdentity(long playerId);

    /**
     * Called when the server instructs the client to update the game room list.
     *
     * @param data game room data
     */
    void onGameServerUpdate(GameListPayload data);

    /**
     * Called when the server returns an error message during authentication.
     *
     * @param errorMessage an error message
     */
    void onError(String errorMessage);
}
