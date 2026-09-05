package com.example.studentmanager.controller;

import com.example.studentmanager.service.AuthService;
import com.example.studentmanager.util.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;

public class SignUpController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private ProgressBar passwordStrengthMeter;
    @FXML private Label passwordStrengthLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordStrength(newVal);
        });
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            passwordStrengthMeter.setProgress(0);
            passwordStrengthLabel.setText("");
            passwordStrengthMeter.setStyle("");
            return;
        }
        
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*].*")) strength++;

        switch (strength) {
            case 0:
            case 1:
                passwordStrengthMeter.setProgress(0.33);
                passwordStrengthLabel.setText("Weak");
                passwordStrengthMeter.setStyle("-fx-accent: -color-danger-emphasis;");
                break;
            case 2:
            case 3:
                passwordStrengthMeter.setProgress(0.66);
                passwordStrengthLabel.setText("Medium");
                passwordStrengthMeter.setStyle("-fx-accent: -color-warning-emphasis;");
                break;
            case 4:
                passwordStrengthMeter.setProgress(1.0);
                passwordStrengthLabel.setText("Strong");
                passwordStrengthMeter.setStyle("-fx-accent: -color-success-emphasis;");
                break;
        }
    }

    @FXML
    public void handleSignUp() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Please fill all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        boolean success = authService.register(username, password);
        if (success) {
            errorLabel.setText("Sign up successful! Please log in.");
            errorLabel.setStyle("-fx-text-fill: -color-success-emphasis;");
            Router.navigateTo("Login.fxml");
        } else {
            errorLabel.setText("Username already exists.");
            errorLabel.setStyle("-fx-text-fill: -color-danger-emphasis;");
        }
    }

    @FXML
    public void handleGoToLogin() {
        Router.navigateTo("Login.fxml");
    }
}
