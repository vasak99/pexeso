package cz.vse.pexeso.common.message.payload;

import java.util.List;

public class LobbyUpdatePayload implements MessagePayload {

    public String gameBoard;
    public List<String> players;

    public LobbyUpdatePayload() {
    }

    public String toSendable() {
        return "";
    }

    public LobbyUpdatePayload(String data) {

    }
}
