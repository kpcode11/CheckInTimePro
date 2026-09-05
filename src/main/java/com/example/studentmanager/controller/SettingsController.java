package com.example.studentmanager.controller;

import com.example.studentmanager.dao.UserSettingsDao;
import com.example.studentmanager.model.UserSettings;
import com.example.studentmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.util.Optional;

public class SettingsController {

    @FXML private ComboBox<String> themeComboBox;
    @FXML private Spinner<Integer> attendanceThresholdSpinner;
    @FXML private CheckBox notificationsCheckBox;
    
    private final UserSettingsDao userSettingsDao = new UserSettingsDao();

    @FXML
    public void initialize() {
        themeComboBox.getItems().addAll("Light", "Dark");
        
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            Optional<UserSettings> settingsOpt = userSettingsDao.getSettingsForUser(user.username());
            if (settingsOpt.isPresent()) {
                UserSettings settings = settingsOpt.get();
                themeComboBox.setValue(settings.theme());
                attendanceThresholdSpinner.getValueFactory().setValue(settings.attendanceThreshold());
                notificationsCheckBox.setSelected(settings.notificationsEnabled());
            } else {
                // Defaults
                themeComboBox.setValue("Dark");
                attendanceThresholdSpinner.getValueFactory().setValue(75);
                notificationsCheckBox.setSelected(true);
            }
        }
    }

    @FXML
    public void handleSaveSettings() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        
        UserSettings newSettings = new UserSettings(
            user.username(),
            themeComboBox.getValue(),
            attendanceThresholdSpinner.getValue(),
            notificationsCheckBox.isSelected()
        );
        
        if (userSettingsDao.saveSettings(newSettings)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Settings Saved");
            alert.setHeaderText(null);
            alert.setContentText("Your preferences have been saved successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save settings.");
            alert.showAndWait();
        }
    }
}
