package view;

import dao.NotificationDAO;
import model.Notification;
import util.ArabicUIHelper;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * حوار الإشعارات
 * Notifications Dialog
 */
public class NotificationsDialog extends JDialog {
    
    private JTable notificationsTable;
    private DefaultTableModel tableModel;
    private NotificationDAO notificationDAO;
    private List<Notification> notifications;
    
    public NotificationsDialog(JFrame parent) {
        super(parent, "الإشعارات", true);
        this.notificationDAO = new NotificationDAO();
        initComponents();
        loadNotifications();
    }
    
    private void initComponents() {
        setSize(600, 450);
        setLocationRelativeTo(getParent());
        ArabicUIHelper.applyRTL(this);
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("الإشعارات");
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        
        String[] columns = {"#", "العنوان", "الرسالة", "التاريخ", "الحالة"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        notificationsTable = new JTable(tableModel);
        ArabicUIHelper.applyRTLToTable(notificationsTable);
        notificationsTable.setRowHeight(35);
        
        notificationsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        notificationsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        notificationsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        notificationsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        notificationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(notificationsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        JButton markReadBtn = ArabicUIHelper.createPrimaryButton("تحديد كمقروء");
        markReadBtn.addActionListener(e -> markAsRead());
        buttonPanel.add(markReadBtn);
        
        JButton markAllReadBtn = ArabicUIHelper.createSecondaryButton("تحديد الكل كمقروء");
        markAllReadBtn.addActionListener(e -> markAllAsRead());
        buttonPanel.add(markAllReadBtn);
        
        JButton deleteBtn = ArabicUIHelper.createDangerButton("حذف");
        deleteBtn.addActionListener(e -> deleteNotification());
        buttonPanel.add(deleteBtn);
        
        JButton closeBtn = ArabicUIHelper.createSecondaryButton("إغلاق");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadNotifications() {
        try {
            int userId = SessionManager.getInstance().getCurrentUserId();
            notifications = notificationDAO.findByUser(userId);
            updateTable();
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تحميل الإشعارات", "خطأ");
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        int index = 1;
        for (Notification n : notifications) {
            Object[] row = {
                index++,
                n.getTitle(),
                n.getMessage(),
                dateFormat.format(n.getCreatedAt()),
                n.isRead() ? "مقروء" : "جديد"
            };
            tableModel.addRow(row);
        }
    }
    
    private void markAsRead() {
        int selectedRow = notificationsTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار إشعار", "تنبيه");
            return;
        }
        
        try {
            Notification n = notifications.get(selectedRow);
            notificationDAO.markAsRead(n.getId());
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تحديث الإشعار", "خطأ");
        }
    }
    
    private void markAllAsRead() {
        try {
            int userId = SessionManager.getInstance().getCurrentUserId();
            notificationDAO.markAllAsRead(userId);
            loadNotifications();
            ArabicUIHelper.showInfo(this, "تم تحديث جميع الإشعارات", "نجاح");
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تحديث الإشعارات", "خطأ");
        }
    }
    
    private void deleteNotification() {
        int selectedRow = notificationsTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار إشعار", "تنبيه");
            return;
        }
        
        try {
            Notification n = notifications.get(selectedRow);
            notificationDAO.delete(n.getId());
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في حذف الإشعار", "خطأ");
        }
    }
}
