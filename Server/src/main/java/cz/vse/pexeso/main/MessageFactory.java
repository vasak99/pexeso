package cz.vse.pexeso.main;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;

public class MessageFactory {

    public static Message getError(String message) {
        Message ret = createMessage(MessageType.ERROR);
        ret.setData(message);
        return ret;
    }

    public static Message getLoginMessage(String data) {
        Message ret = createMessage(MessageType.LOGIN);
        ret.setData(data);
        return ret;
    }

    public static Message getRegisterMessage(String data) {
        Message ret = createMessage(MessageType.REGISTER);
        ret.setData(data);
        return ret;
    }

    public static Message getIdentityMessage(String data) {
        Message ret = createMessage(MessageType.IDENTITY);
        ret.setData(data);
        return ret;
    }

    public static Message getIdentityRequest() {
        Message ret = createMessage(MessageType.REQUEST_IDENTITY);
        ret.setData("");
        return ret;
    }

    public static Message getGameStartMessage() {
        Message ret = createMessage(MessageType.GAME_START);
        ret.setData("");
        return ret;
    }

    private static Message createMessage(MessageType type) {
        Message msg = new Message();
        msg.setType(type);
        return msg;
    }

}
