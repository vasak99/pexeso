package cz.vse.pexeso.common.message.payload;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LobbyUpdatePayload implements MessagePayload {

    public String gameBoard;
    public List<SendablePlayer> players;

    public int cardCount;
    public int playersCapacity;

    public LobbyUpdatePayload() {
    }

    public String toSendable() {
        String ret = "";

        var mapper = new ObjectMapper();
        try {
            ret = mapper.writeValueAsString(this);
        } catch (Exception e) {}

        return ret;
    }

    public LobbyUpdatePayload(String data) {
        var mapper = new ObjectMapper();
        LobbyUpdatePayload deserialized = null;
        try {
            deserialized = mapper.readValue(data, LobbyUpdatePayload.class);
        } catch (JsonMappingException e) {}
        catch (JsonParseException e) {}
        catch (IOException e) {}

        if(deserialized != null) {
            this.gameBoard = deserialized.gameBoard;
            this.players = deserialized.players;
        }
    }
}
