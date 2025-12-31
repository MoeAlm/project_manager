package dao;

import model.Notification;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * طبقة الوصول لبيانات الإشعارات
 * Notification Data Access Object
 */
public class NotificationDAO {
    
    /**
     * إنشاء إشعار جديد
     */
    public int create(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, notification_type, " +
                     "related_entity_type, related_entity_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getNotificationType());
            stmt.setString(5, notification.getRelatedEntityType());
            stmt.setInt(6, notification.getRelatedEntityId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * تحديث حالة القراءة
     */
    public boolean markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * تحديث جميع إشعارات المستخدم كمقروءة
     */
    public boolean markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * حذف إشعار
     */
    public boolean delete(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * جلب إشعارات مستخدم
     */
    public List<Notification> findByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }
    
    /**
     * جلب إشعارات غير مقروءة لمستخدم
     */
    public List<Notification> findUnreadByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }
    
    /**
     * عدد الإشعارات غير المقروءة
     */
    public int countUnread(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * حذف الإشعارات القديمة (أكثر من 30 يوم)
     */
    public int deleteOldNotifications() throws SQLException {
        String sql = "DELETE FROM notifications WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '30 days'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return stmt.executeUpdate();
        }
    }
    
    /**
     * تحويل ResultSet إلى كائن Notification
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setNotificationType(rs.getString("notification_type"));
        notification.setRelatedEntityType(rs.getString("related_entity_type"));
        notification.setRelatedEntityId(rs.getInt("related_entity_id"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}
