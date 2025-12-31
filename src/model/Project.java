package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * نموذج المشروع
 * Project Model
 */
public class Project {
    
    private int id;
    private String projectName;
    private String description;
    private Date startDate;
    private Date endDate;
    private int statusId;
    private String statusName;
    private String statusNameAr;
    private String statusColor;
    private int managerId;
    private String managerName;
    private double completionPercentage;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private int taskCount;
    private int memberCount;
    
    public Project() {}
    
    public Project(String projectName, String description, Date startDate, Date endDate, int managerId) {
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.managerId = managerId;
        this.statusId = 1; // NOT_STARTED
        this.completionPercentage = 0.0;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
    
    public int getManagerId() {
        return managerId;
    }
    
    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
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
    
    public int getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
    
    public int getMemberCount() {
        return memberCount;
    }
    
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
    
    @Override
    public String toString() {
        return projectName;
    }
}
