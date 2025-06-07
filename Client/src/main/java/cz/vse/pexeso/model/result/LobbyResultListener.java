package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;

/**
 * Listener interface for game result events. Implementations will be notified
 * when the server sends game update, error, result, redirect or game rooms update during the game.
 *
 * @author kott10
 * @version June 2025
 */
public interface LobbyResultListener {

    /**
     * Called when the server requests the client’s identity.
     */
    void onRequestIdentity();

    /**
     * Called when the server indicates that the game is starting. Provides game data.
     *
     * @param gup game data
     */
    void onStartGame(GameUpdatePayload gup);

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
     * Called when the server instructs the client to update the game room list.
     *
     * @param glp game room data
     */
    void onGameServerUpdate(GameListPayload glp);

    /**
     * Called when the server sends an error.
     *
     * @param errorDescription an error message
     */
    void onError(String errorDescription);
}
