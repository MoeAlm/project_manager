import util.ArabicUIHelper;
import util.DatabaseConnection;
import view.LoginView;

import javax.swing.*;

/**
 * نظام إدارة المشاريع - النقطة الرئيسية للتطبيق
 * Project Management System - Main Entry Point
 */
public class Main {
    public static void main(String[] args) {
        ArabicUIHelper.setArabicLookAndFeel();
        
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                "فشل الاتصال بقاعدة البيانات!\n" +
                "تأكد من تشغيل خادم PostgreSQL وإنشاء قاعدة البيانات.\n\n" +
                "Database connection failed!\n" +
                "Make sure PostgreSQL server is running and database is created.",
                "خطأ في الاتصال - Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "خطأ في تشغيل التطبيق: " + e.getMessage(),
                    "خطأ",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}