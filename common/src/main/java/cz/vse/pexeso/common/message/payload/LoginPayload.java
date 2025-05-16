package cz.vse.pexeso.common.message.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.vse.pexeso.common.exceptions.DataFormatException;

public class LoginPayload implements MessagePayload {
    public String username;
    public String password;

    public LoginPayload(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String toSendable() {
        String ret = "";

        var mapper = new ObjectMapper();

        try {
            ret = mapper.writeValueAsString(this);
        } catch (Exception e) {}

        return ret;
    }

    public LoginPayload(String data) throws DataFormatException {
        var mapper = new ObjectMapper();

        try {
            var dd = mapper.readValue(data, LoginPayload.class);
            System.out.println(dd.username);
            System.out.println(dd.password);
            this.username = dd.username;
            this.password = dd.password;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
