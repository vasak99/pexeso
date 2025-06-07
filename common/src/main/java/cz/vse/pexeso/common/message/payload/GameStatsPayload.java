package cz.vse.pexeso.common.message.payload;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GameStatsPayload implements MessagePayload {

    public List<GameStat> games;

    public GameStatsPayload(List<GameStat> games) {
        this.games = games;
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
