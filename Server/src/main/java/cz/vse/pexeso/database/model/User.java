package cz.vse.pexeso.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User object to be mapped from database output
 */
public class User {

    public long id;
    public String name;
    public String password;

    /**
     * Construct object from given variables
     * @param id User id
     * @param name User name
     * @param password User password
     */
    public User(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Construct User list from a JDBC ResultSet
     * @param rs Database output
     * @return List<User>
     */
    public static List<User> fromResultSet(ResultSet rs) throws SQLException {
        var ret = new ArrayList<User>();

        while(rs.next()) {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            String password = rs.getString("password");

            ret.add(new User(id, name, password));
        }

        return ret;
    }

}
