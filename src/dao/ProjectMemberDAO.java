package dao;

import model.ProjectMember;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * طبقة الوصول لبيانات أعضاء المشروع
 * Project Member Data Access Object
 */
public class ProjectMemberDAO {
    
    /**
     * إضافة عضو للمشروع
     */
    public int addMember(ProjectMember member) throws SQLException {
        String sql = "INSERT INTO project_members (project_id, user_id, project_role_id) " +
                     "VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, member.getProjectId());
            stmt.setInt(2, member.getUserId());
            stmt.setInt(3, member.getProjectRoleId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * تحديث دور العضو
     */
    public boolean updateRole(int memberId, int projectRoleId) throws SQLException {
        String sql = "UPDATE project_members SET project_role_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectRoleId);
            stmt.setInt(2, memberId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * إزالة عضو من المشروع
     */
    public boolean removeMember(int memberId) throws SQLException {
        String sql = "DELETE FROM project_members WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, memberId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * إزالة عضو من المشروع بواسطة معرف المستخدم والمشروع
     */
    public boolean removeMember(int projectId, int userId) throws SQLException {
        String sql = "DELETE FROM project_members WHERE project_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * جلب أعضاء مشروع
     */
    public List<ProjectMember> findByProject(int projectId) throws SQLException {
        String sql = "SELECT pm.*, p.project_name, u.username, u.full_name as user_full_name, " +
                     "pr.role_name as project_role_name, pr.role_name_ar as project_role_name_ar " +
                     "FROM project_members pm " +
                     "LEFT JOIN projects p ON pm.project_id = p.id " +
                     "LEFT JOIN users u ON pm.user_id = u.id " +
                     "LEFT JOIN project_roles pr ON pm.project_role_id = pr.id " +
                     "WHERE pm.project_id = ? ORDER BY pm.joined_at";
        
        List<ProjectMember> members = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToProjectMember(rs));
                }
            }
        }
        return members;
    }
    
    /**
     * جلب مشاريع مستخدم
     */
    public List<ProjectMember> findByUser(int userId) throws SQLException {
        String sql = "SELECT pm.*, p.project_name, u.username, u.full_name as user_full_name, " +
                     "pr.role_name as project_role_name, pr.role_name_ar as project_role_name_ar " +
                     "FROM project_members pm " +
                     "LEFT JOIN projects p ON pm.project_id = p.id " +
                     "LEFT JOIN users u ON pm.user_id = u.id " +
                     "LEFT JOIN project_roles pr ON pm.project_role_id = pr.id " +
                     "WHERE pm.user_id = ? ORDER BY pm.joined_at DESC";
        
        List<ProjectMember> members = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToProjectMember(rs));
                }
            }
        }
        return members;
    }
    
    /**
     * التحقق من وجود عضو في مشروع
     */
    public boolean isMember(int projectId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM project_members WHERE project_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * جلب عدد أعضاء المشروع
     */
    public int countByProject(int projectId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM project_members WHERE project_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * جلب أعضاء مشروع مع إحصائيات الأداء
     */
    public List<ProjectMember> findByProjectWithPerformance(int projectId) throws SQLException {
        String sql = "SELECT pm.*, p.project_name, u.username, u.full_name as user_full_name, " +
                     "pr.role_name as project_role_name, pr.role_name_ar as project_role_name_ar, " +
                     "(SELECT COUNT(*) FROM tasks t WHERE t.assigned_to = pm.user_id AND t.project_id = pm.project_id) as total_tasks, " +
                     "(SELECT COUNT(*) FROM tasks t WHERE t.assigned_to = pm.user_id AND t.project_id = pm.project_id AND t.status_id = 4) as completed_tasks " +
                     "FROM project_members pm " +
                     "LEFT JOIN projects p ON pm.project_id = p.id " +
                     "LEFT JOIN users u ON pm.user_id = u.id " +
                     "LEFT JOIN project_roles pr ON pm.project_role_id = pr.id " +
                     "WHERE pm.project_id = ? ORDER BY pm.joined_at";
        
        List<ProjectMember> members = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProjectMember member = mapResultSetToProjectMember(rs);
                    member.setTotalTasks(rs.getInt("total_tasks"));
                    member.setCompletedTasks(rs.getInt("completed_tasks"));
                    if (member.getTotalTasks() > 0) {
                        member.setCompletionRate((double) member.getCompletedTasks() / member.getTotalTasks() * 100);
                    }
                    members.add(member);
                }
            }
        }
        return members;
    }
    
    /**
     * جلب جميع أدوار المشروع
     */
    public List<Object[]> getAllProjectRoles() throws SQLException {
        String sql = "SELECT id, role_name, role_name_ar FROM project_roles ORDER BY id";
        
        List<Object[]> roles = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                roles.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("role_name"),
                    rs.getString("role_name_ar")
                });
            }
        }
        return roles;
    }
    
    /**
     * تحويل ResultSet إلى كائن ProjectMember
     */
    private ProjectMember mapResultSetToProjectMember(ResultSet rs) throws SQLException {
        ProjectMember member = new ProjectMember();
        member.setId(rs.getInt("id"));
        member.setProjectId(rs.getInt("project_id"));
        member.setProjectName(rs.getString("project_name"));
        member.setUserId(rs.getInt("user_id"));
        member.setUserName(rs.getString("username"));
        member.setUserFullName(rs.getString("user_full_name"));
        member.setProjectRoleId(rs.getInt("project_role_id"));
        member.setProjectRoleName(rs.getString("project_role_name"));
        member.setProjectRoleNameAr(rs.getString("project_role_name_ar"));
        member.setJoinedAt(rs.getTimestamp("joined_at"));
        return member;
    }
}
