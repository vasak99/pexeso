package cz.vse.pexeso.common.message.payload;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultPayload implements MessagePayload {

    public SendablePlayer[] scores;
    public SendablePlayer winner;

    public ResultPayload() {
    }

    public ResultPayload(SendablePlayer[] scores, SendablePlayer winner) {
        this.scores = scores;
        this.winner = winner;
    }

    public ResultPayload(String data) {
        var mapper = new ObjectMapper();

        try {
            ResultPayload parsed = mapper.readValue(data, ResultPayload.class);

            this.scores = parsed.scores;
            this.winner = parsed.winner;
        } catch (Exception e) {}
    }

    public String toSendable() {
        var mapper = new ObjectMapper();

        String ret = "";

        try {
            ret = mapper.writeValueAsString(this);
        } catch (Exception e) {}

        return ret;
    }
}
