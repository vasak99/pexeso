package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.utils.MessageComponent;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.service.AppServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilder {

    private static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);
    private static final String DATA_SEPARATOR = MessageComponent.DATA_SEPARATOR.getValue();
    private static final AppServices appServices = AppServices.getInstance();

    private MessageBuilder() {
    }

    private static String build(MessageType type, boolean gameId, boolean playerId, String data) {
        Message message = new Message();
        message.setType(type);

        if (gameId) {
            message.setGameId(appServices.getClientSession().getCurrentGameRoom().getGameId());
        }
        if (playerId) {
            message.setPlayerId(String.valueOf(appServices.getClientSession().getPlayerId()));
        }
        if (data != null) {
            message.setData(data);
        }

        return message.toSendable();
    }

    public static String buildLoginMessage(UserCredentials userCredentials) {
        log.debug("Building login message for userCredentials: {}", userCredentials.username());

        String data = userCredentials.username() + DATA_SEPARATOR + userCredentials.password();

        return build(MessageType.LOGIN, false, false, data);
    }

    public static String buildRegisterMessage(UserCredentials userCredentials) {
        log.debug("Building register message for userCredentials: {}", userCredentials.username());

        String data = userCredentials.username() + DATA_SEPARATOR + userCredentials.password();

        return build(MessageType.REGISTER, false, false, data);
    }

    public static String buildCreateGameMessage(GameRoom gameRoom) {
        log.debug("Building create game message for gameRoom: {}", gameRoom);

        String data = gameRoom.getCapacity() + DATA_SEPARATOR + gameRoom.getCardCount();

        return build(MessageType.CREATE_GAME, false, true, data);
    }

    public static String buildEditGameMessage(GameRoom gameRoom) {
        log.info("Building edit game message for gameRoom: {}", gameRoom);

        String data = gameRoom.getCapacity() + DATA_SEPARATOR + gameRoom.getCardCount();

        return build(MessageType.EDIT_GAME, true, true, data);
    }

    public static String buildJoinGameMessage(GameRoom gameRoom) {
        log.info("Building join game message for gameRoom: {}", gameRoom);

        String data = gameRoom.getGameId();

        return build(MessageType.JOIN_GAME, false, true, data);
    }

    public static String buildLeaveGameMessage() {
        log.info("Building leave game message");

        return build(MessageType.LEAVE_GAME, true, true, null);
    }

    public static String buildDeleteGameMessage() {
        log.info("Building delete game message");

        return build(MessageType.DELETE_GAME, true, true, null);
    }

    public static String buildPlayerReadyMessage() {
        log.info("Building ready message");

        return build(MessageType.PLAYER_READY, true, true, null);
    }

    public static String buildKickPlayerMessage(LobbyPlayer lobbyPlayer) {
        log.info("Building kick lobbyPlayer message for lobbyPlayer: {}", lobbyPlayer);

        return build(MessageType.KICK_PLAYER, true, true, String.valueOf(lobbyPlayer.getPlayerId()));
    }
}
