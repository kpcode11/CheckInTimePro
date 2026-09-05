package com.example.studentmanager.controller;

import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordLight;
import com.example.studentmanager.util.Router;
import com.example.studentmanager.util.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainShellController {
    @FXML private StackPane contentArea;
    @FXML private Button themeToggleButton;

    private boolean isDark = true;

    @FXML
    public void initialize() {
        Router.setMainContentArea(contentArea);
        Router.navigateContent("Dashboard.fxml");
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
