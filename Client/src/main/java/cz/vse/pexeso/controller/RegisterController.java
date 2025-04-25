package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    public static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    UserCredentials userCredentials;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label warningLabel;

    @FXML
    private void initialize() {
        AppServices.getMessageHandler().register(MessageTypeClient.REGISTER, this::handleSuccessfulRegistration);
        AppServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR_REGISTER, this::handleInvalidRegistration);
        log.info("RegisterController initialized.");
    }

    @FXML
    private void handleRegisterClick() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            warningLabel.setText("Please fill in all fields.");
        } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            warningLabel.setText("Passwords do not match.");
        } else if (passwordField.getText().length() < 8) {
            warningLabel.setText("Password must be at least 8 characters long.");
        } else {
            warningLabel.setText("");
            userCredentials = new UserCredentials(usernameField.getText(), passwordField.getText());

            sendRegisterMessage(userCredentials);
        }
    }

    private void sendRegisterMessage(UserCredentials userCredentials) {
        String message = MessageBuilder.buildRegisterMessage(userCredentials);
        AppServices.getConnection().sendMessage(message);
        log.info("Registration credentials submitted for verification.");
    }

    private void handleSuccessfulRegistration() {
        log.info("Registration successful.");
        AppServices.justRegistered = true;
        SceneManager.switchScene(UIConstants.LOGIN_FXML);
    }

    private void handleInvalidRegistration(Object errorMessage) {
        log.info("Registration failed: {}", errorMessage);
        clearFields();
        warningLabel.setText(errorMessage + ", please try again.");
    }

    @FXML
    private void handleLoginLinkClick() {
        log.info("Switching to login scene.");
        clearFields();
        warningLabel.setText("");
        SceneManager.switchScene(UIConstants.LOGIN_FXML);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
