package com.hughcode;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hughs_mini_project",
                    "root", "n3u3da!"
            );
        } catch (Exception e) {
            e.printStackTrace();  // Print the actual exception
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}

