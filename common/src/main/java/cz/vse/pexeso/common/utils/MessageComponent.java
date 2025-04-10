package cz.vse.pexeso.common.utils;

public enum MessageComponent {

    START("MESSAGE_START"),

    END("MESSAGE_END");

    private String value;

    MessageComponent(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
