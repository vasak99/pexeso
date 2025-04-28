package cz.vse.pexeso.common.message.payload;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.utils.MessageComponent;

public class StartGameData implements MessagePayload {

    public int capacity;
    public int cardCount;

    public StartGameData(int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

    public String toSendable() {
        return "" + this.capacity + MessageComponent.DATA_SEPARATOR.getValue() + this.cardCount;
    }

    public StartGameData(String data) throws DataFormatException {
        String[] sep = data.split(MessageComponent.DATA_SEPARATOR.getValue());
        if(sep.length != 2) throw new DataFormatException("Create game data in wrong format");

        this.capacity = Integer.parseInt(sep[0]);
        this.cardCount = Integer.parseInt(sep[1]);
    }

}
