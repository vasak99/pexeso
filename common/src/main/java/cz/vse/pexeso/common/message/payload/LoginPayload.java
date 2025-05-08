package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.utils.MessageComponent;

public class LoginPayload implements MessagePayload {
    public String username;
    public String password;

    public LoginPayload(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String toSendable() {
        return "" + this.username + MessageComponent.DATA_SEPARATOR.getValue() + this.password;
    }

    public LoginPayload(String data) throws DataFormatException {
        String[] sep = data.split(MessageComponent.DATA_SEPARATOR.getValue());
        if(sep.length != 2) {
            throw new DataFormatException("Login data in wrong format");
        }
        this.username = sep[0];
        this.password = sep[1];
    }
}
