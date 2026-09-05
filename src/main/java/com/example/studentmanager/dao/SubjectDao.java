package com.example.studentmanager.dao;

import com.example.studentmanager.model.Subject;
import com.example.studentmanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDao {
    public List<Subject> getSubjectsForUser(String username) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("subject_name"),
                        rs.getInt("marks_obtained"),
                        rs.getInt("marks_total"),
                        rs.getInt("target_percentage")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public boolean insertSubject(Subject subject) {
        String sql = "INSERT INTO subjects (username, subject_name, marks_obtained, marks_total, target_percentage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, subject.username());
            pstmt.setString(2, subject.subjectName());
            pstmt.setInt(3, subject.marksObtained());
            pstmt.setInt(4, subject.marksTotal());
            pstmt.setInt(5, subject.targetPercentage());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
