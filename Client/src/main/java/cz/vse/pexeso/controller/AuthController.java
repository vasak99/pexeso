package cz.vse.pexeso.controller;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.model.model.AuthModel;
import cz.vse.pexeso.model.result.AuthResultHandler;
import cz.vse.pexeso.model.result.AuthResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.util.FormValidator;
import cz.vse.pexeso.view.AuthUIHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController implements AuthResultListener {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final Navigator navigator;
    private final AuthModel authModel;

    private final AuthResultHandler resultHandler;

    @FXML
    private Label titleLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button actionButton;
    @FXML
    private Label warningLabel;
    @FXML
    private Label linkLabel;

    private Mode mode;

    private enum Mode {
        LOGIN,
        REGISTER
    }

    public AuthController(Navigator navigator, AuthModel authModel, Injector injector) {
        this.navigator = navigator;
        this.authModel = authModel;
        this.resultHandler = injector.createAuthResultHandler(this);
    }

    @FXML
    private void initialize() {
        resultHandler.register();
        switchToLogin();
        log.info("AuthController initialized.");
    }

    @FXML
    private void handleButtonClick() {
        disableFields(true);
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (mode == Mode.LOGIN) {
            login(username, password);
        } else {
            register(username, password, confirmPassword);
        }
    }

    private void login(String username, String password) {
        if (FormValidator.isEmpty(username, password)) {
            warningLabel.setText("Please fill in all fields.");
            return;
        }
        authModel.setCredentials(new UserCredentials(username, password));
        authModel.attemptLogin();
    }

    private void register(String username, String password, String confirmPassword) {
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
    private void handleLinkClick() {
        if (mode == Mode.LOGIN) {
            switchToRegister();
        } else {
            switchToLogin();
        }
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
            passwordField.clear();
            confirmPasswordField.clear();
            disableFields(false);
        });
    }

    private void switchToLogin() {
        mode = Mode.LOGIN;

        AuthUIHelper.switchToLogin(titleLabel, confirmPasswordLabel, confirmPasswordField, actionButton, linkLabel);
    }

    private void switchToRegister() {
        mode = Mode.REGISTER;

        AuthUIHelper.switchToRegister(titleLabel, confirmPasswordLabel, confirmPasswordField, actionButton, linkLabel);
    }

    private void disableFields(boolean disable) {
        usernameField.setDisable(disable);
        passwordField.setDisable(disable);
        confirmPasswordField.setDisable(disable);
    }
}
