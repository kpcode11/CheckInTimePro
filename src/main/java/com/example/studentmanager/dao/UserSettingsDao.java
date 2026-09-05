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
}
