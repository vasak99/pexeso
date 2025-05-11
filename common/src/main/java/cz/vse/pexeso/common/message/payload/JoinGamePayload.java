package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.message.Message;

public class JoinGamePayload implements MessagePayload {

    public String gameId;

    public JoinGamePayload(String gameId) {
        this.gameId = gameId;
    }

    public String toSendable() {
        return this.gameId;
    }

    public JoinGamePayload(Message msg) {
        this.gameId = msg.getData();
    }
}
