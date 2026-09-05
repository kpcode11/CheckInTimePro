package com.example.studentmanager.dao;

import com.example.studentmanager.model.Task;
import com.example.studentmanager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    public List<Task> getTasksForUser(String username) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("task_date");
                    tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("task_name"),
                        rs.getString("category"),
                        date != null ? date.toLocalDate() : null,
                        rs.getString("task_time"),
                        rs.getString("priority"),
                        rs.getInt("reminder_minutes_before"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public boolean insertTask(Task task) {
        String sql = "INSERT INTO tasks (username, task_name, category, task_date, task_time, priority, reminder_minutes_before, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.username());
            pstmt.setString(2, task.taskName());
            pstmt.setString(3, task.category());
            pstmt.setDate(4, task.taskDate() != null ? Date.valueOf(task.taskDate()) : null);
            pstmt.setString(5, task.taskTime());
            pstmt.setString(6, task.priority());
            if (task.reminderMinutesBefore() != null) {
                pstmt.setInt(7, task.reminderMinutesBefore());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            pstmt.setString(8, task.status());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
