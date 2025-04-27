package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.ClientSession;
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

public class LoginController {
    public static final Logger log = LoggerFactory.getLogger(LoginController.class);
    UserCredentials userCredentials;
    @FXML
    private Label registrationConfirmationLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label warningLabel;

    @FXML
    private void initialize() {
        AppServices.initialize();
        AppServices.getMessageHandler().registerWithData(MessageTypeClient.LOGIN, this::handleSuccessfulLogin);
        AppServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR_LOGIN, this::handleInvalidLogin);
        if (AppServices.justRegistered) {
            registrationConfirmationLabel.setVisible(true);
            AppServices.justRegistered = false;
        }
        log.info("LoginController initialized.");
    }

    /**
     * Validates the input fields and sends a login message to the server.
     */
    @FXML
    private void handleLoginClick() {
        registrationConfirmationLabel.setVisible(false);
        if (FormValidator.isEmpty(usernameField, passwordField)) {
            warningLabel.setText("Please fill in all fields.");
        } else {
            warningLabel.setText("");
            userCredentials = new UserCredentials(usernameField.getText(), passwordField.getText());

            sendLoginMessage(userCredentials);
        }
        //handleSuccessfulLogin("abcd");
    }

    private void sendLoginMessage(UserCredentials userCredentials) {
        String message = MessageBuilder.buildLoginMessage(userCredentials);
        AppServices.getConnection().sendMessage(message);
        log.info("Login credentials submitted for verification.");
    }

    private void handleSuccessfulLogin(Object playerId) {
        log.info("Login successful.");
        AppServices.setClientSession(new ClientSession((String) playerId, userCredentials));
        SceneManager.switchScene(UIConstants.LOBBY_FXML);
    }

    private void handleInvalidLogin(Object errorMessage) {
        log.info("Login failed: {}", errorMessage);

        clearFields();
        warningLabel.setText(errorMessage + ", please try again.");
    }

    @FXML
    private void handleRegisterLinkClick() {
        log.info("Switching to register screen.");
        clearFields();
        warningLabel.setText("");
        sceneManager.switchScene(UIConstants.REGISTER_FXML);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
}