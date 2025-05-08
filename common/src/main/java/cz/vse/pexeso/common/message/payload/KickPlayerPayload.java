package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.message.Message;

public class KickPlayerPayload implements MessagePayload {

    public String playerId;

    public KickPlayerPayload(String playerId) {
        this.playerId = playerId;
    }

    public String toSendable() {
        return this.playerId;
    }

    public KickPlayerPayload(Message msg) {
        this.playerId = msg.getData();
    }
}
