package controller;

import dao.ActivityLogDAO;
import dao.UserDAO;
import model.ActivityLog;
import model.User;
import util.SessionManager;
import view.LoginView;
import view.RegisterView;

import javax.swing.*;
import java.sql.SQLException;

/**
 * متحكم المصادقة
 * Authentication Controller
 */
public class AuthController {
    
    private UserDAO userDAO;
    private ActivityLogDAO activityLogDAO;
    private LoginView loginView;
    private RegisterView registerView;
    
    public AuthController(LoginView loginView) {
        this.loginView = loginView;
        this.userDAO = new UserDAO();
        this.activityLogDAO = new ActivityLogDAO();
    }
    
    public AuthController(RegisterView registerView) {
        this.registerView = registerView;
        this.userDAO = new UserDAO();
        this.activityLogDAO = new ActivityLogDAO();
    }
    
    /**
     * تسجيل الدخول
     */
    public void login(String username, String password) {
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        SessionManager.getInstance().setCurrentUser(user);
                        
                        
                        try {
                            ActivityLog log = new ActivityLog(
                                user.getId(),
                                ActivityLog.ActionTypes.LOGIN,
                                "تسجيل دخول المستخدم: " + user.getFullName(),
                                ActivityLog.EntityTypes.USER,
                                user.getId()
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        loginView.showLoginSuccess();
                    } else {
                        loginView.showLoginError("اسم المستخدم أو كلمة المرور غير صحيحة");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loginView.showLoginError("حدث خطأ أثناء تسجيل الدخول:\n" + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * تسجيل مستخدم جديد
     */
    public void register(String username, String password, String fullName, 
                         String email, String phone, int roleId) {
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                
                if (userDAO.usernameExists(username)) {
                    throw new Exception("اسم المستخدم موجود مسبقاً");
                }
                
                
                if (email != null && !email.isEmpty() && userDAO.emailExists(email)) {
                    throw new Exception("البريد الإلكتروني مستخدم مسبقاً");
                }
                
                User user = new User(username, password, fullName, email, phone, roleId);
                return userDAO.create(user);
            }
            
            @Override
            protected void done() {
                try {
                    int userId = get();
                    if (userId > 0) {
                        
                        try {
                            ActivityLog log = new ActivityLog(
                                userId,
                                ActivityLog.ActionTypes.CREATE,
                                "تسجيل مستخدم جديد: " + fullName,
                                ActivityLog.EntityTypes.USER,
                                userId
                            );
                            activityLogDAO.log(log);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                        registerView.showRegisterSuccess();
                    } else {
                        registerView.showRegisterError("فشل في إنشاء الحساب");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    registerView.showRegisterError(e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * تسجيل الخروج
     */
    public static void logout() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            try {
                ActivityLogDAO logDAO = new ActivityLogDAO();
                ActivityLog log = new ActivityLog(
                    currentUser.getId(),
                    ActivityLog.ActionTypes.LOGOUT,
                    "تسجيل خروج المستخدم: " + currentUser.getFullName(),
                    ActivityLog.EntityTypes.USER,
                    currentUser.getId()
                );
                logDAO.log(log);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        SessionManager.getInstance().logout();
    }
}
