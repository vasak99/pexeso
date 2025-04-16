package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageTranslatorImpl;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.LoginPayload;
import cz.vse.pexeso.helper.AppServices;
import cz.vse.pexeso.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilder {

    public static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);
    private static final MessageTranslatorImpl messageTranslator = new MessageTranslatorImpl();


    private MessageBuilder() {
    }

    public static String buildLoginMessage(User user) {
        log.debug("Building login message for user: {}", user.username());

        LoginPayload loginPayload = new LoginPayload(user.username(), user.password());
        Message message = new Message(MessageType.LOGIN.getValue(), null, null, loginPayload);

        return messageTranslator.messageToString(message);
    }

    public static String buildSubmitCard(int order, String secondCardID) {
        String playerId = AppServices.getClientSession().getPlayerId();
        return "";
    }
}
