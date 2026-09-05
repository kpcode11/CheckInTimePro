package com.example.studentmanager.controller;

import com.example.studentmanager.service.AuthService;
import com.example.studentmanager.util.Router;
import com.example.studentmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMeCheck;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            var user = authService.login(username, password);
            SessionManager.getInstance().setCurrentUser(user);
            errorLabel.setText("Login successful!");
            errorLabel.setStyle("-fx-text-fill: -color-success-emphasis;");
            Router.navigateTo("MainShell.fxml");
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill: -color-danger-emphasis;");
        }
    }

    @FXML
    public void handleGoToSignUp() {
        Router.navigateTo("SignUp.fxml");
    }
}
