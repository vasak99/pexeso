package cz.vse.pexeso.common.message.payload;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameListPayload implements MessagePayload {

    public List<SendableGame> games;

    public GameListPayload() {}

	@Override
	public String toSendable() {
        var mapper = new ObjectMapper();

        String ret = "";
        try {
            ret = mapper.writeValueAsString(this);

        } catch (JsonGenerationException e) {}
        catch (JsonMappingException e) {}
        catch (IOException e) {}

        return ret;
	}

    public GameListPayload(List<SendableGame> games) {
        this.games = games;
    }

    public GameListPayload(String data) {
        var mapper = new ObjectMapper();

        GameListPayload parsed = null;

        try {
            parsed = mapper.readValue(data, GameListPayload.class);
        } catch (JsonMappingException e) {}
        catch (JsonParseException e) {}
        catch (IOException e) {}

        if(parsed != null) {
            this.games = parsed.games;
        }
    }

}
