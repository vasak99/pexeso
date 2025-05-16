package cz.vse.pexeso.main;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;

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

    public static Message getGameStartMessage(String data) {
        Message ret = createMessage(MessageType.START_GAME);
        ret.setData(data);
        return ret;
    }

    public static Message getRedirectMessage(String host, String port) {
        Message ret = createMessage(MessageType.REDIRECT);
        ret.setData(host + ":" + port);
        return ret;
    }

    public static Message getRedirectMessage(String host, int port) {
        Message ret = createMessage(MessageType.REDIRECT);
        ret.setData(host + ":" + port);
        return ret;
    }

    public static Message getLobbyMessage(LobbyUpdatePayload data) {
        Message ret = createMessage(MessageType.LOBBY_UPDATE);
        ret.setData(data.toSendable());
        return ret;

    }

    private static Message createMessage(MessageType type) {
        Message msg = new Message();
        msg.setType(type);
        return msg;
    }

}
