package view;

import controller.AuthController;
import util.ArabicUIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * واجهة تسجيل الدخول
 * Login View
 */
public class LoginView extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthController authController;
    
    public LoginView() {
        authController = new AuthController(this);
        initComponents();
    }
    
    private void initComponents() {
        setTitle("نظام إدارة المشاريع - تسجيل الدخول");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        ArabicUIHelper.applyRTL(this);
        
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        
        JPanel titlePanel = ArabicUIHelper.createPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("نظام إدارة المشاريع");
        titleLabel.setFont(ArabicUIHelper.getArabicFontBold(24));
        titleLabel.setForeground(new Color(33, 150, 243));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = ArabicUIHelper.createLabel("تسجيل الدخول إلى حسابك", 14);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        
        JPanel formPanel = ArabicUIHelper.createPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel usernameLabel = ArabicUIHelper.createLabel("اسم المستخدم:");
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = ArabicUIHelper.createTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(usernameField, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passwordLabel = ArabicUIHelper.createLabel("كلمة المرور:");
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = ArabicUIHelper.createPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(passwordField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        loginButton = ArabicUIHelper.createPrimaryButton("تسجيل الدخول");
        loginButton.setPreferredSize(new Dimension(140, 40));
        loginButton.addActionListener(e -> handleLogin());
        
        registerButton = ArabicUIHelper.createSecondaryButton("تسجيل جديد");
        registerButton.setPreferredSize(new Dimension(140, 40));
        registerButton.addActionListener(e -> openRegisterView());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        
        
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            ArabicUIHelper.showError(this, "الرجاء إدخال اسم المستخدم", "خطأ");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            ArabicUIHelper.showError(this, "الرجاء إدخال كلمة المرور", "خطأ");
            passwordField.requestFocus();
            return;
        }
        
        authController.login(username, password);
    }
    
    private void openRegisterView() {
        RegisterView registerView = new RegisterView(this);
        registerView.setVisible(true);
        this.setVisible(false);
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }
    
    public void showLoginSuccess() {
        dispose();
        MainDashboard dashboard = new MainDashboard();
        dashboard.setVisible(true);
    }
    
    public void showLoginError(String message) {
        ArabicUIHelper.showError(this, message, "خطأ في تسجيل الدخول");
        passwordField.setText("");
        passwordField.requestFocus();
    }
}
