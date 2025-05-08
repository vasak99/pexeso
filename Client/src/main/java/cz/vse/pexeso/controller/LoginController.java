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

public class LoginController implements AuthResultListener {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final Navigator navigator;
    private final AuthModel authModel;

    private final AuthResultHandler resultHandler;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label warningLabel;

    public LoginController(Navigator navigator, AuthModel authModel, Injector injector) {
        this.navigator = navigator;
        this.authModel = authModel;
        this.resultHandler = injector.createAuthResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();
        log.info("LoginController initialized.");
    }

    @FXML
    private void handleLoginClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (FormValidator.isEmpty(username, password)) {
            warningLabel.setText("Please fill in all fields.");
            return;
        }
        authModel.setCredentials(new UserCredentials(username, password));
        authModel.attemptLogin();
    }

    @FXML
    private void handleRegisterLinkClick() {
        log.info("Switching to register screen");
        resultHandler.unregister();
        navigator.goToRegister();
    }

    @Override
    public void onAuthSuccess(long playerId) {
        authModel.finalizeAuth(playerId, usernameField.getText(), passwordField.getText());
        resultHandler.unregister();
        Platform.runLater(navigator::goToLobby);
    }

    @Override
    public void onAuthError(String errorMessage) {
        Platform.runLater(() -> {
            warningLabel.setText(errorMessage + ", please try again.");
            usernameField.clear();
            passwordField.clear();
        });
    }
}