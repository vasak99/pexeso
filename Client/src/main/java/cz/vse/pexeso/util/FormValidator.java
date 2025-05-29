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
            return "Please fill in all fields.";
        }
        return null;
    }

    public static String validateRegisterForm(String username, String password, String confirmPassword) {
        if (isEmpty(username, password, confirmPassword)) {
            return "Please fill in all fields.";
        }

        if (!validateNameLength(username)) {
            return "Name can not be longer than 16 characters";
        }

        if (!passwordMatch(password, confirmPassword)) {
            return "Passwords do not match.";
        }

        if (!passwordStrong(password)) {
            return "Password must be at least 8 characters long.";
        }
        return null;
    }

    public static String validateGameRoomForm(String name, GameRoom.BoardSize boardSize, String customBoardSize) {
        if (name.trim().isEmpty()) {
            return "Choose name";
        }

        if (boardSize == null) {
            return "Choose board size";
        }

        if (boardSize == GameRoom.BoardSize.CUSTOM && customBoardSize.isEmpty()) {
            return "Choose custom board size";
        }

        if (!validateNameLength(name)) {
            return "Name can not be longer than 16 characters";
        }

        if (boardSize == GameRoom.BoardSize.CUSTOM
                && (Integer.parseInt(customBoardSize) < Variables.MIN_CARDS
                || Integer.parseInt(customBoardSize) > Variables.MAX_CARDS
                || Integer.parseInt(customBoardSize) % 2 != 0)) {
            return "The board size must be an even number between " + Variables.MIN_CARDS + " and " + Variables.MAX_CARDS + ".";
        }
        return null;
    }

    private static boolean validateNameLength(String name) {
        return name.length() <= 16;
    }
}
