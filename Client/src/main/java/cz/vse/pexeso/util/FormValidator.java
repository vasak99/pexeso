package cz.vse.pexeso.util;

public class FormValidator {

    public static boolean isEmpty(String... textFields) {
        for (String textField : textFields) {
            if (textField.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean passwordMatch(String passwordField1, String passwordField2) {
        return passwordField1.equals(passwordField2);
    }

    public static boolean passwordStrong(String passwordField) {
        return passwordField.length() >= 8;
    }
}
