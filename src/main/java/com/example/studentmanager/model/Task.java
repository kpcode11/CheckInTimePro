package com.example.studentmanager.model;

import java.time.LocalDate;

public record Task(
    int id,
    String username,
    String taskName,
    String category,
    LocalDate taskDate,
    String taskTime,
    String priority,
    Integer reminderMinutesBefore,
    String status
) {}
