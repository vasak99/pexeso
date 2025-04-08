package cz.vse.pexeso.controller;

import cz.vse.pexeso.helper.SceneManager;
import cz.vse.pexeso.model.User;
import cz.vse.pexeso.model.observer.MessageType;
import cz.vse.pexeso.network.ClientConnection;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.network.MessageHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label warningLabel;

    @FXML
    private void initialize() {
        MessageHandler.getInstance().register(MessageType.LOGIN_OK, this::handleSuccessfulLogin);
        MessageHandler.getInstance().register(MessageType.LOGIN_INVALID, this::handleInvalidLogin);
        MessageHandler.getInstance().register(MessageType.LOGIN_DUPLICATE, this::handleDuplicateLogin);
    }

    @FXML
    private void handleLoginClick() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            warningLabel.setText("Please fill in all fields.");
        } else {
            warningLabel.setText("");
            User user = new User(usernameField.getText(), passwordField.getText());
            ClientConnection connection = new ClientConnection();

            String loginMessage = MessageBuilder.getInstance().buildLoginMessage(user);
            connection.sendMessage(loginMessage);
        }
    }

    private void handleSuccessfulLogin() {
        SceneManager.switchScene("/cz/vse/pexeso/fxml/lobby.fxml");
    }

    private void handleInvalidLogin() {
        warningLabel.setText("Invalid username or password.");
    }

    private void handleDuplicateLogin() {
        warningLabel.setText("User already logged in.");
    }
}
