package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.utils.MessageComponent;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilder {

    public static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);

    private MessageBuilder() {
    }

    public static String buildLoginMessage(UserCredentials userCredentials) {
        log.debug("Building login message for userCredentials: {}", userCredentials.username());
        Message message = new Message();
        message.setType(MessageType.LOGIN);
        message.setData(userCredentials.username() + MessageComponent.DATA_SEPARATOR.getValue() + userCredentials.password());

        return message.toSendable();
    }

    public static String buildRegisterMessage(UserCredentials userCredentials) {
        log.debug("Building register message for userCredentials: {}", userCredentials.username());
        Message message = new Message();
        message.setType(MessageType.REGISTER);
        message.setData(userCredentials.username() + MessageComponent.DATA_SEPARATOR.getValue() + userCredentials.password());

        return message.toSendable();
    }

    public static String buildCreateGameMessage(GameRoom gameRoom) {
        log.debug("Building create game message for gameRoom: {}", gameRoom);
        Message message = new Message();
        message.setType(MessageType.CREATE_GAME);
        message.setData(gameRoom.getCapacity() + MessageComponent.DATA_SEPARATOR.getValue() + gameRoom.getCardCount());

        return message.toSendable();
    }

}
