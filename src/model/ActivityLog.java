package model;

import java.sql.Timestamp;

/**
 * نموذج سجل الأنشطة
 * Activity Log Model
 */
public class ActivityLog {
    
    private int id;
    private int userId;
    private String userName;
    private String actionType;
    private String actionDescription;
    private String entityType;
    private int entityId;
    private Timestamp createdAt;
    
    public ActivityLog() {}
    
    public ActivityLog(int userId, String actionType, String actionDescription, 
                       String entityType, int entityId) {
        this.userId = userId;
        this.actionType = actionType;
        this.actionDescription = actionDescription;
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getActionDescription() {
        return actionDescription;
    }
    
    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public int getEntityId() {
        return entityId;
    }
    
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    
    public static class ActionTypes {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String ASSIGN = "ASSIGN";
        public static final String COMPLETE = "COMPLETE";
    }
    
    
    public static class EntityTypes {
        public static final String USER = "USER";
        public static final String PROJECT = "PROJECT";
        public static final String TASK = "TASK";
        public static final String MEMBER = "MEMBER";
    }
}
