package com.example.studentmanager.dao;

import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {
    public List<AttendanceRecord> getAttendanceForUser(String username) {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance_records WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("record_date");
                    records.add(new AttendanceRecord(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("subject_name"),
                        rs.getBoolean("attended"),
                        date != null ? date.toLocalDate() : null
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
