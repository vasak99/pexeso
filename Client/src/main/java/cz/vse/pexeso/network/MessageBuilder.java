package cz.vse.pexeso.network;

import cz.vse.pexeso.model.User;

public class MessageBuilder {

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
        return "LOGIN" + "|" + user.getUsername() + "|" + user.getPassword();
    }

    public String buildSubmitCardPairMessage(String firstCardID, String secondCardID) {
        return "CARD_PAIR" + "|" + firstCardID + "|" + secondCardID;
    }
}
