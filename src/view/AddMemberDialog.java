package view;

import dao.ProjectMemberDAO;
import dao.UserDAO;
import model.Project;
import model.ProjectMember;
import model.User;
import util.ArabicUIHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * حوار إضافة عضو للمشروع
 * Add Member Dialog
 */
public class AddMemberDialog extends JDialog {
    
    private JComboBox<User> userComboBox;
    private JComboBox<String> roleComboBox;
    private Project project;
    private boolean confirmed = false;
    private UserDAO userDAO;
    private ProjectMemberDAO memberDAO;
    private List<Object[]> projectRoles;
    
    public AddMemberDialog(JFrame parent, Project project) {
        super(parent, "إضافة عضو للمشروع: " + project.getProjectName(), true);
        this.project = project;
        this.userDAO = new UserDAO();
        this.memberDAO = new ProjectMemberDAO();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setResizable(false);
        ArabicUIHelper.applyRTL(this);
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("إضافة عضو جديد");
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = ArabicUIHelper.createPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("المستخدم: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        userComboBox = ArabicUIHelper.createComboBox();
        userComboBox.setPreferredSize(new Dimension(200, 32));
        formPanel.add(userComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الدور: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        roleComboBox = ArabicUIHelper.createComboBox();
        roleComboBox.setPreferredSize(new Dimension(200, 32));
        formPanel.add(roleComboBox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton addButton = ArabicUIHelper.createSuccessButton("إضافة");
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> addMember());
        buttonPanel.add(addButton);
        
        JButton cancelButton = ArabicUIHelper.createSecondaryButton("إلغاء");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadData() {
        try {
            List<User> allUsers = userDAO.findAllActive();
            
            List<ProjectMember> existingMembers = memberDAO.findByProject(project.getId());
            
            for (User user : allUsers) {
                boolean isMember = existingMembers.stream()
                    .anyMatch(m -> m.getUserId() == user.getId());
                
                if (!isMember && user.getId() != project.getManagerId()) {
                    userComboBox.addItem(user);
                }
            }
            
            projectRoles = memberDAO.getAllProjectRoles();
            for (Object[] role : projectRoles) {
                roleComboBox.addItem((String) role[2]);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تحميل البيانات", "خطأ");
        }
    }
    
    private void addMember() {
        User selectedUser = (User) userComboBox.getSelectedItem();
        if (selectedUser == null) {
            ArabicUIHelper.showError(this, "الرجاء اختيار مستخدم", "خطأ");
            return;
        }
        
        int selectedRoleIndex = roleComboBox.getSelectedIndex();
        if (selectedRoleIndex < 0) {
            ArabicUIHelper.showError(this, "الرجاء اختيار دور", "خطأ");
            return;
        }
        
        int roleId = (int) projectRoles.get(selectedRoleIndex)[0];
        
        try {
            
            if (memberDAO.isMember(project.getId(), selectedUser.getId())) {
                ArabicUIHelper.showWarning(this, "هذا المستخدم عضو في المشروع بالفعل", "تنبيه");
                return;
            }
            
            ProjectMember member = new ProjectMember(project.getId(), selectedUser.getId(), roleId);
            int result = memberDAO.addMember(member);
            
            if (result > 0) {
                confirmed = true;
                ArabicUIHelper.showInfo(this, "تم إضافة العضو بنجاح", "نجاح");
                dispose();
            } else {
                ArabicUIHelper.showError(this, "فشل في إضافة العضو", "خطأ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في إضافة العضو: " + e.getMessage(), "خطأ");
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
