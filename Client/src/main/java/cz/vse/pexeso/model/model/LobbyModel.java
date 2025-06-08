package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.Game;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.service.LobbyService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.GameRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages lobby-related actions: joining, leaving, sending readiness, and initializing games.
 * Relies on LobbyService to send requests to the server and uses GameRoomManager to update local state.
 *
 * @author kott10
 * @version June 2025
 */
public class LobbyModel extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(LobbyModel.class);

    private final LobbyService lobbyService;
    private String attemptedJoinGameId;

    /**
     * Constructs a LobbyModel with the given services.
     *
     * @param lobbyService    service to send lobby-related requests
     * @param sessionService  session manager
     * @param redirectService redirect handler
     */
    public LobbyModel(LobbyService lobbyService, SessionService sessionService, RedirectService redirectService) {
        super(sessionService, redirectService);
        this.lobbyService = lobbyService;
    }

    /**
     * Attempts to join a game room by sending a join request to the server.
     *
     * @param gameId the ID of the game to join
     */
    public void attemptJoin(String gameId) {
        attemptedJoinGameId = gameId;
        log.info("Sending join request for gameId={}", gameId);
        lobbyService.sendJoinGameRequest(gameId, getPlayerId());
    }

    /**
     * Sends a leave request for the current game room.
     */
    public void attemptLeave() {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot leave: missing currentGameId");
            return;
        }
        log.info("Sending leave request for gameId={}", getCurrentGameRoomId());
        lobbyService.sendLeaveGameRequest(getCurrentGameRoomId(), getPlayerId());
    }

    /**
     * Sends a ready signal for the current game room and marks the session as ready.
     */
    public void attemptReady() {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot kick player: missing currentRoomId");
            return;
        }
        log.info("Sending ready request for gameId={}", getCurrentGameRoomId());
        lobbyService.sendReadyRequest(getCurrentGameRoomId(), getPlayerId());
        getSession().setReady(true);
    }

    /**
     * Sends the player’s identity to the server.
     */
    public void sendIdentity() {
        log.info("Sending identity for playerId={}", getPlayerId());
        lobbyService.sendIdentity(getPlayerId());
    }

    /**
     * Finalizes the join operation by updating the session with the joined room and redirecting
     *
     * @param parameters redirect payload from server
     */
    public void finalizeJoin(RedirectParameters parameters) {
        if (attemptedJoinGameId == null) {
            log.error("No attemptedJoinGameId when finalizing join");
            return;
        }
        GameRoom roomToJoin = GameRoomManager.findById(attemptedJoinGameId);
        if (roomToJoin == null) {
            log.error("GameRoom not found for ID={}", attemptedJoinGameId);
            attemptedJoinGameId = null;
            return;
        }
        getSession().setCurrentGameRoom(roomToJoin);
        attemptedJoinGameId = null;
        log.info("joined GameRoom[id={}]", roomToJoin.getGameId());

        redirectService.redirect(parameters);
    }

    /**
     * Finalizes the leave operation by clearing the session’s current room, marking not ready, and redirecting.
     *
     * @param parameters redirect payload from server
     */
    public void finalizeLeave(RedirectParameters parameters) {
        getSession().setCurrentGameRoom(null);
        getSession().setReady(false);
        log.info("left the game room");
        redirectService.redirect(parameters);
    }

    /**
     * Initializes a new game by creating a Game instance, attaching it to the current room,
     * updating its state from payload data, and assigning player colors.
     *
     * @param gup payload representing the initial game state
     */
    public void initializeGame(GameUpdatePayload gup) {
        GameRoom currentRoom = getCurrentGameRoom();
        if (currentRoom == null) {
            log.error("Cannot initialize game: no current GameRoom");
            return;
        }
        Game game = new Game();
        currentRoom.setGame(game);
        game.update(gup);
        game.setupPlayerColors(currentRoom.getPlayers());
        log.info("Game initialized for GameRoom[id={}]", currentRoom.getGameId());
    }

    /**
     * Updates the lobby listing based on payload data.
     *
     * @param glp update payload
     */
    public void updateLobby(GameListPayload glp) {
        GameRoomManager.update(glp);
        log.info("Lobby updated via LobbyModel");
    }

    /**
     * Updates the current GameRoom’s state based on payload data.
     *
     * @param lup update payload
     */
    public void updateGameRoom(LobbyUpdatePayload lup) {
        GameRoom room = getCurrentGameRoom();
        if (room == null) {
            log.error("Cannot update game room: current GameRoom is null");
            return;
        }
        room.update(lup);
        log.info("GameRoom[id={}] updated via LobbyModel", room.getGameId());
    }

    /**
     * @return true if the session has marked itself ready
     */
    public boolean isReady() {
        return getSession().isReady();
    }

    /**
     * @return true if the session is hosting its current game room
     */
    public boolean isHosting() {
        return getSession().isHostingAGameRoom();
    }

    /**
     * Sets the in-progress flag on the current GameRoom.
     *
     * @param inProgress true if the game room has started
     */
    public void setCurrentRoomInProgress(boolean inProgress) {
        getCurrentGameRoom().setInProgress(inProgress);
        log.info("GameRoom inProgress set to {}", inProgress);
    }

    /**
     * @return true if the current game room has started
     */
    public boolean isRoomInProgress() {
        return getSession().isRoomInProgress();
    }

    public void askForStats() {
        lobbyService.sendStatsRequest(getPlayerId());
    }
}