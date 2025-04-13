package cz.vse.pexeso.common.message;

import cz.vse.pexeso.common.message.payload.MessagePayload;
import cz.vse.pexeso.common.utils.MessageComponent;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private Map<MessageComponent, String> entries = new HashMap<MessageComponent, String>();

    public Message() {
    }

    public Message(String type, String gameId, String playerId, MessagePayload data) {
        this.entries.put(MessageComponent.TYPE, type);
        this.entries.put(MessageComponent.GAME_ID, gameId);
        this.entries.put(MessageComponent.PLAYER_ID, playerId);
        this.entries.put(MessageComponent.DATA, data.toSendable());
    }

    public Message(Map<MessageComponent, String> entries) {
        this.entries = entries;
    }

    public void setType(String type) {
        this.setEntry(MessageComponent.TYPE, type);
    }

    public void setGameId(String id) {
        this.setEntry(MessageComponent.GAME_ID, id);
    }

    public void setPlayerId(String id) {
        this.setEntry(MessageComponent.PLAYER_ID, id);
    }

    public void setData(String data) {
        this.setEntry(MessageComponent.DATA, data);
    }

    public void setEntry(MessageComponent key, String value) {
        if(this.entries.containsKey(key)) {
            this.entries.replace(key, value);
        } else {
            this.entries.put(key, value);
        }
    }

    public MessageType getType() {
        return MessageType.valueOf(this.entries.get(MessageComponent.TYPE));
    }

    public String getGameId() {
        return this.entries.get(MessageComponent.GAME_ID);
    }

    public String getPlayerId() {
        return this.entries.get(MessageComponent.PLAYER_ID);
    }

    public String getData() {
        return this.entries.get(MessageComponent.DATA);
    }

    public String getTimeStamp() {
        return this.entries.get(MessageComponent.TIMESTAMP);
    }

    public String getEntry(MessageComponent mc) {
        return this.entries.get(mc);
    }

    public Map<MessageComponent, String> getEntries() {
        return new HashMap<MessageComponent, String>(this.entries);
    }

}
