package cz.vse.pexeso.util;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.*;
import cz.vse.pexeso.model.CardCoordinates;
import cz.vse.pexeso.model.GameRoomParameters;
import cz.vse.pexeso.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds protocol messages to send to the server.
 *
 * @author kott10
 * @version June 2025
 */
public final class MessageBuilder {
    private static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);

    private MessageBuilder() {
    }

    /**
     * Constructs the sendable string for a message, given its type and optional fields.
     *
     * @param type     MessageType enum
     * @param gameId   ID of the game room
     * @param playerId ID of the player
     * @param data     Payload data as sendable string
     * @return full sendable message string
     */
    private static String build(MessageType type, String gameId, Long playerId, String data) {
        Message message = new Message();
        message.setType(type);

        if (gameId != null && !gameId.isBlank()) {
            message.setGameId(gameId);
        }
        if (playerId != null) {
            message.setPlayerId(String.valueOf(playerId));
        }
        if (data != null && !data.isBlank()) {
            message.setData(data);
        }

        String sendable = message.toSendable();
        log.debug("Built message of type {}: {}", type, sendable);
        return sendable;
    }

    /**
     * Builds a login request message.
     *
     * @param userCredentials UserCredentials containing username and password
     * @return sendable login message string
     */
    public static String buildLoginMessage(UserCredentials userCredentials) {
        log.debug("Building LOGIN message for username='{}'", userCredentials.username());

        String data = new LoginPayload(userCredentials.username(), userCredentials.password()).toSendable();
        return build(MessageType.LOGIN, null, null, data);
    }

    /**
     * Builds a register request message.
     *
     * @param userCredentials UserCredentials containing username and password
     * @return sendable register message string
     */
    public static String buildRegisterMessage(UserCredentials userCredentials) {
        log.debug("Building REGISTER message for username='{}'", userCredentials.username());

        String data = new RegisterPayload(userCredentials.username(), userCredentials.password()).toSendable();
        return build(MessageType.REGISTER, null, null, data);
    }

    /**
     * Builds an identity confirmation message to notify the server of this client’s playerId.
     *
     * @param playerId ID assigned by server
     * @return sendable identity message string
     */
    public static String buildIdentityMessage(long playerId) {
        log.debug("Building IDENTITY message for playerId={}", playerId);

        String data = new IdentityPayload(String.valueOf(playerId)).toSendable();
        return build(MessageType.IDENTITY, null, null, data);
    }

    /**
     * Builds a create-game request message with specified parameters.
     *
     * @param gameId     ID to assign to the new game
     * @param parameters GameRoomParameters containing room name, capacity, and card count
     * @param playerId   ID of the creating player
     * @return sendable create game message string
     */
    public static String buildCreateGameMessage(String gameId, GameRoomParameters parameters, long playerId) {
        log.debug("Building CREATE_GAME message for gameId='{}', roomName='{}'", gameId, parameters.name());

        String data = new CreateGamePayload(gameId, parameters.capacity(), parameters.cardCount(), parameters.name()).toSendable();
        return build(MessageType.CREATE_GAME, null, playerId, data);
    }

    /**
     * Builds an edit-game request message to update an existing room’s settings.
     *
     * @param gameId     ID of the existing game room
     * @param parameters Updated GameRoomParameters
     * @param playerId   ID of the editing player
     * @return sendable edit game message string
     */
    public static String buildEditGameMessage(String gameId, GameRoomParameters parameters, long playerId) {
        log.debug("Building EDIT_GAME message for gameId='{}', roomName='{}'", gameId, parameters.name());

        String data = new EditGamePayload(parameters.name(), parameters.capacity(), parameters.cardCount()).toSendable();
        return build(MessageType.EDIT_GAME, gameId, playerId, data);
    }

    /**
     * Builds a delete-game request message.
     *
     * @param gameId   ID of the game room to delete
     * @param playerId ID of the deleting player
     * @return sendable delete game message string
     */
    public static String buildDeleteGameMessage(String gameId, long playerId) {
        log.debug("Building DELETE_GAME message for gameId='{}'", gameId);

        return build(MessageType.DELETE_GAME, gameId, playerId, null);
    }

    /**
     * Builds a join-game request message for the given gameId.
     *
     * @param gameId   ID of the game room to join
     * @param playerId ID of the joining player
     * @return sendable join game message string
     */
    public static String buildJoinGameMessage(String gameId, long playerId) {
        log.debug("Building JOIN_GAME message for gameId='{}'", gameId);

        String data = new JoinGamePayload(gameId).toSendable();
        return build(MessageType.JOIN_GAME, null, playerId, data);
    }

    /**
     * Builds a leave-game request message.
     *
     * @param gameId   ID of the game room to leave
     * @param playerId ID of the leaving player
     * @return sendable leave game message string
     */
    public static String buildLeaveGameMessage(String gameId, Long playerId) {
        log.debug("Building LEAVE_GAME message for gameId='{}'", gameId);

        return build(MessageType.LEAVE_GAME, gameId, playerId, null);
    }

    /**
     * Builds a player-ready notification message.
     *
     * @param gameId   ID of the game room
     * @param playerId ID of the ready player
     * @return sendable player ready message string
     */
    public static String buildPlayerReadyMessage(String gameId, long playerId) {
        log.debug("Building PLAYER_READY message for gameId='{}', playerId={}", gameId, playerId);

        return build(MessageType.PLAYER_READY, gameId, playerId, null);
    }

    /**
     * Builds a kick-player request message to remove a player from a room.
     *
     * @param gameId       ID of the game room
     * @param playerId     ID of the requesting
     * @param kickPlayerId LobbyPlayer to kick
     * @return sendable kick player message string
     */
    public static String buildKickPlayerMessage(String gameId, long playerId, long kickPlayerId) {
        log.debug("Building KICK_PLAYER message for targetId={}, gameId='{}'", kickPlayerId, gameId);

        String data = new KickPlayerPayload(kickPlayerId).toSendable();
        return build(MessageType.KICK_PLAYER, gameId, playerId, data);
    }

    /**
     * Builds a start-game request message.
     *
     * @param gameId   ID of the game room
     * @param playerId ID of the requesting player
     * @return sendable start game message string
     */
    public static String buildStartGameMessage(String gameId, long playerId) {
        log.debug("Building START_GAME message for gameId='{}'", gameId);

        return build(MessageType.START_GAME, gameId, playerId, null);
    }

    /**
     * Builds a reveal-card request message indicating which card coordinates to reveal.
     *
     * @param coordinates CardCoordinates specifying row and column
     * @param gameId      ID of the game room
     * @param playerId    ID of the revealing player
     * @return sendable reveal card message string
     */
    public static String buildRevealCardMessage(CardCoordinates coordinates, String gameId, long playerId) {
        log.debug("Building REVEAL message for coordinates=({},{}) in gameId='{}'",
                coordinates.row(), coordinates.column(), gameId);

        String data = new RevealCardPayload(coordinates.row(), coordinates.column()).toSendable();
        return build(MessageType.REVEAL, gameId, playerId, data);
    }

    /**
     * Builds a give-up request message indicating the player surrenders the game.
     *
     * @param gameId   ID of the game room
     * @param playerId ID of the surrendering player
     * @return sendable give-up message string
     */
    public static String buildGiveUpMessage(String gameId, Long playerId) {
        log.debug("Building GIVE_UP message for gameId='{}', playerId={}", gameId, playerId);

        return build(MessageType.GIVE_UP, gameId, playerId, null);
    }

    /**
     * Builds a logout request message to notify the server that the player is logging out.
     *
     * @param playerId ID of the logging out player
     * @param gameId   ID of the game room (if exists)
     * @return sendable logout message string
     */
    public static String buildLogoutMessage(Long playerId, String gameId) {
        log.debug("Building LOGOUT message");

        return build(MessageType.LOGOUT, gameId, playerId, null);
    }
}