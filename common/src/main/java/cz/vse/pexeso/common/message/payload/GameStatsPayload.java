package cz.vse.pexeso.common.message.payload;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GameStatsPayload implements MessagePayload {

    public List<GameStat> games;

    public GameStatsPayload(List<GameStat> games) {
        this.games = games;
    }

    public GameStatsPayload(String data) {
        var mapper = new ObjectMapper();

        try {
            GameStatsPayload rr = mapper.readValue(data, GameStatsPayload.class);

            this.games = rr.games;
        } catch (Exception e) {
            this.games = new ArrayList<>();
        }
    }

    @Override
    public String toSendable() {
        var mapper = new ObjectMapper();

        String ret = "";
        try {
            mapper.writeValueAsString(this);
        } catch (Exception e) {}

        return ret;
    }

}
