package dao;

import model.ActivityLog;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * طبقة الوصول لبيانات سجل الأنشطة
 * Activity Log Data Access Object
 */
public class ActivityLogDAO {
    
    /**
     * تسجيل نشاط جديد
     */
    public int log(ActivityLog activityLog) throws SQLException {
        String sql = "INSERT INTO activity_logs (user_id, action_type, action_description, " +
                     "entity_type, entity_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityLog.getUserId());
            stmt.setString(2, activityLog.getActionType());
            stmt.setString(3, activityLog.getActionDescription());
            stmt.setString(4, activityLog.getEntityType());
            stmt.setInt(5, activityLog.getEntityId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * جلب جميع الأنشطة
     */
    public List<ActivityLog> findAll() throws SQLException {
        String sql = "SELECT al.*, u.full_name as user_name FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.id " +
                     "ORDER BY al.created_at DESC LIMIT 100";
        
        List<ActivityLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                logs.add(mapResultSetToActivityLog(rs));
            }
        }
        return logs;
    }
    
    /**
     * جلب أنشطة مستخدم
     */
    public List<ActivityLog> findByUser(int userId) throws SQLException {
        String sql = "SELECT al.*, u.full_name as user_name FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.id " +
                     "WHERE al.user_id = ? ORDER BY al.created_at DESC LIMIT 50";
        
        List<ActivityLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToActivityLog(rs));
                }
            }
        }
        return logs;
    }
    
    /**
     * جلب أنشطة كيان محدد
     */
    public List<ActivityLog> findByEntity(String entityType, int entityId) throws SQLException {
        String sql = "SELECT al.*, u.full_name as user_name FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.id " +
                     "WHERE al.entity_type = ? AND al.entity_id = ? " +
                     "ORDER BY al.created_at DESC";
        
        List<ActivityLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, entityType);
            stmt.setInt(2, entityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToActivityLog(rs));
                }
            }
        }
        return logs;
    }
    
    /**
     * جلب الأنشطة الأخيرة
     */
    public List<ActivityLog> findRecent(int limit) throws SQLException {
        String sql = "SELECT al.*, u.full_name as user_name FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.id " +
                     "ORDER BY al.created_at DESC LIMIT ?";
        
        List<ActivityLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToActivityLog(rs));
                }
            }
        }
        return logs;
    }
    
    /**
     * حذف السجلات القديمة (أكثر من 90 يوم)
     */
    public int deleteOldLogs() throws SQLException {
        String sql = "DELETE FROM activity_logs WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '90 days'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return stmt.executeUpdate();
        }
    }
    
    /**
     * تحويل ResultSet إلى كائن ActivityLog
     */
    private ActivityLog mapResultSetToActivityLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setUserName(rs.getString("user_name"));
        log.setActionType(rs.getString("action_type"));
        log.setActionDescription(rs.getString("action_description"));
        log.setEntityType(rs.getString("entity_type"));
        log.setEntityId(rs.getInt("entity_id"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
