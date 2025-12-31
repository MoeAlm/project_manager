package dao;

import model.Project;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * طبقة الوصول لبيانات المشاريع
 * Project Data Access Object
 */
public class ProjectDAO {
    
    /**
     * إنشاء مشروع جديد
     */
    public int create(Project project) throws SQLException {
        String sql = "INSERT INTO projects (project_name, description, start_date, end_date, " +
                     "status_id, manager_id, completion_percentage) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, project.getProjectName());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getStatusId());
            stmt.setInt(6, project.getManagerId());
            stmt.setDouble(7, project.getCompletionPercentage());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * تحديث مشروع
     */
    public boolean update(Project project) throws SQLException {
        String sql = "UPDATE projects SET project_name = ?, description = ?, start_date = ?, " +
                     "end_date = ?, status_id = ?, manager_id = ?, completion_percentage = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, project.getProjectName());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getStatusId());
            stmt.setInt(6, project.getManagerId());
            stmt.setDouble(7, project.getCompletionPercentage());
            stmt.setInt(8, project.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * حذف مشروع
     */
    public boolean delete(int projectId) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * جلب مشروع بالمعرف
     */
    public Project findById(int id) throws SQLException {
        String sql = "SELECT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProject(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * جلب جميع المشاريع
     */
    public List<Project> findAll() throws SQLException {
        String sql = "SELECT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name, " +
                     "(SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.id) as task_count, " +
                     "(SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.id) as member_count " +
                     "FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id " +
                     "ORDER BY p.created_at DESC";
        
        List<Project> projects = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Project project = mapResultSetToProject(rs);
                project.setTaskCount(rs.getInt("task_count"));
                project.setMemberCount(rs.getInt("member_count"));
                projects.add(project);
            }
        }
        return projects;
    }
    
    /**
     * جلب مشاريع مدير معين
     */
    public List<Project> findByManager(int managerId) throws SQLException {
        String sql = "SELECT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name, " +
                     "(SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.id) as task_count, " +
                     "(SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.id) as member_count " +
                     "FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id " +
                     "WHERE p.manager_id = ? ORDER BY p.created_at DESC";
        
        List<Project> projects = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, managerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = mapResultSetToProject(rs);
                    project.setTaskCount(rs.getInt("task_count"));
                    project.setMemberCount(rs.getInt("member_count"));
                    projects.add(project);
                }
            }
        }
        return projects;
    }
    
    /**
     * جلب مشاريع عضو معين
     */
    public List<Project> findByMember(int userId) throws SQLException {
        String sql = "SELECT DISTINCT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name, " +
                     "(SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.id) as task_count, " +
                     "(SELECT COUNT(*) FROM project_members pm2 WHERE pm2.project_id = p.id) as member_count " +
                     "FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id " +
                     "INNER JOIN project_members pm ON p.id = pm.project_id " +
                     "WHERE pm.user_id = ? OR p.manager_id = ? ORDER BY p.created_at DESC";
        
        List<Project> projects = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = mapResultSetToProject(rs);
                    project.setTaskCount(rs.getInt("task_count"));
                    project.setMemberCount(rs.getInt("member_count"));
                    projects.add(project);
                }
            }
        }
        return projects;
    }
    
    /**
     * جلب مشاريع حسب الحالة
     */
    public List<Project> findByStatus(int statusId) throws SQLException {
        String sql = "SELECT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id " +
                     "WHERE p.status_id = ? ORDER BY p.created_at DESC";
        
        List<Project> projects = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, statusId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }
        }
        return projects;
    }
    
    /**
     * البحث عن مشاريع
     */
    public List<Project> search(String keyword) throws SQLException {
        String sql = "SELECT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id " +
                     "WHERE p.project_name ILIKE ? OR p.description ILIKE ? " +
                     "ORDER BY p.created_at DESC";
        
        List<Project> projects = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }
        }
        return projects;
    }
    
    /**
     * جلب المشاريع المكتملة
     */
    public List<Project> findCompleted() throws SQLException {
        return findByStatus(4); // COMPLETED status
    }
    
    /**
     * جلب المشاريع المتأخرة
     */
    public List<Project> findOverdue() throws SQLException {
        String sql = "SELECT p.*, ps.status_name, ps.status_name_ar, ps.color as status_color, " +
                     "u.full_name as manager_name FROM projects p " +
                     "LEFT JOIN project_status ps ON p.status_id = ps.id " +
                     "LEFT JOIN users u ON p.manager_id = u.id " +
                     "WHERE p.end_date < CURRENT_DATE AND p.status_id NOT IN (4, 5) " +
                     "ORDER BY p.end_date";
        
        List<Project> projects = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        }
        return projects;
    }
    
    /**
     * تحديث نسبة إكمال المشروع
     */
    public boolean updateCompletionPercentage(int projectId) throws SQLException {
        String sql = "UPDATE projects SET completion_percentage = " +
                     "(SELECT COALESCE(AVG(completion_percentage), 0) FROM tasks WHERE project_id = ?), " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            stmt.setInt(2, projectId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * جلب عدد المشاريع حسب الحالة
     */
    public int countByStatus(int statusId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM projects WHERE status_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, statusId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * جلب جميع حالات المشروع
     */
    public List<Object[]> getAllStatuses() throws SQLException {
        String sql = "SELECT id, status_name, status_name_ar, color FROM project_status ORDER BY id";
        
        List<Object[]> statuses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                statuses.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("status_name"),
                    rs.getString("status_name_ar"),
                    rs.getString("color")
                });
            }
        }
        return statuses;
    }
    
    /**
     * تحويل ResultSet إلى كائن Project
     */
    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setProjectName(rs.getString("project_name"));
        project.setDescription(rs.getString("description"));
        project.setStartDate(rs.getDate("start_date"));
        project.setEndDate(rs.getDate("end_date"));
        project.setStatusId(rs.getInt("status_id"));
        project.setStatusName(rs.getString("status_name"));
        project.setStatusNameAr(rs.getString("status_name_ar"));
        project.setStatusColor(rs.getString("status_color"));
        project.setManagerId(rs.getInt("manager_id"));
        project.setManagerName(rs.getString("manager_name"));
        project.setCompletionPercentage(rs.getDouble("completion_percentage"));
        project.setCreatedAt(rs.getTimestamp("created_at"));
        project.setUpdatedAt(rs.getTimestamp("updated_at"));
        return project;
    }
}
