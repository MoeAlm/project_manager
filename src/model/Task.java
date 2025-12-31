package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * نموذج المهمة
 * Task Model
 */
public class Task {
    
    private int id;
    private int projectId;
    private String projectName;
    private String taskName;
    private String description;
    private int assignedTo;
    private String assignedToName;
    private int priorityId;
    private String priorityName;
    private String priorityNameAr;
    private String priorityColor;
    private int statusId;
    private String statusName;
    private String statusNameAr;
    private String statusColor;
    private Date dueDate;
    private double completionPercentage;
    private int createdBy;
    private String createdByName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Task() {}
    
    public Task(int projectId, String taskName, String description, int assignedTo, 
                int priorityId, Date dueDate, int createdBy) {
        this.projectId = projectId;
        this.taskName = taskName;
        this.description = description;
        this.assignedTo = assignedTo;
        this.priorityId = priorityId;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.statusId = 1; // TODO
        this.completionPercentage = 0.0;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public String getAssignedToName() {
        return assignedToName;
    }
    
    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }
    
    public int getPriorityId() {
        return priorityId;
    }
    
    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }
    
    public String getPriorityName() {
        return priorityName;
    }
    
    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }
    
    public String getPriorityNameAr() {
        return priorityNameAr;
    }
    
    public void setPriorityNameAr(String priorityNameAr) {
        this.priorityNameAr = priorityNameAr;
    }
    
    public String getPriorityColor() {
        return priorityColor;
    }
    
    public void setPriorityColor(String priorityColor) {
        this.priorityColor = priorityColor;
    }
    
    public int getStatusId() {
        return statusId;
    }
    
    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
    
    public String getStatusName() {
        return statusName;
    }
    
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    public String getStatusNameAr() {
        return statusNameAr;
    }
    
    public void setStatusNameAr(String statusNameAr) {
        this.statusNameAr = statusNameAr;
    }
    
    public String getStatusColor() {
        return statusColor;
    }
    
    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isOverdue() {
        if (dueDate == null) return false;
        if (statusId == 4) return false; // COMPLETED
        return dueDate.before(new java.util.Date());
    }
    
    @Override
    public String toString() {
        return taskName;
    }
}
