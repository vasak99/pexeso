package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.User;
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
    User user;
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
        log.info("LoginController initialized.");
    }

    /**
     * Validates the input fields and sends a login message to the server.
     */
    @FXML
    private void handleLoginClick() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            warningLabel.setText("Please fill in all fields.");
        } else {
            warningLabel.setText("");
            user = new User(usernameField.getText(), passwordField.getText());

            sendLoginMessage(user);
        }
    }

    private void sendLoginMessage(User user) {
        String message = MessageBuilder.buildLoginMessage(user);
        AppServices.getConnection().sendMessage(message);
        log.info("Login credentials submitted for verification.");
    }

    private void handleSuccessfulLogin(Object playerId) {
        log.info("Login successful.");
        AppServices.setClientSession(new ClientSession((String) playerId, user));
        SceneManager.switchScene(UIConstants.LOBBY_FXML);
    }

    private void handleInvalidLogin(Object errorMessage) {
        log.info("Login failed: {}", errorMessage);

        usernameField.clear();
        passwordField.clear();
        warningLabel.setText(errorMessage + ", please try again.");
    }

    @FXML
    private void handleRegisterLinkClick() {
        log.info("Switching to register screen.");
        usernameField.clear();
        passwordField.clear();
        warningLabel.setText("");
        SceneManager.switchScene(UIConstants.REGISTER_FXML);
    }
}
