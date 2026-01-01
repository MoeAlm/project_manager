package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * فئة الاتصال بقاعدة البيانات
 * Database Connection Utility Class
 */
public class DatabaseConnection {
    
    private static final String URL = Config.getDatabaseUrl();
    private static final String USER = Config.DB_USER;
    private static final String PASSWORD = Config.DB_PASSWORD;
    
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
