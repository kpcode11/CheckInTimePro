package com.example.studentmanager.model;

import java.time.LocalDate;

public record AttendanceRecord(
    int id,
    String username,
    String subjectName,
    boolean attended,
    LocalDate recordDate
) {}
