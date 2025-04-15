package cz.vse.pexeso.common.utils;

import java.util.ArrayList;
import java.util.List;

public enum MessageComponent {

    START("MESSAGE_START"),

    TYPE("MESSAGE_TYPE"),

    GAME_ID("GAME_ID"),

    DATA("DATA"),

    PLAYER_ID("PLAYER_ID"),

    TIMESTAMP("TIMESTAMP"),

    SEPARATOR("\n"),

    KEY_VALUE_SEPARATOR(": "),

    END("MESSAGE_END");

    private String value;

    MessageComponent(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static List<MessageComponent> getOrderedKeys() {
        var ret = new ArrayList<MessageComponent>();
        ret.add(TYPE);
        ret.add(GAME_ID);
        ret.add(PLAYER_ID);
        ret.add(DATA);

        return ret;
    }

    public static MessageComponent fromString(String value) {
        for (MessageComponent mc : values()) {
            if (mc.value.equals(value)) {
                return mc;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}
