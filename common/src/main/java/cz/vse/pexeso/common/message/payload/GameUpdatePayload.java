package cz.vse.pexeso.common.message.payload;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GameUpdatePayload implements MessagePayload {

    public String gameBoard;
    public List<SendablePlayer> players;
    public Long activePlayer;

    public GameUpdatePayload(String gameBoard, List<SendablePlayer> players, Long activePlayer) {
        this.gameBoard = gameBoard;
        this.players = players;
        this.activePlayer = activePlayer;
    }

    //empty constructor required by jackson
    public GameUpdatePayload() {
    }

    public GameUpdatePayload(String data) {
        var mapper = new ObjectMapper();

        try {
            var parsed = mapper.readValue(data, GameUpdatePayload.class);

            this.gameBoard = parsed.gameBoard;
            this.players = parsed.players;
            this.activePlayer = parsed.activePlayer;
        } catch(Exception e) {}
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

}

