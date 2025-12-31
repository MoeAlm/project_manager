package controller;

import dao.ActivityLogDAO;
import dao.NotificationDAO;
import dao.ProjectDAO;
import dao.TaskDAO;
import model.ActivityLog;
import model.Notification;
import model.Task;
import util.SessionManager;
import view.TasksPanel;

import javax.swing.*;
import java.sql.SQLException;

/**
 * متحكم المهام
 * Task Controller
 */
public class TaskController {
    
    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private ActivityLogDAO activityLogDAO;
    private NotificationDAO notificationDAO;
    private TasksPanel tasksPanel;
    
    public TaskController(TasksPanel tasksPanel) {
        this.tasksPanel = tasksPanel;
        this.taskDAO = new TaskDAO();
        this.projectDAO = new ProjectDAO();
        this.activityLogDAO = new ActivityLogDAO();
        this.notificationDAO = new NotificationDAO();
    }
    
    /**
     * حذف مهمة
     */
    public void deleteTask(int taskId, int projectId) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean success = taskDAO.delete(taskId);
                if (success) {
                    // Update project completion
                    projectDAO.updateCompletionPercentage(projectId);
                }
                return success;
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Log activity
                        try {
                            ActivityLog log = new ActivityLog(
                                SessionManager.getInstance().getCurrentUserId(),
                                ActivityLog.ActionTypes.DELETE,
                                "حذف مهمة رقم: " + taskId,
                                ActivityLog.EntityTypes.TASK,
                                taskId
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        tasksPanel.showSuccess("تم حذف المهمة بنجاح");
                    } else {
                        tasksPanel.showError("فشل في حذف المهمة");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tasksPanel.showError("خطأ في حذف المهمة: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * تحديث نسبة إكمال المهمة
     */
    public void updateTaskProgress(int taskId, int projectId, double percentage) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean success = taskDAO.updateCompletion(taskId, percentage);
                if (success) {
                    // Update project completion
                    projectDAO.updateCompletionPercentage(projectId);
                }
                return success;
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Log activity
                        try {
                            String description = percentage >= 100 ? 
                                "إكمال المهمة رقم: " + taskId :
                                "تحديث تقدم المهمة رقم: " + taskId + " إلى " + percentage + "%";
                            
                            ActivityLog log = new ActivityLog(
                                SessionManager.getInstance().getCurrentUserId(),
                                percentage >= 100 ? ActivityLog.ActionTypes.COMPLETE : ActivityLog.ActionTypes.UPDATE,
                                description,
                                ActivityLog.EntityTypes.TASK,
                                taskId
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        String message = percentage >= 100 ? 
                            "تم إكمال المهمة بنجاح" : 
                            "تم تحديث تقدم المهمة بنجاح";
                        tasksPanel.showSuccess(message);
                    } else {
                        tasksPanel.showError("فشل في تحديث المهمة");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tasksPanel.showError("خطأ في تحديث المهمة: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * تحديث حالة المهمة
     */
    public void updateTaskStatus(int taskId, int projectId, int statusId) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean success = taskDAO.updateStatus(taskId, statusId);
                if (success) {
                    // Update project completion
                    projectDAO.updateCompletionPercentage(projectId);
                }
                return success;
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Log activity
                        try {
                            ActivityLog log = new ActivityLog(
                                SessionManager.getInstance().getCurrentUserId(),
                                ActivityLog.ActionTypes.UPDATE,
                                "تحديث حالة المهمة رقم: " + taskId,
                                ActivityLog.EntityTypes.TASK,
                                taskId
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        tasksPanel.showSuccess("تم تحديث حالة المهمة بنجاح");
                    } else {
                        tasksPanel.showError("فشل في تحديث حالة المهمة");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tasksPanel.showError("خطأ في تحديث المهمة: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * تعيين مهمة لمستخدم مع إرسال إشعار
     */
    public void assignTask(Task task, int assigneeId, String assigneeName) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                task.setAssignedTo(assigneeId);
                boolean success = taskDAO.update(task);
                
                if (success && assigneeId > 0) {
                    // Send notification to assignee
                    Notification notification = new Notification(
                        assigneeId,
                        "مهمة جديدة",
                        "تم تعيينك في المهمة: " + task.getTaskName(),
                        "TASK_ASSIGNED"
                    );
                    notification.setRelatedEntityType(ActivityLog.EntityTypes.TASK);
                    notification.setRelatedEntityId(task.getId());
                    notificationDAO.create(notification);
                }
                
                return success;
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        // Log activity
                        try {
                            ActivityLog log = new ActivityLog(
                                SessionManager.getInstance().getCurrentUserId(),
                                ActivityLog.ActionTypes.ASSIGN,
                                "تعيين المهمة: " + task.getTaskName() + " إلى " + assigneeName,
                                ActivityLog.EntityTypes.TASK,
                                task.getId()
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        tasksPanel.showSuccess("تم تعيين المهمة بنجاح");
                    } else {
                        tasksPanel.showError("فشل في تعيين المهمة");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tasksPanel.showError("خطأ في تعيين المهمة: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
