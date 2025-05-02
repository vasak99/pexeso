package cz.vse.pexeso.controller;

import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.service.RegisterService;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final AppServices appServices = AppServices.getInstance();
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final RegisterService registerService = new RegisterService();

    private final ObserverWithData errorObserver = this::handleInvalidRegistration;
    private final ObserverWithData successObserver = this::handleSuccessfulRegistration;

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
        register();
        log.info("RegisterController initialized.");
    }

    @FXML
    private void handleRegisterClick() {
        String warning = registerService.register(usernameField.getText(), passwordField.getText(), confirmPasswordField.getText());
        warningLabel.setText(warning);
    }

    private void handleSuccessfulRegistration(Object playerId) {
        registerService.initializeSession((long) playerId, usernameField.getText(), passwordField.getText());
        unregister();
        Platform.runLater(() -> sceneManager.switchScene(UIConstants.LOBBY_FXML));
    }

    private void handleInvalidRegistration(Object errorMessage) {
        log.info("Registration failed: {}", errorMessage);
        clearFields();
        Platform.runLater(() -> warningLabel.setText(errorMessage + ", please try again."));
    }

    @FXML
    private void handleLoginLinkClick() {
        log.info("Switching to login scene.");
        unregister();
        sceneManager.switchScene(UIConstants.LOGIN_FXML);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void register() {
        appServices.getMessageHandler().registerWithData(MessageTypeClient.AUTH_SUCCESS, successObserver);
        appServices.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
    }

    private void unregister() {
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.AUTH_SUCCESS, successObserver);
        appServices.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
    }


}
