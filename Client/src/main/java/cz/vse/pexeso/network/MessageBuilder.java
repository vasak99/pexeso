package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.utils.MessageComponent;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.service.AppServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilder {

    public static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);
    public static MessageType latest = null;

    private MessageBuilder() {
    }

    public static String buildLoginMessage(UserCredentials userCredentials) {
        log.debug("Building login message for userCredentials: {}", userCredentials.username());
        Message message = new Message();
        message.setType(MessageType.LOGIN);
        message.setData(userCredentials.username() + MessageComponent.DATA_SEPARATOR.getValue() + userCredentials.password());

        latest = MessageType.LOGIN;
        return message.toSendable();
    }

    public static String buildRegisterMessage(UserCredentials userCredentials) {
        log.debug("Building register message for userCredentials: {}", userCredentials.username());
        Message message = new Message();
        message.setType(MessageType.REGISTER);
        message.setData(userCredentials.username() + MessageComponent.DATA_SEPARATOR.getValue() + userCredentials.password());

        latest = MessageType.REGISTER;
        return message.toSendable();
    }

    public static String buildCreateGameMessage(GameRoom gameRoom) {
        log.debug("Building create game message for gameRoom: {}", gameRoom);
        Message message = new Message();
        message.setType(MessageType.CREATE_GAME);
        message.setData(gameRoom.getCapacity() + MessageComponent.DATA_SEPARATOR.getValue() + gameRoom.getCardCount());

        latest = MessageType.CREATE_GAME;
        return message.toSendable();
    }

    public static String buildDeleteGameMessage(GameRoom gameRoom) {
        log.info("Building delete game message for gameRoom: {}", gameRoom);
        Message message = new Message();
        message.setType(MessageType.DELETE_GAME);
        message.setGameId(gameRoom.getGameId());
        message.setPlayerId(AppServices.getClientSession().getPlayerId());

        latest = MessageType.DELETE_GAME;
        return message.toSendable();
    }

    public static String buildEditGameMessage(GameRoom gameRoom) {
        log.info("Building edit game message for gameRoom: {}", gameRoom);
        Message message = new Message();
        message.setType(MessageType.EDIT_GAME);
        message.setData(gameRoom.getCapacity() + MessageComponent.DATA_SEPARATOR.getValue() + gameRoom.getCardCount());
        message.setGameId(gameRoom.getGameId());
        message.setPlayerId(AppServices.getClientSession().getPlayerId());

        latest = MessageType.EDIT_GAME;
        return message.toSendable();
    }

    public static String buildJoinGameMessage(GameRoom gameRoom) {
        log.info("Building join game message for gameRoom: {}", gameRoom);
        Message message = new Message();
        message.setType(MessageType.JOIN_GAME);
        message.setPlayerId(AppServices.getClientSession().getPlayerId());

        // gameId in data or in gameId?
        message.setGameId(gameRoom.getGameId());
        message.setData(gameRoom.getGameId());

        latest = MessageType.JOIN_GAME;
        return message.toSendable();
    }
}
