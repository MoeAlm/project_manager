package dao;

import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * طبقة الوصول لبيانات المستخدمين
 * User Data Access Object
 */
public class UserDAO {
    
    /**
     * التحقق من تسجيل الدخول
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id " +
                     "WHERE u.username = ? AND u.password = ? AND u.is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * إنشاء مستخدم جديد
     */
    public int create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role_id, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setInt(6, user.getRoleId());
            stmt.setBoolean(7, user.isActive());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * تحديث بيانات مستخدم
     */
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, role_id = ?, " +
                     "is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getRoleId());
            stmt.setBoolean(5, user.isActive());
            stmt.setInt(6, user.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * تحديث كلمة المرور
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * حذف مستخدم (تعطيل)
     */
    public boolean delete(int userId) throws SQLException {
        String sql = "UPDATE users SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * جلب مستخدم بالمعرف
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * جلب مستخدم باسم المستخدم
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id WHERE u.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * جلب جميع المستخدمين
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id ORDER BY u.full_name";
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    /**
     * جلب المستخدمين النشطين
     */
    public List<User> findAllActive() throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id WHERE u.is_active = TRUE ORDER BY u.full_name";
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    /**
     * جلب المستخدمين حسب الدور
     */
    public List<User> findByRole(int roleId) throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id " +
                     "WHERE u.role_id = ? AND u.is_active = TRUE ORDER BY u.full_name";
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * البحث عن مستخدمين
     */
    public List<User> search(String keyword) throws SQLException {
        String sql = "SELECT u.*, r.role_name, r.role_name_ar FROM users u " +
                     "LEFT JOIN roles r ON u.role_id = r.id " +
                     "WHERE (u.full_name ILIKE ? OR u.username ILIKE ? OR u.email ILIKE ?) " +
                     "AND u.is_active = TRUE ORDER BY u.full_name";
        
        List<User> users = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * التحقق من وجود اسم مستخدم
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * التحقق من وجود بريد إلكتروني
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * تحويل ResultSet إلى كائن User
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        user.setRoleName(rs.getString("role_name"));
        user.setRoleNameAr(rs.getString("role_name_ar"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
