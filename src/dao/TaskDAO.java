package dao;

import model.Task;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * طبقة الوصول لبيانات المهام
 * Task Data Access Object
 */
public class TaskDAO {
    
    /**
     * إنشاء مهمة جديدة
     */
    public int create(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (project_id, task_name, description, assigned_to, " +
                     "priority_id, status_id, due_date, completion_percentage, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, task.getProjectId());
            stmt.setString(2, task.getTaskName());
            stmt.setString(3, task.getDescription());
            if (task.getAssignedTo() > 0) {
                stmt.setInt(4, task.getAssignedTo());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, task.getPriorityId());
            stmt.setInt(6, task.getStatusId());
            stmt.setDate(7, task.getDueDate());
            stmt.setDouble(8, task.getCompletionPercentage());
            stmt.setInt(9, task.getCreatedBy());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * تحديث مهمة
     */
    public boolean update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET task_name = ?, description = ?, assigned_to = ?, " +
                     "priority_id = ?, status_id = ?, due_date = ?, completion_percentage = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getDescription());
            if (task.getAssignedTo() > 0) {
                stmt.setInt(3, task.getAssignedTo());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, task.getPriorityId());
            stmt.setInt(5, task.getStatusId());
            stmt.setDate(6, task.getDueDate());
            stmt.setDouble(7, task.getCompletionPercentage());
            stmt.setInt(8, task.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * حذف مهمة
     */
    public boolean delete(int taskId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * جلب مهمة بالمعرف
     */
    public Task findById(int id) throws SQLException {
        String sql = "SELECT t.*, p.project_name, " +
                     "tp.priority_name, tp.priority_name_ar, tp.color as priority_color, " +
                     "ts.status_name, ts.status_name_ar, ts.color as status_color, " +
                     "u1.full_name as assigned_to_name, u2.full_name as created_by_name " +
                     "FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.id " +
                     "LEFT JOIN task_priority tp ON t.priority_id = tp.id " +
                     "LEFT JOIN task_status ts ON t.status_id = ts.id " +
                     "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
                     "LEFT JOIN users u2 ON t.created_by = u2.id " +
                     "WHERE t.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTask(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * جلب مهام مشروع
     */
    public List<Task> findByProject(int projectId) throws SQLException {
        String sql = "SELECT t.*, p.project_name, " +
                     "tp.priority_name, tp.priority_name_ar, tp.color as priority_color, " +
                     "ts.status_name, ts.status_name_ar, ts.color as status_color, " +
                     "u1.full_name as assigned_to_name, u2.full_name as created_by_name " +
                     "FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.id " +
                     "LEFT JOIN task_priority tp ON t.priority_id = tp.id " +
                     "LEFT JOIN task_status ts ON t.status_id = ts.id " +
                     "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
                     "LEFT JOIN users u2 ON t.created_by = u2.id " +
                     "WHERE t.project_id = ? ORDER BY tp.priority_level DESC, t.due_date";
        
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        return tasks;
    }
    
    /**
     * جلب مهام مستخدم
     */
    public List<Task> findByAssignedUser(int userId) throws SQLException {
        String sql = "SELECT t.*, p.project_name, " +
                     "tp.priority_name, tp.priority_name_ar, tp.color as priority_color, " +
                     "ts.status_name, ts.status_name_ar, ts.color as status_color, " +
                     "u1.full_name as assigned_to_name, u2.full_name as created_by_name " +
                     "FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.id " +
                     "LEFT JOIN task_priority tp ON t.priority_id = tp.id " +
                     "LEFT JOIN task_status ts ON t.status_id = ts.id " +
                     "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
                     "LEFT JOIN users u2 ON t.created_by = u2.id " +
                     "WHERE t.assigned_to = ? ORDER BY tp.priority_level DESC, t.due_date";
        
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        return tasks;
    }
    
    /**
     * جلب المهام المتأخرة
     */
    public List<Task> findOverdue() throws SQLException {
        String sql = "SELECT t.*, p.project_name, " +
                     "tp.priority_name, tp.priority_name_ar, tp.color as priority_color, " +
                     "ts.status_name, ts.status_name_ar, ts.color as status_color, " +
                     "u1.full_name as assigned_to_name, u2.full_name as created_by_name " +
                     "FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.id " +
                     "LEFT JOIN task_priority tp ON t.priority_id = tp.id " +
                     "LEFT JOIN task_status ts ON t.status_id = ts.id " +
                     "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
                     "LEFT JOIN users u2 ON t.created_by = u2.id " +
                     "WHERE t.due_date < CURRENT_DATE AND t.status_id != 4 " +
                     "ORDER BY t.due_date";
        
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        }
        return tasks;
    }
    
    /**
     * جلب المهام المتأخرة لمستخدم
     */
    public List<Task> findOverdueByUser(int userId) throws SQLException {
        String sql = "SELECT t.*, p.project_name, " +
                     "tp.priority_name, tp.priority_name_ar, tp.color as priority_color, " +
                     "ts.status_name, ts.status_name_ar, ts.color as status_color, " +
                     "u1.full_name as assigned_to_name, u2.full_name as created_by_name " +
                     "FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.id " +
                     "LEFT JOIN task_priority tp ON t.priority_id = tp.id " +
                     "LEFT JOIN task_status ts ON t.status_id = ts.id " +
                     "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
                     "LEFT JOIN users u2 ON t.created_by = u2.id " +
                     "WHERE t.assigned_to = ? AND t.due_date < CURRENT_DATE AND t.status_id != 4 " +
                     "ORDER BY t.due_date";
        
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        return tasks;
    }
    
    /**
     * تحديث حالة المهمة
     */
    public boolean updateStatus(int taskId, int statusId) throws SQLException {
        String sql = "UPDATE tasks SET status_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, statusId);
            stmt.setInt(2, taskId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * تحديث نسبة الإكمال
     */
    public boolean updateCompletion(int taskId, double percentage) throws SQLException {
        String sql = "UPDATE tasks SET completion_percentage = ?, " +
                     "status_id = CASE WHEN ? >= 100 THEN 4 ELSE status_id END, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, percentage);
            stmt.setDouble(2, percentage);
            stmt.setInt(3, taskId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * البحث عن مهام
     */
    public List<Task> search(String keyword) throws SQLException {
        String sql = "SELECT t.*, p.project_name, " +
                     "tp.priority_name, tp.priority_name_ar, tp.color as priority_color, " +
                     "ts.status_name, ts.status_name_ar, ts.color as status_color, " +
                     "u1.full_name as assigned_to_name, u2.full_name as created_by_name " +
                     "FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.id " +
                     "LEFT JOIN task_priority tp ON t.priority_id = tp.id " +
                     "LEFT JOIN task_status ts ON t.status_id = ts.id " +
                     "LEFT JOIN users u1 ON t.assigned_to = u1.id " +
                     "LEFT JOIN users u2 ON t.created_by = u2.id " +
                     "WHERE t.task_name ILIKE ? OR t.description ILIKE ? " +
                     "ORDER BY t.created_at DESC";
        
        List<Task> tasks = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        return tasks;
    }
    
    /**
     * جلب عدد المهام حسب الحالة للمشروع
     */
    public int countByStatusForProject(int projectId, int statusId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE project_id = ? AND status_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            stmt.setInt(2, statusId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * جلب عدد المهام المكتملة للمستخدم
     */
    public int countCompletedByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE assigned_to = ? AND status_id = 4";
        
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
     * جلب إجمالي مهام المستخدم
     */
    public int countTotalByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE assigned_to = ?";
        
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
     * جلب جميع حالات المهام
     */
    public List<Object[]> getAllStatuses() throws SQLException {
        String sql = "SELECT id, status_name, status_name_ar, color FROM task_status ORDER BY id";
        
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
     * جلب جميع الأولويات
     */
    public List<Object[]> getAllPriorities() throws SQLException {
        String sql = "SELECT id, priority_name, priority_name_ar, priority_level, color FROM task_priority ORDER BY priority_level";
        
        List<Object[]> priorities = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                priorities.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("priority_name"),
                    rs.getString("priority_name_ar"),
                    rs.getInt("priority_level"),
                    rs.getString("color")
                });
            }
        }
        return priorities;
    }
    
    /**
     * تحويل ResultSet إلى كائن Task
     */
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setProjectId(rs.getInt("project_id"));
        task.setProjectName(rs.getString("project_name"));
        task.setTaskName(rs.getString("task_name"));
        task.setDescription(rs.getString("description"));
        task.setAssignedTo(rs.getInt("assigned_to"));
        task.setAssignedToName(rs.getString("assigned_to_name"));
        task.setPriorityId(rs.getInt("priority_id"));
        task.setPriorityName(rs.getString("priority_name"));
        task.setPriorityNameAr(rs.getString("priority_name_ar"));
        task.setPriorityColor(rs.getString("priority_color"));
        task.setStatusId(rs.getInt("status_id"));
        task.setStatusName(rs.getString("status_name"));
        task.setStatusNameAr(rs.getString("status_name_ar"));
        task.setStatusColor(rs.getString("status_color"));
        task.setDueDate(rs.getDate("due_date"));
        task.setCompletionPercentage(rs.getDouble("completion_percentage"));
        task.setCreatedBy(rs.getInt("created_by"));
        task.setCreatedByName(rs.getString("created_by_name"));
        task.setCreatedAt(rs.getTimestamp("created_at"));
        task.setUpdatedAt(rs.getTimestamp("updated_at"));
        return task;
    }
}
