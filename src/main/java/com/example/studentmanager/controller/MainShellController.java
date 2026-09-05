package com.example.studentmanager.controller;

import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordLight;
import com.example.studentmanager.util.DatabaseConnection;
import com.example.studentmanager.util.Router;
import com.example.studentmanager.util.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class MainShellController {
    @FXML private StackPane contentArea;
    @FXML private Button themeToggleButton;

    @FXML private HBox errorBanner;
    @FXML private Label errorBannerLabel;

    private boolean isDark = true;
    private static MainShellController instance;

    @FXML
    public void initialize() {
        instance = this;
        Router.setMainContentArea(contentArea);
        Router.navigateContent("Dashboard.fxml");
        performConnectionCheck();
    }

    private void performConnectionCheck() {
        new Thread(() -> {
            if (!DatabaseConnection.checkConnection()) {
                showConnectionError("Database connection failed. Please check your internet or Supabase configuration.");
            }
        }).start();
    }

    public static void showConnectionError(String msg) {
        if (instance != null) {
            javafx.application.Platform.runLater(() -> {
                instance.errorBannerLabel.setText(msg);
                instance.errorBanner.setManaged(true);
                instance.errorBanner.setVisible(true);
            });
        }
    }

    @FXML
    public void handleRetryConnection() {
        errorBanner.setManaged(false);
        errorBanner.setVisible(false);
        performConnectionCheck();
    }

    @FXML
    public void navToDashboard() { Router.navigateContent("Dashboard.fxml"); }
    
    @FXML
    public void navToSubjects() { Router.navigateContent("Subjects.fxml"); }
    
    @FXML
    public void navToTasks() { Router.navigateContent("Tasks.fxml"); }
    
    @FXML
    public void navToAttendance() { Router.navigateContent("Attendance.fxml"); }
    
    @FXML
    public void navToReports() { Router.navigateContent("Reports.fxml"); }
    
    @FXML
    public void navToSettings() { Router.navigateContent("Settings.fxml"); }

    @FXML
    public void toggleTheme() {
        isDark = !isDark;
        if (isDark) {
            Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
            themeToggleButton.setText("Light Mode");
        } else {
            Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
            themeToggleButton.setText("Dark Mode");
        }
    }
    
    @FXML
    public void handleLogout() {
        SessionManager.getInstance().clearSession();
        Router.setMainContentArea(null);
        Router.navigateTo("Login.fxml");
    }
}
