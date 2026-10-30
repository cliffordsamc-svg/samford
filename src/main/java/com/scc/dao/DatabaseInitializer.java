package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    /** Creates the tables and the default admin. Returns true if the database is ready. */
    public static boolean initialize() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles    TEXT NOT NULL,
                status   TEXT NOT NULL DEFAULT 'PENDING'
            )
            """;

        // Default admin: username "admin", password "admin123" (stored as a SHA-256 hash)
        String seedAdmin = """
            INSERT OR IGNORE INTO users (username, password, roles, status)
            VALUES ('admin',
                    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
                    'ADMIN',
                    'APPROVED')
            """;

        // Converts accounts saved earlier with status ACTIVE to the new APPROVED status
        String migrateStatus = "UPDATE users SET status = 'APPROVED' WHERE status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(seedAdmin);
            stmt.execute(migrateStatus);
            return true;
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
            return false;
        }
    }
}