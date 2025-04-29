package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.service.LoginService;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final AppServices appServices = AppServices.getInstance();
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final LoginService loginService = new LoginService();
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label warningLabel;
    private final ObserverWithData errorObserver = this::handleInvalidLogin;

    @FXML
    private void initialize() {
        appServices.getMessageHandler().registerWithData(MessageTypeClient.AUTH_SUCCESS, successObserver);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
        log.info("LoginController initialized.");
    }    private final ObserverWithData successObserver = this::handleSuccessfulLogin;

    @FXML
    private void handleLoginClick() {
        String warning = loginService.login(usernameField.getText(), passwordField.getText());
        warningLabel.setText(warning);
    }

    private void handleSuccessfulLogin(Object playerId) {
        loginService.initializeSession((long) playerId, usernameField.getText(), passwordField.getText());
        unregister();
        Platform.runLater(() -> sceneManager.switchScene(UIConstants.LOBBY_FXML));
    }

    private void handleInvalidLogin(Object errorMessage) {
        log.info("Login failed: {}", errorMessage);
        clearFields();
        Platform.runLater(() -> warningLabel.setText(errorMessage + ", please try again."));
    }

    @FXML
    private void handleRegisterLinkClick() {
        log.info("Switching to register screen.");
        unregister();
        sceneManager.switchScene(UIConstants.REGISTER_FXML);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    private void unregister() {
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.AUTH_SUCCESS, successObserver);
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
    }




}