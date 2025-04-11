package cz.vse.pexeso.network;

import cz.vse.pexeso.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilder {

    public static final Logger log = LoggerFactory.getLogger(MessageBuilder.class);
    private static MessageBuilder instance = null;


    private MessageBuilder() {
    }

    public static MessageBuilder getInstance() {
        if (instance == null) {
            instance = new MessageBuilder();
        }
        return instance;
    }

    public String buildLoginMessage(User user) {
        log.debug("Building login message for user: {}", user.getUsername());
        return "LOGIN" + "|" + user.getUsername() + "|" + user.getPassword();
    }

    public String buildSubmitCard(int order, String secondCardID) {
        log.debug("Building submit card message: {} - {}", order, secondCardID);
        return "CARD_PAIR" + "|" + order + "|" + secondCardID;
    }
}
