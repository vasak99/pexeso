package cz.vse.pexeso.main;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;

public class Messenger {

    protected Message getError(String message) {
        Message ret = new Message();
        ret.setType(MessageType.ERROR);
        ret.setData(message);
        return ret;
    }

    protected Message getLoginMessage(String data) {
        Message ret = new Message();
        ret.setType(MessageType.LOGIN);
        ret.setData(data);
        return ret;
    }

    protected Message getRegisterMessage(String data) {
        Message ret = new Message();
        ret.setType(MessageType.REGISTER);
        ret.setData(data);
        return ret;
    }

    protected Message getIdentityMessage(String data) {
        Message ret = new Message();
        ret.setType(MessageType.IDENTITY);
        ret.setData(data);
        return ret;
    }

}
