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

    public Message(String msg) {
        String[] strEntries = msg.split(MessageComponent.SEPARATOR.getValue());

        boolean isInMsg = false;

        for(var ent : strEntries) {
            if(ent.equals(MessageComponent.START.getValue())) {
                isInMsg = true;
                continue;
            }
            if(ent.equals(MessageComponent.END.getValue())) {
                break;
            }

            if(!isInMsg) { continue; }

            String[] spl = ent.split(MessageComponent.KEY_VALUE_SEPARATOR.getValue());

            if(spl.length != 2) {
                continue;
            }

            MessageComponent mc = MessageComponent.fromString(spl[0]);
            if(mc == null) { continue; }

            this.setEntry(mc, spl[1]);
        }
    }

    public void setType(String type) {
        this.setEntry(MessageComponent.TYPE, type);
    }

    public void setType(MessageType mt) {
        this.setEntry(MessageComponent.TYPE, mt.getValue());
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
        String ret = this.entries.get(MessageComponent.TYPE);
        if(ret == null) { return null; }
        return MessageType.valueOf(ret);
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

    public String toSendable() {
        String ret = "";

        ret += MessageComponent.START.getValue();
        ret += MessageComponent.SEPARATOR.getValue();

        for(var mc : MessageComponent.getOrderedKeys()) {
            String val = this.getEntry(mc);

            if(val == null) { continue; }

            ret += mc.getValue();
            ret += MessageComponent.KEY_VALUE_SEPARATOR.getValue();
            ret += val;
            ret += MessageComponent.SEPARATOR.getValue();
        }

        ret += MessageComponent.END.getValue();

        return ret;
    }

}
