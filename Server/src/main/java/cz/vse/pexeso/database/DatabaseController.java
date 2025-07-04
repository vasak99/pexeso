package cz.vse.pexeso.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.message.payload.GameStat;
import cz.vse.pexeso.database.model.User;
import cz.vse.pexeso.game.Player;

/**
 * Methods for handling database communication
 */
public class DatabaseController {
    public static final Logger log = LoggerFactory.getLogger(DatabaseController.class);

    private Connection db;

    public DatabaseController() throws SQLException {
        var dbs = new DbVariables();
        Properties props = new Properties();
        props.setProperty("user", dbs.getUser());
        props.setProperty("password", dbs.getPassword());
        this.db = DriverManager.getConnection(dbs.getConnectionString(), props);
    }

    /**
     * Exposes a singular application-wide database connection
     * @return {@link Connection}
     */
    public Connection getDbConnection() {
        return this.db;
    }

    /**
     * Retrieves a {@link User} from database
     * @param playerId ID of a player
     */
    public User getUserById(long playerId) {
        try {
            var ps = this.db.prepareStatement("select * from Users where id = ?");
            ps.setLong(1, playerId);

            ResultSet res = ps.executeQuery();
            List<User> users = User.fromResultSet(res);

            if(users.size() == 1) {
                return users.get(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Could not retrieve user with ID '{}' from database", playerId);
            return null;
        }
    }

    /**
     * Saves game result to database
     * @param gameId in game ID of game object
     * @param gameName user-given name of game
     * @param start date & time of game start
     * @param finish date & time of game finish
     * @param players list of players
     */
    public void saveGameResult(String gameId, String gameName, LocalDateTime start, LocalDateTime finish, List<Player> players) {
        long dbGameId = 0;
        try {
            var ps1 = this.db.prepareStatement("insert into game (in_game_id, name, winner, start, finish) values (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

            Player winner = players.getFirst();
            for(var pl : players) {
                if(winner.getScore() < pl.getScore()) {
                    winner = pl;
                }
            }

            ps1.setString(1, gameId);
            ps1.setString(2, gameName);
            ps1.setLong(3, winner.getPlayerId());
            ps1.setTimestamp(4, Timestamp.valueOf(start));
            ps1.setTimestamp(5, Timestamp.valueOf(finish));

            ps1.executeUpdate();
            var rs = ps1.getGeneratedKeys();
            if(rs.next()) {
                System.out.println("got here");
                dbGameId = rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        try {
            for(var pl : players) {
                var ps2 = this.db.prepareStatement("insert into game_user (game_id, player_id, score) values(?, ?, ?);");

                ps2.setLong(1, dbGameId);
                ps2.setLong(2, pl.getPlayerId());
                ps2.setInt(3, pl.getScore());

                ps2.executeQuery();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

    }

    /**
     * Retrieves a list of previously played games
     * @param playerId player ID
     * @return List<GameStat>
     */
    public List<GameStat> getAllPlayerGames(long playerId) {
        try {
            var ps = this.db.prepareStatement("select * from game_user join game on (game_user.game_id = game.id) where game_user.player_id = ?;");

            ps.setLong(1, playerId);

            var rs = ps.executeQuery();
            return GameStat.fromResultSet(rs);
        } catch (SQLException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

}
