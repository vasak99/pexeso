package cz.vse.pexeso.common.message.payload;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameStat {

    public long gameId;
    public String name;
    //public LocalDateTime start;
    //public LocalDateTime finish;
    public int score;

    // Required for Jackson
    public GameStat() {}

    public static List<GameStat> fromResultSet(ResultSet rs) {
        List<GameStat> ret = new ArrayList<>();

        try {
            while (rs.next()) {
                GameStat gs = new GameStat();
                gs.gameId = new BigDecimal(rs.getString("in_game_id")).longValue();
                gs.name = rs.getString("name");
                gs.score = rs.getInt("score");
                //gs.start = rs.getTimestamp("start").toLocalDateTime();
                //gs.finish = rs.getTimestamp("finish").toLocalDateTime();
                ret.add(gs);
            }
        } catch (SQLException e) {
        }

        return ret;
    }
}
