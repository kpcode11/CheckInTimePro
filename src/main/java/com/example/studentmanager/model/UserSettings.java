package com.example.studentmanager.model;

public record UserSettings(
    String username,
    String theme,
    int attendanceThreshold,
    boolean notificationsEnabled
) {}
