package cz.vse.pexeso.util;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FormValidator {

    public static boolean isEmpty(TextField... textFields) {
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean passwordMatch(PasswordField passwordField1, PasswordField passwordField2) {
        return passwordField1.getText().equals(passwordField2.getText());
    }

    public static boolean passwordStrong(PasswordField passwordField) {
        return passwordField.getText().length() >= 8;
    }
}
