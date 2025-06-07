package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;

/**
 * Listener interface for game room creator/manager result events. Implementations will be notified
 * when the server sends redirect, game rooms update, or error messages during game room creation/management.
 *
 * @author kott10
 * @version June 2025
 */
public interface GameRoomResultListener {
    /**
     * Called when the server instructs the client to redirect to a different host/port.
     *
     * @param parameters redirect data
     */
    void onRedirect(RedirectParameters parameters);

    /**
     * Called when the server sends current game room update.
     *
     * @param lup current room update data
     */
    void onLobbyUpdate(LobbyUpdatePayload lup);

    /**
     * Called when the server sends an error.
     *
     * @param errorDescription an error message
     */
    void onError(String errorDescription);
}