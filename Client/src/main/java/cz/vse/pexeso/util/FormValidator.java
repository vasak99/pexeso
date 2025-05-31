package cz.vse.pexeso.util;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.GameRoom;

public final class FormValidator {

    private FormValidator() {
    }

    public static boolean isEmpty(String... textFields) {
        for (String textField : textFields) {
            if (textField.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean passwordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean passwordStrong(String password) {
        return password.length() >= 8;
    }

    public static String validateLoginForm(String username, String password) {
        if (isEmpty(username, password)) {
            return Strings.FILL_IN_FIELDS;
        }
        return null;
    }

    public static String validateRegisterForm(String username, String password, String confirmPassword) {
        if (isEmpty(username, password, confirmPassword)) {
            return Strings.FILL_IN_FIELDS;
        }

        if (isTooLong(username)) {
            return Strings.NAME_TOO_LONG;
        }

        if (!passwordMatch(password, confirmPassword)) {
            return Strings.PASSWORD_NO_MATCH;
        }

        if (!passwordStrong(password)) {
            return Strings.PASSWORD_SHORT;
        }
        return null;
    }

    public static String validateGameRoomForm(String name, GameRoom.BoardSize boardSize, String customBoardSize) {
        if (name.trim().isEmpty()) {
            return Strings.CHOOSE_NAME;
        }

        if (boardSize == null) {
            return Strings.CHOOSE_BOARD_SIZE;
        }

        if (boardSize == GameRoom.BoardSize.CUSTOM && customBoardSize.isEmpty()) {
            return Strings.CHOOSE_CUSTOM_BOARD_SIZE;
        }

        if (isTooLong(name)) {
            return Strings.NAME_TOO_LONG;
        }

        if (boardSize == GameRoom.BoardSize.CUSTOM
                && (Integer.parseInt(customBoardSize) < Variables.MIN_CARDS
                || Integer.parseInt(customBoardSize) > Variables.MAX_CARDS
                || Integer.parseInt(customBoardSize) % 2 != 0)) {
            return String.format(Strings.BOARD_SIZE_REQUIREMENTS, Variables.MIN_CARDS, Variables.MAX_CARDS);
        }
        return null;
    }

    private static boolean isTooLong(String name) {
        return name.length() > 16;
    }
}
