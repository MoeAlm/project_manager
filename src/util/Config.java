package util;

/**
 * ملف الإعدادات
 * Configuration File
 */
public class Config {
    
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "5432";
    public static final String DB_NAME = "project_management_db";
    public static final String DB_USER = "moealm";
    public static final String DB_PASSWORD = "";
    
    public static final String APP_NAME = "نظام إدارة المشاريع";
    public static final String APP_VERSION = "1.0";
    
    public static final int DEFAULT_FONT_SIZE = 14;
    public static final String DEFAULT_FONT_NAME = "Arial";
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    public static final int SESSION_TIMEOUT_MINUTES = 60;
    
    public static final String EXPORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String EXPORT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String getDatabaseUrl() {
        return "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    }
}
