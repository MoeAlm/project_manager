package controller;

import dao.ActivityLogDAO;
import dao.ProjectDAO;
import model.ActivityLog;
import model.Project;
import util.SessionManager;
import view.ProjectsPanel;

import javax.swing.*;
import java.sql.SQLException;

/**
 * متحكم المشاريع
 * Project Controller
 */
public class ProjectController {
    
    private ProjectDAO projectDAO;
    private ActivityLogDAO activityLogDAO;
    private ProjectsPanel projectsPanel;
    
    public ProjectController(ProjectsPanel projectsPanel) {
        this.projectsPanel = projectsPanel;
        this.projectDAO = new ProjectDAO();
        this.activityLogDAO = new ActivityLogDAO();
    }
    
    /**
     * حذف مشروع
     */
    public void deleteProject(int projectId) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return projectDAO.delete(projectId);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        try {
                            ActivityLog log = new ActivityLog(
                                SessionManager.getInstance().getCurrentUserId(),
                                ActivityLog.ActionTypes.DELETE,
                                "حذف مشروع رقم: " + projectId,
                                ActivityLog.EntityTypes.PROJECT,
                                projectId
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        projectsPanel.showSuccess("تم حذف المشروع بنجاح");
                    } else {
                        projectsPanel.showError("فشل في حذف المشروع");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    projectsPanel.showError("خطأ في حذف المشروع: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * تحديث حالة المشروع
     */
    public void updateProjectStatus(Project project, int newStatusId) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                project.setStatusId(newStatusId);
                return projectDAO.update(project);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        try {
                            ActivityLog log = new ActivityLog(
                                SessionManager.getInstance().getCurrentUserId(),
                                ActivityLog.ActionTypes.UPDATE,
                                "تحديث حالة المشروع: " + project.getProjectName(),
                                ActivityLog.EntityTypes.PROJECT,
                                project.getId()
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        projectsPanel.showSuccess("تم تحديث حالة المشروع بنجاح");
                    } else {
                        projectsPanel.showError("فشل في تحديث حالة المشروع");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    projectsPanel.showError("خطأ في تحديث المشروع: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
