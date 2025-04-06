package cz.vse.pexeso.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    private Button loginButton;
    @FXML
    private Label warningLabel;

    @FXML
    private void handleLoginClick() {
        //TODO: verify login, if successful, load the lobby, otherwise show warning
    }
}
