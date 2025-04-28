package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.message.Message;

public class IdentityPayload implements MessagePayload {

    public String userId;

    public IdentityPayload(String userId) {
        this.userId = userId;
    }

    public String toSendable() {
        return this.userId;
    }

    public IdentityPayload(Message msg) {
        this.userId = msg.getData();
    }

}
