package com.example.studentmanager.model;

import java.sql.Timestamp;

public record User(
    int id,
    String username,
    String passwordHash,
    Timestamp createdAt
) {}
