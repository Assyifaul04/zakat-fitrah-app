package com.zakatfitrah.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DatabaseUtil.java
public class DatabaseUtil {
    private static final String URL = "jdbc:sqlite:zakatfitrah.db"; // table plus

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

