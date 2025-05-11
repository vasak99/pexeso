package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.model.model.AuthModel;
import cz.vse.pexeso.model.result.AuthResultHandler;
import cz.vse.pexeso.model.result.AuthResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.util.FormValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements AuthResultListener {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final Navigator navigator;
    private final AuthModel authModel;

    private final AuthResultHandler resultHandler;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label warningLabel;

    public RegisterController(Navigator navigator, AuthModel authModel, Injector injector) {
        this.navigator = navigator;
        this.authModel = authModel;
        this.resultHandler = injector.createAuthResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();
        log.info("RegisterController initialized.");
    }

    @FXML
    private void handleRegisterClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (FormValidator.isEmpty(username, password, confirmPassword)) {
            warningLabel.setText("Please fill in all fields.");
            return;
        } else if (!FormValidator.passwordMatch(password, confirmPassword)) {
            warningLabel.setText("Passwords do not match.");
            return;
        } else if (!FormValidator.passwordStrong(password)) {
            warningLabel.setText("Password must be at least 8 characters long.");
            return;
        }
        authModel.setCredentials(new UserCredentials(username, password));
        authModel.attemptRegister();
    }

    @FXML
    private void handleLoginLinkClick() {
        log.info("Switching to login screen.");
        resultHandler.unregister();
        navigator.goToLogin();
    }

    @Override
    public void onAuthSuccess(long playerId) {
        authModel.finalizeAuth(playerId, usernameField.getText(), passwordField.getText());
        resultHandler.unregister();
        Platform.runLater(navigator::goToLobby);
    }

    @Override
    public void onAuthError(String errorDescription) {
        Platform.runLater(() -> {
            warningLabel.setText(errorDescription + ", please try again.");
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        });
    }
}
