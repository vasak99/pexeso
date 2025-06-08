package cz.vse.pexeso.common.message.payload;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GameStatsPayload implements MessagePayload {

    public List<GameStat> games;

    public GameStatsPayload() {
        this.games = new ArrayList<>();
    }

    public GameStatsPayload(List<GameStat> games) {
        this.games = games;
    }

    public GameStatsPayload(String data) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            GameStatsPayload parsed = mapper.readValue(data, GameStatsPayload.class);
            this.games = parsed.games;
        } catch (Exception e) {
            this.games = new ArrayList<>();
        }
    }

    @Override
    public String toSendable() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "";
        }
    }
}
