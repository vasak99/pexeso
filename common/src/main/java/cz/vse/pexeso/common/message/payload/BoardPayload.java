package cz.vse.pexeso.common.message.payload;

public class BoardPayload implements MessagePayload {

    public String[][] cards;

    public BoardPayload(String data) {
        String[] rows = data.split(";");

    }

    @Override
    public String toSendable() {
        return "";
    }

}
