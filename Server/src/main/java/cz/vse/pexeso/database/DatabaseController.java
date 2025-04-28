package cz.vse.pexeso.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseController {

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

}
