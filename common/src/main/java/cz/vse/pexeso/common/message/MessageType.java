package cz.vse.pexeso.common.message;

public enum MessageType {
    LOGIN("LOGIN"),

    REVEAL("REVEAL"),

    BOARD("BOARD"),

    STATUS("STATUS"),

    MOVE("MOVE"),

    CREATE_GAME("CREATE_GAME"),

    GAME_START("GAME_START"),

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
