package cz.vse.pexeso.view.helper;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for switching between login and registration forms in the authentication UI.
 * Provides utility methods to clear password fields, and enable/disable inputs.
 *
 * @author kott10
 * @version June 2025
 */
public final class AuthFormHelper {
    private static final Logger log = LoggerFactory.getLogger(AuthFormHelper.class);

    private AuthFormHelper() {
    }

    /**
     * Configures the UI to display login fields only.
     *
     * @param titleLabel           label showing the form title
     * @param confirmPasswordLabel label for the confirm-password field
     * @param confirmPasswordField confirm-password input field
     * @param actionButton         button to submit login
     * @param linkLabel            label to switch to registration
     */
    public static void switchToLogin(
            Label titleLabel,
            Label confirmPasswordLabel,
            PasswordField confirmPasswordField,
            Button actionButton,
            Label linkLabel
    ) {
        titleLabel.setText("Log in");
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);
        actionButton.setText("Log in");
        linkLabel.setText("Don't have an account yet? Register here!");
        log.debug("Switched to login form");
    }

    /**
     * Configures the UI to display registration fields, including confirm password.
     *
     * @param titleLabel           label showing the form title
     * @param confirmPasswordLabel label for the confirm-password field
     * @param confirmPasswordField confirm-password input field
     * @param actionButton         button to submit registration
     * @param linkLabel            label to switch to login
     */
    public static void switchToRegister(
            Label titleLabel,
            Label confirmPasswordLabel,
            PasswordField confirmPasswordField,
            Button actionButton,
            Label linkLabel
    ) {
        titleLabel.setText("Register");
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setVisible(true);
        actionButton.setText("Register");
        linkLabel.setText("Already have an account? Log in here!");
        log.debug("Switched to registration form");
    }

    /**
     * Clears the text in the password and confirm-password fields.
     *
     * @param passwordField        password input field
     * @param confirmPasswordField confirm-password input field
     */
    public static void clearPasswordFields(
            TextField passwordField,
            TextField confirmPasswordField
    ) {
        UICommonHelper.clearFields(passwordField, confirmPasswordField);
        log.debug("Cleared password fields");
    }

    /**
     * Enables or disables the username, password, and confirm-password fields.
     *
     * @param disable              true to disable, false to enable
     * @param usernameField        username input field
     * @param passwordField        password input field
     * @param confirmPasswordField confirm-password input field
     */
    public static void disableFields(
            boolean disable,
            TextField usernameField,
            TextField passwordField,
            TextField confirmPasswordField
    ) {
        UICommonHelper.setDisableAll(disable, usernameField, passwordField, confirmPasswordField);
        log.debug("Set disable={} on Auth form fields", disable);
    }
}