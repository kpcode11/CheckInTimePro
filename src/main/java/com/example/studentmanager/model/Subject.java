package com.example.studentmanager.model;

public record Subject(
    int id,
    String username,
    String subjectName,
    int marksObtained,
    int marksTotal,
    int targetPercentage
) {}
