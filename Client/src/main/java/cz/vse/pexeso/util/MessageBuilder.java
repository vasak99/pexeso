package cz.vse.pexeso.util;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.*;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageBuilder {
    private static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);

    private MessageBuilder() {
    }

    private static String build(MessageType type, String gameId, Long playerId, String data) {
        Message message = new Message();
        message.setType(type);

        if (gameId != null) {
            message.setGameId(gameId);
        }
        if (playerId != null) {
            message.setPlayerId(String.valueOf(playerId));
        }
        if (data != null) {
            message.setData(data);
        }

        return message.toSendable();
    }

    public static String buildLoginMessage(UserCredentials userCredentials) {
        log.debug("Building login message for userCredentials: {}", userCredentials.username());

        String data = new LoginPayload(userCredentials.username(), userCredentials.password()).toSendable();

        return build(MessageType.LOGIN, null, null, data);
    }

    public static String buildRegisterMessage(UserCredentials userCredentials) {
        log.debug("Building register message for userCredentials: {}", userCredentials.username());

        String data = new RegisterPayload(userCredentials.username(), userCredentials.password()).toSendable();

        return build(MessageType.REGISTER, null, null, data);
    }

    public static String buildIdentityMessage(long playerId) {
        log.debug("Building identity message");

        String data = new IdentityPayload(String.valueOf(playerId)).toSendable();

        return build(MessageType.IDENTITY, null, null, data);
    }

    public static String buildCreateGameMessage(GameRoom gameRoom, long playerId) {
        log.debug("Building create game message for gameRoom: {}", gameRoom);

        String data = new CreateGamePayload(gameRoom.getGameId(), gameRoom.getCapacity(), gameRoom.getCardCount(), gameRoom.getName()).toSendable();

        return build(MessageType.CREATE_GAME, null, playerId, data);
    }

    public static String buildEditGameMessage(GameRoom gameRoom, long playerId) {
        log.info("Building edit game message for gameRoom: {}", gameRoom);

        String data = new EditGamePayload(gameRoom.getName(), gameRoom.getCapacity(), gameRoom.getCardCount()).toSendable();

        return build(MessageType.EDIT_GAME, gameRoom.getGameId(), playerId, data);
    }

    public static String buildDeleteGameMessage(GameRoom gameRoom, long playerId) {
        log.info("Building delete game message");

        return build(MessageType.DELETE_GAME, gameRoom.getGameId(), playerId, null);
    }

    public static String buildJoinGameMessage(GameRoom gameRoom, long playerId) {
        log.info("Building join game message for gameRoom: {}", gameRoom);

        String data = new JoinGamePayload(gameRoom.getGameId()).toSendable();

        return build(MessageType.JOIN_GAME, null, playerId, data);
    }

    public static String buildLeaveGameMessage(GameRoom gameRoom, long playerId) {
        log.info("Building leave game message");

        return build(MessageType.LEAVE_GAME, gameRoom.getGameId(), playerId, null);
    }

    public static String buildPlayerReadyMessage(GameRoom gameRoom, long playerId) {
        log.info("Building ready message");

        return build(MessageType.PLAYER_READY, gameRoom.getGameId(), playerId, null);
    }

    public static String buildKickPlayerMessage(GameRoom gameRoom, long playerId, LobbyPlayer lobbyPlayer) {
        log.info("Building kick lobbyPlayer message for lobbyPlayer: {}", lobbyPlayer);

        String data = new KickPlayerPayload(lobbyPlayer.getPlayerId()).toSendable();

        return build(MessageType.KICK_PLAYER, gameRoom.getGameId(), playerId, data);
    }

    public static String buildStartGameMessage(GameRoom gameRoom, long playerId) {
        log.info("Building start game message");

        return build(MessageType.START_GAME, gameRoom.getGameId(), playerId, null);
    }
}
