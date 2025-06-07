package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.GameRoomParameters;
import cz.vse.pexeso.network.ConnectionService;
import cz.vse.pexeso.util.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for sending game room management requests (create, edit, delete, start, kick) to the server.
 *
 * @author kott10
 * @version June 2025
 */
public class GameRoomService {
    private static final Logger log = LoggerFactory.getLogger(GameRoomService.class);
    private final ConnectionService connectionService;

    /**
     * Constructs a GameRoomService with the provided ConnectionService.
     *
     * @param connectionService ConnectionService to send messages
     */
    public GameRoomService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Sends a create-game request with specified parameters.
     *
     * @param gameId     ID to assign to the new game
     * @param parameters GameRoomParameters containing room name, capacity, card count
     * @param playerId   ID of the creating player
     */
    public void sendCreateGameRequest(String gameId, GameRoomParameters parameters, long playerId) {
        connectionService.send(MessageBuilder.buildCreateGameMessage(gameId, parameters, playerId));
        log.debug("Sent CREATE_GAME request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends an edit-game request to update an existing room’s settings.
     *
     * @param gameId     ID of the existing game room
     * @param parameters GameRoomParameters containing room name, capacity, card count
     * @param playerId   ID of the requesting player
     */
    public void sendEditGameRequest(String gameId, GameRoomParameters parameters, long playerId) {
        connectionService.send(MessageBuilder.buildEditGameMessage(gameId, parameters, playerId));
        log.debug("Sent EDIT_GAME request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends a delete-game request to remove a room.
     *
     * @param gameId   ID of the game room to delete
     * @param playerId ID of the requesting player
     */
    public void sendDeleteGameRequest(String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildDeleteGameMessage(gameId, playerId));
        log.debug("Sent DELETE_GAME request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends a start-game request to begin the game in the specified room.
     *
     * @param gameId   ID of the game room to start
     * @param playerId ID of the requesting player
     */
    public void sendStartGameRequest(String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildStartGameMessage(gameId, playerId));
        log.debug("Sent START_GAME request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends a kick-player request to remove a specific player from the room.
     *
     * @param gameId       ID of the game room
     * @param playerId     ID of the requesting player
     * @param kickPlayerId ID representing the player to kick
     */
    public void sendKickPlayerRequest(String gameId, long playerId, long kickPlayerId) {
        connectionService.send(MessageBuilder.buildKickPlayerMessage(gameId, playerId, kickPlayerId));
        log.debug("Sent KICK_PLAYER request for targetId={}, gameId='{}'", kickPlayerId, gameId);
    }
}