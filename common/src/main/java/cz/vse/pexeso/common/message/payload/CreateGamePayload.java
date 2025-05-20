package cz.vse.pexeso.common.message.payload;

import com.fasterxml.jackson.databind.ObjectMapper;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.utils.MessageComponent;

public class CreateGamePayload implements MessagePayload {

    public int capacity;
    public int cardCount;
    public String gameName;

    public CreateGamePayload(int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public CreateGamePayload(int capacity, int cardCount, String name) {
        this.capacity = capacity;
        this.cardCount = cardCount;
        this.gameName = name;
    }

    @Override
    public String toSendable() {
        var mapper = new ObjectMapper();

        String ret = "";

        try {
            ret = mapper.writeValueAsString(this);
        } catch (Exception e) {}

        return ret;
    }

    public CreateGamePayload(String data) throws DataFormatException {
        var mapper = new ObjectMapper();

        try {
            var parsed = mapper.readValue(data, CreateGamePayload.class);

            this.capacity = parsed.capacity;
            this.cardCount = parsed.cardCount;
            this.gameName = parsed.gameName;
        } catch (Exception e) {}
    }

}
