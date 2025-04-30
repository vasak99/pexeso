package cz.vse.pexeso.common.message;

public enum MessageType {
    LOGIN("LOGIN"),

    REVEAL("REVEAL"),

    STATUS("STATUS"),

    MOVE("MOVE"),

    CREATE_GAME("CREATE_GAME"),

    GAME_START("GAME_START"),

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
