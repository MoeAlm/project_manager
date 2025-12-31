package view;

import controller.AuthController;
import util.ArabicUIHelper;
import util.ValidationHelper;

import javax.swing.*;
import java.awt.*;

/**
 * واجهة تسجيل مستخدم جديد
 * Registration View
 */
public class RegisterView extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton backButton;
    private AuthController authController;
    private LoginView loginView;
    
    public RegisterView(LoginView loginView) {
        this.loginView = loginView;
        authController = new AuthController(this);
        initComponents();
    }
    
    private void initComponents() {
        setTitle("نظام إدارة المشاريع - تسجيل جديد");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        ArabicUIHelper.applyRTL(this);
        
        // Main panel
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Title panel
        JPanel titlePanel = ArabicUIHelper.createPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("إنشاء حساب جديد");
        titleLabel.setFont(ArabicUIHelper.getArabicFontBold(22));
        titleLabel.setForeground(new Color(33, 150, 243));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = ArabicUIHelper.createPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        int row = 0;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("اسم المستخدم: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = ArabicUIHelper.createTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 32));
        formPanel.add(usernameField, gbc);
        
        row++;
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("كلمة المرور: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = ArabicUIHelper.createPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 32));
        formPanel.add(passwordField, gbc);
        
        row++;
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("تأكيد كلمة المرور: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        confirmPasswordField = ArabicUIHelper.createPasswordField(20);
        confirmPasswordField.setPreferredSize(new Dimension(200, 32));
        formPanel.add(confirmPasswordField, gbc);
        
        row++;
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الاسم الكامل: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        fullNameField = ArabicUIHelper.createTextField(20);
        fullNameField.setPreferredSize(new Dimension(200, 32));
        formPanel.add(fullNameField, gbc);
        
        row++;
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("البريد الإلكتروني:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = ArabicUIHelper.createTextField(20);
        emailField.setPreferredSize(new Dimension(200, 32));
        formPanel.add(emailField, gbc);
        
        row++;
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("رقم الهاتف:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phoneField = ArabicUIHelper.createTextField(20);
        phoneField.setPreferredSize(new Dimension(200, 32));
        formPanel.add(phoneField, gbc);
        
        row++;
        
        // Role
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الدور: *"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] roles = {"مدير مشروع", "عضو فريق"};
        roleComboBox = ArabicUIHelper.createComboBox(roles);
        roleComboBox.setPreferredSize(new Dimension(200, 32));
        formPanel.add(roleComboBox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        registerButton = ArabicUIHelper.createSuccessButton("تسجيل");
        registerButton.setPreferredSize(new Dimension(120, 38));
        registerButton.addActionListener(e -> handleRegister());
        
        backButton = ArabicUIHelper.createSecondaryButton("رجوع");
        backButton.setPreferredSize(new Dimension(120, 38));
        backButton.addActionListener(e -> goBack());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        // Note panel
        JPanel notePanel = ArabicUIHelper.createPanel(new BorderLayout());
        notePanel.setBackground(new Color(245, 245, 245));
        JLabel noteLabel = ArabicUIHelper.createLabel("* الحقول المطلوبة", 12);
        noteLabel.setForeground(new Color(150, 150, 150));
        notePanel.add(noteLabel, BorderLayout.NORTH);
        notePanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.add(notePanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Window close listener
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                goBack();
            }
        });
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        int roleId = roleComboBox.getSelectedIndex() + 1;
        
        // Validation
        if (!ValidationHelper.isValidUsername(username)) {
            ArabicUIHelper.showError(this, ValidationHelper.ErrorMessages.INVALID_USERNAME, "خطأ");
            usernameField.requestFocus();
            return;
        }
        
        if (!ValidationHelper.isValidPassword(password)) {
            ArabicUIHelper.showError(this, ValidationHelper.ErrorMessages.INVALID_PASSWORD, "خطأ");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            ArabicUIHelper.showError(this, ValidationHelper.ErrorMessages.PASSWORDS_NOT_MATCH, "خطأ");
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (!ValidationHelper.isNotEmpty(fullName)) {
            ArabicUIHelper.showError(this, "الرجاء إدخال الاسم الكامل", "خطأ");
            fullNameField.requestFocus();
            return;
        }
        
        if (!ValidationHelper.isValidEmail(email)) {
            ArabicUIHelper.showError(this, ValidationHelper.ErrorMessages.INVALID_EMAIL, "خطأ");
            emailField.requestFocus();
            return;
        }
        
        if (!ValidationHelper.isValidPhone(phone)) {
            ArabicUIHelper.showError(this, ValidationHelper.ErrorMessages.INVALID_PHONE, "خطأ");
            phoneField.requestFocus();
            return;
        }
        
        authController.register(username, password, fullName, email, phone, roleId);
    }
    
    private void goBack() {
        dispose();
        loginView.setVisible(true);
    }
    
    public void showRegisterSuccess() {
        ArabicUIHelper.showInfo(this, "تم تسجيل الحساب بنجاح!\nيمكنك الآن تسجيل الدخول.", "نجاح");
        goBack();
    }
    
    public void showRegisterError(String message) {
        ArabicUIHelper.showError(this, message, "خطأ في التسجيل");
    }
}
