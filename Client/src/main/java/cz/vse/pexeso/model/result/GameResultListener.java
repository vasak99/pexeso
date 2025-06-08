package cz.vse.pexeso.model.result;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;

/**
 * Listener interface for game result events. Implementations will be notified
 * when the server sends game update, error, result, redirect or game rooms update during the game.
 *
 * @author kott10
 * @version June 2025
 */
public interface GameResultListener {
    /**
     * Called when the server sends a game update (gameboard etc.).
     *
     * @param gup game update data
     */
    void onGameUpdate(GameUpdatePayload gup);

    /**
     * Called when the server indicates the last move was invalid.
     *
     * @param errorDescription error message
     */
    void onInvalidMove(String errorDescription);

    /**
     * Called when the server indicates the game has ended.
     */
    void onResult();

    /**
     * Called when the server instructs the client to redirect to a different host/port.
     *
     * @param parameters redirect data
     */
    void onRedirect(RedirectParameters parameters);

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
