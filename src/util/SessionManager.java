package util;

import model.User;

/**
 * فئة إدارة جلسة المستخدم
 * User Session Manager
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isProjectManager() {
        return currentUser != null && currentUser.getRoleId() == 1;
    }
    
    public boolean isTeamMember() {
        return currentUser != null && currentUser.getRoleId() == 2;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : "";
    }
}
