package com.example.studentmanager.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL"));
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASS"));
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized. Check your .env file.");
        }
        return dataSource.getConnection();
    }
}
