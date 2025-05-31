package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.util.Strings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public final class AuthUIHelper {
    private AuthUIHelper() {
    }

    public static void switchToLogin(Label titleLabel,
                                     Label confirmPasswordLabel,
                                     PasswordField confirmPasswordField,
                                     Button actionButton,
                                     Label linkLabel) {
        titleLabel.setText(Strings.LOG_IN);
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);
        actionButton.setText(Strings.LOG_IN);
        linkLabel.setText(Strings.REGISTER_LINK);
    }

    public static void switchToRegister(Label titleLabel,
                                        Label confirmPasswordLabel,
                                        PasswordField confirmPasswordField,
                                        Button actionButton,
                                        Label linkLabel) {
        titleLabel.setText(Strings.REGISTER);
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setVisible(true);
        actionButton.setText(Strings.REGISTER);
        linkLabel.setText(Strings.LOGIN_LINK);
    }
}
