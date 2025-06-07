package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.GameRoomParameters;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.service.GameRoomService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.GameRoomManager;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages creation, editing, deletion, and starting of game rooms.
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoomModel extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(GameRoomModel.class);

    private final GameRoomService gameRoomService;
    private GameRoomParameters parameters;
    private String pendingGameId;

    /**
     * Constructs a GameRoomModel with the given services.
     *
     * @param gameRoomService service to send game-room-related requests
     * @param sessionService  session manager
     * @param redirectService redirect handler
     */
    public GameRoomModel(GameRoomService gameRoomService,
                         SessionService sessionService,
                         RedirectService redirectService) {
        super(sessionService, redirectService);
        this.gameRoomService = gameRoomService;
    }

    /**
     * Generates a random ID for a new game room. Stores it in pendingGameId.
     *
     * @return the newly generated game ID
     */
    private String createNewGameId() {
        pendingGameId = String.valueOf(Math.round(Math.random() * 1_000_000));
        log.debug("Generated pending gameId={}", pendingGameId);
        return pendingGameId;
    }

    /**
     * Sets the parameters for creating or editing a game room.
     *
     * @param parameters GameRoomParameters
     */
    public void setParameters(GameRoomParameters parameters) {
        this.parameters = parameters;
        log.debug("GameRoom parameters set: {}", parameters);
    }

    public void clearParameters() {
        this.parameters = null;
    }

    /**
     * Attempts to create a new game room with the previously set parameters.
     */
    public void attemptCreateGame() {
        String newGameId = createNewGameId();
        log.info("Sending create game request for gameId={}", newGameId);
        gameRoomService.sendCreateGameRequest(newGameId, parameters, getPlayerId());
    }

    /**
     * Attempts to edit the current game room using the previously set parameters.
     */
    public void attemptEditGame() {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot edit game: missing currentGameId");
            return;
        }
        log.info("Sending edit game request for gameId={}", getCurrentGameRoomId());
        gameRoomService.sendEditGameRequest(getCurrentGameRoomId(), parameters, getPlayerId());
    }

    /**
     * Attempts to delete the current game room.
     */
    public void attemptDeleteGame() {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot delete game: missing currentGameId");
            return;
        }
        log.info("Sending delete game request for gameId={}", getCurrentGameRoomId());
        gameRoomService.sendDeleteGameRequest(getCurrentGameRoomId(), getPlayerId());
    }

    /**
     * Attempts to start the current game room.
     */
    public void attemptStartGame() {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot start game: missing currentGameId");
            return;
        }
        log.info("Sending start game request for gameId={}", getCurrentGameRoomId());
        gameRoomService.sendStartGameRequest(getCurrentGameRoomId(), getPlayerId());
    }

    /**
     * Attempts to kick a player from the current game room.
     *
     * @param kickPlayerId LobbyPlayer to kick
     */
    public void attemptKickPlayer(long kickPlayerId) {
        if (isCurrentRoomIdNull()) {
            log.error("Cannot kick player: missing currentGameId");
            return;
        }
        log.info("Sending kick player request for playerId={} in gameId={}", kickPlayerId, getCurrentGameRoomId());
        gameRoomService.sendKickPlayerRequest(getCurrentGameRoomId(), getPlayerId(), kickPlayerId);
    }

    /**
     * Finalizes game creation by adding a new GameRoom to the manager, updating session state,
     * and redirecting.
     *
     * @param redirectData redirect payload from server
     */
    public void finalizeGameCreation(RedirectParameters redirectData) {
        if (pendingGameId == null || parameters == null) {
            log.error("Cannot finalize game creation: pendingGameId or parameters missing");
            return;
        }
        String name = parameters.name();
        int capacity = parameters.capacity();
        int cardCount = parameters.cardCount();
        Long playerId = getPlayerId();
        String playerName = getPlayerName();

        GameRoom gameRoom = new GameRoom(
                GameRoom.GameStatus.WAITING_FOR_PLAYERS,
                pendingGameId,
                name,
                playerId,
                playerName,
                capacity,
                cardCount
        );
        GameRoomManager.gameRooms.add(gameRoom);
        getSession().setCurrentGameRoom(gameRoom);
        getSession().setReady(true);
        log.info("Created and entered new GameRoom[id={}, name={}]", pendingGameId, name);
        pendingGameId = null;

        redirectService.redirect(redirectData);
    }

    /**
     * Finalizes game deletion by removing the GameRoom from the manager, updating session state,
     * and redirecting if needed.
     *
     * @param parameters redirect payload from server
     */
    public void finalizeGameDeletion(RedirectParameters parameters) {
        GameRoom currentRoom = getCurrentGameRoom();
        if (currentRoom == null) {
            log.error("Cannot finalize game deletion: no current GameRoom");
            return;
        }
        GameRoomManager.gameRooms.remove(currentRoom);
        getSession().setCurrentGameRoom(null);
        log.info("Deleted GameRoom[id={}]", currentRoom.getGameId());
        redirectService.redirect(parameters);
    }

    /**
     * Updates the current GameRoom’s state from a JSON payload.
     *
     * @param lup update payload
     */
    public void updateRoom(LobbyUpdatePayload lup) {
        if (!isInRoom()) {
            log.error("Cannot update room: no current GameRoom");
            return;
        }
        getCurrentGameRoom().update(lup);
        log.info("GameRoom state updated via GameRoomModel");
    }

    /**
     * @return the host ID of the current GameRoom, or null if no room is active
     */
    public Long getCurrentGameHostId() {
        if (isInRoom()) {
            return getCurrentGameRoom().getHostId();
        }
        return null;
    }

    /**
     * @return the card count of the current GameRoom, or null if no room is active
     */
    public Integer getCurrentRoomCardCount() {
        if (isInRoom()) {
            return getCurrentGameRoom().getCardCount();
        }
        return null;
    }

    /**
     * @return the capacity of the current GameRoom, or null if no room is active
     */
    public Integer getCurrentRoomCapacity() {
        if (isInRoom()) {
            return getCurrentGameRoom().getCapacity();
        }
        return null;
    }

    /**
     * @return the name of the current GameRoom, or null if no room is active
     */
    public String getCurrentGameName() {
        if (isInRoom()) {
            return getCurrentGameRoom().getName();
        }
        return null;
    }

    /**
     * @return a list of non-host players in the current GameRoom, or null if no room is active
     */
    public ObservableList<LobbyPlayer> getFilteredPlayers() {
        if (isInRoom()) {
            return getCurrentGameRoom().getNonHostPlayers();
        }
        return null;
    }

    /**
     * @return true if all non-host players in the current GameRoom are ready
     */
    public boolean areAllPlayersReady() {
        if (isInRoom()) {
            return getCurrentGameRoom().areAllPlayersReady();
        }
        return false;
    }
}