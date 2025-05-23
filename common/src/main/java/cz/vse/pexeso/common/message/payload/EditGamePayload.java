package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.utils.MessageComponent;

public class EditGamePayload implements MessagePayload {

    public String name;
    public int capacity;
    public int cardCount;

    public EditGamePayload(String name, int capacity, int cardCount) {
        this.name = name;
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public String toSendable() {
        return "" + this.name + MessageComponent.DATA_SEPARATOR.getValue() + this.capacity + MessageComponent.DATA_SEPARATOR.getValue() + this.cardCount;
    }

    public EditGamePayload(String data) throws DataFormatException {
        String[] sep = data.split(MessageComponent.DATA_SEPARATOR.getValue());
        if (sep.length != 3) {
            throw new DataFormatException("Edit game data in wrong format");
        }

        this.name = sep[0];
        this.capacity = Integer.parseInt(sep[1]);
        this.cardCount = Integer.parseInt(sep[2]);
    }
}
