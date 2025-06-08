package cz.vse.pexeso.controller;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.model.model.AuthModel;
import cz.vse.pexeso.model.result.AuthResultHandler;
import cz.vse.pexeso.model.result.AuthResultListener;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.util.FormValidator;
import cz.vse.pexeso.view.helper.AuthFormHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the authentication screen. Handles login and registration actions, performs form validation.
 * Disables/enables form fields and displays warnings on validation or server errors.
 *
 * @author kott10
 * @version June 2025
 */
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

    /**
     * Constructs an AuthController with required dependencies injected.
     *
     * @param navigator for screen navigation
     * @param authModel for authentication logic
     * @param injector  to obtain AuthResultHandler
     */
    public AuthController(Navigator navigator, AuthModel authModel, Injector injector) {
        this.navigator = navigator;
        this.authModel = authModel;
        this.resultHandler = injector.getHandlerFactory().createAuthResultHandler(this);
    }

    /**
     * Initializes the controller by registering message observers and setting the initial form to login mode.
     */
    @FXML
    private void initialize() {
        resultHandler.register();
        switchToLogin();
        log.info("AuthController initialized");
    }

    /**
     * Handles click on the login/register action button.
     * Disables form fields, and delegates to either login or register logic.
     */
    @FXML
    private void handleAuthButtonClick() {
        AuthFormHelper.disableFields(true, usernameField, passwordField, confirmPasswordField);

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        switch (mode) {
            case LOGIN -> login(username, password);
            case REGISTER -> register(username, password, confirmPassword);
        }
    }

    /**
     * Handles click on the toggle link between login and registration screens.
     * Clears password fields and switches the mode accordingly.
     */
    @FXML
    private void handleLinkClick() {
        if (mode == Mode.LOGIN) {
            switchToRegister();
        } else {
            switchToLogin();
        }

        AuthFormHelper.clearPasswordFields(passwordField, confirmPasswordField);
    }


    /**
     * Validates login input, sets credentials, and attempts login.
     *
     * @param username the username to login
     * @param password the password to login
     */
    private void login(String username, String password) {
        String warning = FormValidator.validateLoginForm(username, password);
        if (warning != null) {
            showWarning(warning);
            return;
        }

        authModel.setCredentials(new UserCredentials(username, password));
        try {
            authModel.attemptLogin();
        } catch (IllegalStateException e) {
            showWarning("Error occurred, please try again later");
        }
        log.info("Login attempt sent for user={}", username);
    }

    /**
     * Validates registration input, sets credentials, and attempts registration.
     *
     * @param username        the username to register
     * @param password        the password to register
     * @param confirmPassword the confirmation password
     */
    private void register(String username, String password, String confirmPassword) {
        String warning = FormValidator.validateRegisterForm(username, password, confirmPassword);
        if (warning != null) {
            showWarning(warning);
            return;
        }

        authModel.setCredentials(new UserCredentials(username, password));
        try {
            authModel.attemptRegister();
        } catch (IllegalStateException e) {
            showWarning("Error occurred, please try again later");
        }
        log.info("Registration attempt sent for user={}", username);
    }

    /**
     * Handles successful authentication by receiving player ID.
     * Unregisters the result handler, finalizes authentication, and navigates to the lobby.
     *
     * @param playerId the authenticated player's ID
     */
    @Override
    public void onIdentity(long playerId) {
        resultHandler.unregister();
        authModel.finalizeAuth(playerId);
        log.info("Received identity from server: playerId={}", playerId);
        Platform.runLater(navigator::goToLobby);
    }

    /**
     * Handles authentication error by displaying a warning and clearing password fields.
     *
     * @param errorDescription the error description received from the server
     */
    @Override
    public void onError(String errorDescription) {
        Platform.runLater(() -> {
            authModel.emptyCredentials();
            showWarning(String.format("%s, please try again.", errorDescription));
            AuthFormHelper.clearPasswordFields(passwordField, confirmPasswordField);
            log.warn("Authentication error received: {}", errorDescription);
        });
    }

    /**
     * Handles game server updates by updating the lobby in the auth model.
     *
     * @param data update payload
     */
    @Override
    public void onGameServerUpdate(GameListPayload data) {
        authModel.updateLobby(data);
    }

    /**
     * Switches the form to login mode, updating UI elements accordingly.
     */
    private void switchToLogin() {
        mode = Mode.LOGIN;
        AuthFormHelper.switchToLogin(titleLabel, confirmPasswordLabel, confirmPasswordField, actionButton, linkLabel);
    }

    /**
     * Switches the form to registration mode, updating UI elements accordingly.
     */
    private void switchToRegister() {
        mode = Mode.REGISTER;
        AuthFormHelper.switchToRegister(titleLabel, confirmPasswordLabel, confirmPasswordField, actionButton, linkLabel);
    }

    /**
     * Displays a warning message in the UI and enables the form fields.
     *
     * @param warning the warning message to display
     */
    private void showWarning(String warning) {
        warningLabel.setText(warning);
        AuthFormHelper.disableFields(false, usernameField, passwordField, confirmPasswordField);
    }
}