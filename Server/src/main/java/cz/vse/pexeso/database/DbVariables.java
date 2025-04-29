package cz.vse.pexeso.database;

public class DbVariables {

    public static final String HOST = "localhost";
    public static final String PORT = "5432";
    public static final String DB_NAME = "Pexeso";

    public static final String USER = "postgres";
    public static final String PASSWORD = "1144729";

    public static String getConnectionString() {
        return "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
    }

}
