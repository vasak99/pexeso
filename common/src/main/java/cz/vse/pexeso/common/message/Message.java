package cz.vse.pexeso.common.message;

import cz.vse.pexeso.common.message.payload.MessagePayload;

import java.time.Instant;

public class Message {
    private MessageType type;
    private String gameId;
    private String playerId;
    private MessagePayload data;
    private Instant timestamp;

    public Message() {
    }

    public Message(MessageType type, String gameId, String playerId, MessagePayload data, Instant timestamp) {
        this.type = type;
        this.gameId = gameId;
        this.playerId = playerId;
        this.data = data;
        this.timestamp = timestamp;
    }
}
