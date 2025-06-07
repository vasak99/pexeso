package cz.vse.pexeso.model.service;

import cz.vse.pexeso.network.ConnectionService;
import cz.vse.pexeso.util.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for sending lobby-related requests (join, leave, ready, identity) to the server.
 *
 * @author kott10
 * @version June 2025
 */
public class LobbyService {
    private static final Logger log = LoggerFactory.getLogger(LobbyService.class);
    private final ConnectionService connectionService;

    /**
     * Constructs a LobbyService with the provided ConnectionService.
     *
     * @param connectionService ConnectionService to send messages
     */
    public LobbyService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Sends a join-game request for the specified gameId and playerId.
     *
     * @param gameId   ID of the game room to join
     * @param playerId ID of the joining player
     */
    public void sendJoinGameRequest(String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildJoinGameMessage(gameId, playerId));
        log.debug("Sent JOIN_GAME request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends a leave-game request for the specified gameId and playerId.
     *
     * @param gameId   ID of the game room to leave
     * @param playerId ID of the leaving player
     */
    public void sendLeaveGameRequest(String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildLeaveGameMessage(gameId, playerId));
        log.debug("Sent LEAVE_GAME request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends a player-ready notification to the server for the given gameId and playerId.
     *
     * @param gameId   ID of the game room
     * @param playerId ID of the ready player
     */
    public void sendReadyRequest(String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildPlayerReadyMessage(gameId, playerId));
        log.debug("Sent PLAYER_READY request for gameId='{}', playerId={}", gameId, playerId);
    }

    /**
     * Sends an identity response (playerId) to the server.
     *
     * @param playerId ID of this client
     */
    public void sendIdentity(long playerId) {
        connectionService.send(MessageBuilder.buildIdentityMessage(playerId));
        log.debug("Sent IDENTITY message for playerId={}", playerId);
    }
}