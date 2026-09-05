package com.example.studentmanager.dao;

import com.example.studentmanager.model.UserSettings;
import com.example.studentmanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserSettingsDao {
    public Optional<UserSettings> getSettingsForUser(String username) {
        String sql = "SELECT * FROM user_settings WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new UserSettings(
                        rs.getString("username"),
                        rs.getString("theme"),
                        rs.getInt("attendance_threshold"),
                        rs.getBoolean("notifications_enabled")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    public boolean saveSettings(UserSettings settings) {
        String updateSql = "UPDATE user_settings SET theme = ?, attendance_threshold = ?, notifications_enabled = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, settings.theme());
            updateStmt.setInt(2, settings.attendanceThreshold());
            updateStmt.setBoolean(3, settings.notificationsEnabled());
            updateStmt.setString(4, settings.username());
            
            if (updateStmt.executeUpdate() > 0) {
                return true;
            }
            
            String insertSql = "INSERT INTO user_settings (username, theme, attendance_threshold, notifications_enabled) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, settings.username());
                insertStmt.setString(2, settings.theme());
                insertStmt.setInt(3, settings.attendanceThreshold());
                insertStmt.setBoolean(4, settings.notificationsEnabled());
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
