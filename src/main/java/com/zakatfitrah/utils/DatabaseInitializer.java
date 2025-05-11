package com.zakatfitrah.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS jamaah (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nama TEXT NOT NULL," +
                "alamat TEXT NOT NULL," +
                "jumlah_anggota INTEGER NOT NULL," +
                "jenis_zakat TEXT NOT NULL," +
                "nominal REAL NOT NULL," +
                "tanggal_pembayaran TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Connection connection = DatabaseUtil.connect();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}