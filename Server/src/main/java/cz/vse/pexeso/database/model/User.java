package cz.vse.pexeso.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {

    public long id;
    public String name;
    public String password;

    public User(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public static List<User> fromResultSet(ResultSet rs) throws SQLException {
        var ret = new ArrayList<User>();

        while(rs.next()) {
            long id = rs.getLong("id");
            String name = rs.getString("id");
            String password = rs.getString("id");

            ret.add(new User(id, name, password));
        }

        return ret;
    }

}
