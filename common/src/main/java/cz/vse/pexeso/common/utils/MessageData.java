package cz.vse.pexeso.common.utils;


import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.payload.StartGameData;

public class MessageData {

    public static StartGameData getCreateGameData(String data) throws DataFormatException {
        String[] parts = data.split(MessageComponent.DATA_SEPARATOR.getValue());
        if(parts.length != 2) {
            throw new DataFormatException("Create game data in wrong format");
        }

        StartGameData ret = new StartGameData(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

        return ret;
    }

}
