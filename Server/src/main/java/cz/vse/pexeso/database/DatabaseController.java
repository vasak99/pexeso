package cz.vse.pexeso.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.database.model.User;

public class DatabaseController {
    public static final Logger log = LoggerFactory.getLogger(DatabaseController.class);

    private Connection db;

    public DatabaseController() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DbVariables.USER);
        props.setProperty("password", DbVariables.PASSWORD);
        this.db = DriverManager.getConnection(DbVariables.getConnectionString(), props);
    }

    public Connection getDbConnection() {
        return this.db;
    }

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

}
