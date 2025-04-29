package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.utils.MessageComponent;

public class RegisterPayload implements MessagePayload {

    public String name;
    public String password;

    public RegisterPayload(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String toSendable() {
        return "" + this.name + MessageComponent.DATA_SEPARATOR.getValue() + this.password;
    }

    public RegisterPayload(String data) throws DataFormatException {
        String[] sep = data.split(MessageComponent.DATA_SEPARATOR.getValue());
        if(sep.length != 2) {
            throw new DataFormatException("Login data in wrong format");
        }
        this.name = sep[0];
        this.password = sep[1];
    }

}
