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

    public boolean logAttendance(AttendanceRecord record) {
        // First try to update
        String updateSql = "UPDATE attendance_records SET attended = ? WHERE username = ? AND subject_name = ? AND record_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setBoolean(1, record.attended());
            updateStmt.setString(2, record.username());
            updateStmt.setString(3, record.subjectName());
            updateStmt.setDate(4, Date.valueOf(record.recordDate()));
            
            if (updateStmt.executeUpdate() > 0) {
                return true;
            }
            
            // If no rows updated, insert
            String insertSql = "INSERT INTO attendance_records (username, subject_name, record_date, attended) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, record.username());
                insertStmt.setString(2, record.subjectName());
                insertStmt.setDate(3, Date.valueOf(record.recordDate()));
                insertStmt.setBoolean(4, record.attended());
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
