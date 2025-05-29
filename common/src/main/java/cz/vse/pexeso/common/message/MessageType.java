package cz.vse.pexeso.common.message;

public enum MessageType {
    LOGIN("LOGIN"),

    REVEAL("REVEAL"),

    BOARD("BOARD"),

    LOBBY_UPDATE("LOBBY_UPDATE"),

    GAME_SERVER_UPDATE("GAME_SERVER_UPDATE"),

    GAME_UPDATE("GAME_UPDATE"),

    INVALID_MOVE("INVALID_GAME"),

    STATUS("STATUS"),

    MOVE("MOVE"),

    CREATE_GAME("CREATE_GAME"),

    DELETE_GAME("DELETE_GAME"),

    EDIT_GAME("EDIT_GAME"),

    JOIN_GAME("JOIN_GAME"),

    LEAVE_GAME("LEAVE_GAME"),

    PLAYER_READY("PLAYER_READY"),

    KICK_PLAYER("KICK_PLAYER"),

    START_GAME("START_GAME"),

    REDIRECT("REDIRECT"),

    IDENTITY("IDENTITY"),

    REQUEST_IDENTITY("REQUEST_IDENTITY"),

    RESULT("RESULT"),

    GIVE_UP("GIVE_UP"),

    ERROR("ERROR"),
    REGISTER("REGISTER"),;

    private String value;

    public String getValue() {
        return this.value;
    }

    MessageType(String value) {
        this.value = value;
    }
}
