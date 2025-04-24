package cz.vse.pexeso.common.message;

public enum MessageType {
    LOGIN("LOGIN"),

    REVEAL("REVEAL"),

    STATUS("STATUS"),

    MOVE("MOVE"),

    CREATE_GAME("CREATE_GAME"),

    REDIRECT("REDIRECT"),

    RESULT("RESULT"),
    //START,
    // CARD,
    // PAIR,
    // STATUS,
    // RESULT;

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
