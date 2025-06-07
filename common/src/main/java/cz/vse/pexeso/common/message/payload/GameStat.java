package cz.vse.pexeso.common.message.payload;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameStat {

    public long gameId;
    public String name;
    public LocalDateTime start;
    public LocalDateTime finish;
    public int score;

    public static List<GameStat> fromResultSet(ResultSet rs) {
        var ret = new ArrayList<GameStat>();

        try {
            while(rs.next()) {
                GameStat gs = new GameStat();
                gs.gameId = Long.parseLong(rs.getString("in_game_id"));
                gs.name = rs.getString("name");
                gs.score = rs.getInt("score");
                gs.start = rs.getTimestamp("start").toLocalDateTime();
                gs.finish = rs.getTimestamp("finish").toLocalDateTime();

                ret.add(gs);
            }
        } catch (SQLException e) {}

        return ret;
    }


}
