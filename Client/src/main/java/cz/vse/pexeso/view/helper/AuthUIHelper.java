package cz.vse.pexeso.view.helper;

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
        titleLabel.setText("Log in");
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);
        actionButton.setText("Log in");
        linkLabel.setText("Don't have an account yet? Register here!");
    }

    public static void switchToRegister(Label titleLabel,
                                        Label confirmPasswordLabel,
                                        PasswordField confirmPasswordField,
                                        Button actionButton,
                                        Label linkLabel) {
        titleLabel.setText("Register");
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setVisible(true);
        actionButton.setText("Register");
        linkLabel.setText("Already have an account? Log in here!");
    }
}
