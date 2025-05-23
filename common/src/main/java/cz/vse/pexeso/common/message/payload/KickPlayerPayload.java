package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.message.Message;

public class KickPlayerPayload implements MessagePayload {

    public long playerId;

    public KickPlayerPayload(long playerId) {
        this.playerId = playerId;
    }

    public String toSendable() {
        return "" + this.playerId;
    }

    public KickPlayerPayload(Message msg) {
        this.playerId = Long.parseLong(msg.getData());
    }
}
