package model;

import java.sql.Timestamp;

/**
 * نموذج عضو المشروع
 * Project Member Model
 */
public class ProjectMember {
    
    private int id;
    private int projectId;
    private String projectName;
    private int userId;
    private String userName;
    private String userFullName;
    private int projectRoleId;
    private String projectRoleName;
    private String projectRoleNameAr;
    private Timestamp joinedAt;
    
    // Performance metrics
    private int totalTasks;
    private int completedTasks;
    private double completionRate;
    
    public ProjectMember() {}
    
    public ProjectMember(int projectId, int userId, int projectRoleId) {
        this.projectId = projectId;
        this.userId = userId;
        this.projectRoleId = projectRoleId;
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
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserFullName() {
        return userFullName;
    }
    
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
    
    public int getProjectRoleId() {
        return projectRoleId;
    }
    
    public void setProjectRoleId(int projectRoleId) {
        this.projectRoleId = projectRoleId;
    }
    
    public String getProjectRoleName() {
        return projectRoleName;
    }
    
    public void setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
    }
    
    public String getProjectRoleNameAr() {
        return projectRoleNameAr;
    }
    
    public void setProjectRoleNameAr(String projectRoleNameAr) {
        this.projectRoleNameAr = projectRoleNameAr;
    }
    
    public Timestamp getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public int getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    public double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
    
    @Override
    public String toString() {
        return userFullName + " - " + projectRoleNameAr;
    }
}
