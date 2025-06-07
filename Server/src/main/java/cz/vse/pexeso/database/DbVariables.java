package cz.vse.pexeso.database;

import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbVariables {

    public static final String HOST = "localhost";
    public static final String PORT = "5432";
    public static final String DB_NAME = "Pexeso";

    public static final String USER = "postgres";
    public static final String PASSWORD = "1144729";

    public String getConnectionString() {
        return "jdbc:postgresql://" + getHost() + ":" + getPort() + "/" + getDbName();
    }

    public static final Logger log = LoggerFactory.getLogger(DbVariables.class);

    private Properties properties;

    public DbVariables() {
        this.properties = this.loadProperties();
        Base64.getDecoder();
    }

    private Properties loadProperties() {
        Properties p = new Properties();

        try {
            FileReader fr = new FileReader("configuration.properties", StandardCharsets.UTF_8);
            p.load(fr);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return p;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public String getHost() {
        return getValue("DB_HOST");
    }

    public String getPort() {
        return getValue("DB_PORT");
    }

    public String getDbName() {
        return getValue("DB_NAME");
    }

    public String getUser() {
        return getValue("DB_USER");
    }

    public String getPassword() {
        return getValue("DB_PASSWORD");
    }

}
