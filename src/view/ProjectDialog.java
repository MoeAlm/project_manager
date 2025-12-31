package view;

import dao.ProjectDAO;
import dao.UserDAO;
import model.Project;
import model.User;
import util.ArabicUIHelper;
import util.SessionManager;
import util.ValidationHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * حوار إضافة/تعديل مشروع
 * Project Add/Edit Dialog
 */
public class ProjectDialog extends JDialog {
    
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> statusComboBox;
    private JComboBox<User> managerComboBox;
    
    private Project project;
    private boolean confirmed = false;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private List<Object[]> statuses;
    
    public ProjectDialog(JFrame parent, Project project) {
        super(parent, project == null ? "مشروع جديد" : "تعديل المشروع", true);
        this.project = project;
        this.projectDAO = new ProjectDAO();
        this.userDAO = new UserDAO();
        initComponents();
        loadData();
        if (project != null) {
            populateFields();
        }
    }
    
    private void initComponents() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
        ArabicUIHelper.applyRTL(this);
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        String title = project == null ? "إضافة مشروع جديد" : "تعديل المشروع";
        JLabel titleLabel = ArabicUIHelper.createTitleLabel(title);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        
        JPanel formPanel = ArabicUIHelper.createPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        int row = 0;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("اسم المشروع: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        nameField = ArabicUIHelper.createTextField(25);
        formPanel.add(nameField, gbc);
        
        row++;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الوصف:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        descriptionArea = ArabicUIHelper.createTextArea(3, 25);
        JScrollPane descScrollPane = ArabicUIHelper.createScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(200, 70));
        formPanel.add(descScrollPane, gbc);
        
        row++;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("تاريخ البدء: * (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        startDateField = ArabicUIHelper.createTextField(25);
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        formPanel.add(startDateField, gbc);
        
        row++;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("تاريخ الانتهاء: (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        endDateField = ArabicUIHelper.createTextField(25);
        formPanel.add(endDateField, gbc);
        
        row++;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الحالة: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        statusComboBox = ArabicUIHelper.createComboBox();
        formPanel.add(statusComboBox, gbc);
        
        row++;
        
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("المدير: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        managerComboBox = ArabicUIHelper.createComboBox();
        formPanel.add(managerComboBox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton saveButton = ArabicUIHelper.createSuccessButton("حفظ");
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> save());
        buttonPanel.add(saveButton);
        
        JButton cancelButton = ArabicUIHelper.createSecondaryButton("إلغاء");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadData() {
        try {
            
            statuses = projectDAO.getAllStatuses();
            for (Object[] status : statuses) {
                statusComboBox.addItem((String) status[2]); // status_name_ar
            }
            
            
            List<User> managers = userDAO.findByRole(1); // PROJECT_MANAGER role
            for (User manager : managers) {
                managerComboBox.addItem(manager);
            }
            
            
            User currentUser = SessionManager.getInstance().getCurrentUser();
            for (int i = 0; i < managerComboBox.getItemCount(); i++) {
                if (managerComboBox.getItemAt(i).getId() == currentUser.getId()) {
                    managerComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تحميل البيانات", "خطأ");
        }
    }
    
    private void populateFields() {
        nameField.setText(project.getProjectName());
        descriptionArea.setText(project.getDescription());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (project.getStartDate() != null) {
            startDateField.setText(dateFormat.format(project.getStartDate()));
        }
        if (project.getEndDate() != null) {
            endDateField.setText(dateFormat.format(project.getEndDate()));
        }
        
        
        statusComboBox.setSelectedIndex(project.getStatusId() - 1);
        
        
        for (int i = 0; i < managerComboBox.getItemCount(); i++) {
            if (managerComboBox.getItemAt(i).getId() == project.getManagerId()) {
                managerComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void save() {
        
        String name = nameField.getText().trim();
        if (!ValidationHelper.isNotEmpty(name)) {
            ArabicUIHelper.showError(this, "الرجاء إدخال اسم المشروع", "خطأ");
            nameField.requestFocus();
            return;
        }
        
        String startDateStr = startDateField.getText().trim();
        if (!ValidationHelper.isNotEmpty(startDateStr)) {
            ArabicUIHelper.showError(this, "الرجاء إدخال تاريخ البدء", "خطأ");
            startDateField.requestFocus();
            return;
        }
        
        Date startDate, endDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        
        try {
            startDate = new Date(dateFormat.parse(startDateStr).getTime());
        } catch (ParseException e) {
            ArabicUIHelper.showError(this, "صيغة تاريخ البدء غير صحيحة (YYYY-MM-DD)", "خطأ");
            startDateField.requestFocus();
            return;
        }
        
        String endDateStr = endDateField.getText().trim();
        if (!endDateStr.isEmpty()) {
            try {
                endDate = new Date(dateFormat.parse(endDateStr).getTime());
                if (endDate.before(startDate)) {
                    ArabicUIHelper.showError(this, ValidationHelper.ErrorMessages.END_DATE_BEFORE_START, "خطأ");
                    endDateField.requestFocus();
                    return;
                }
            } catch (ParseException e) {
                ArabicUIHelper.showError(this, "صيغة تاريخ الانتهاء غير صحيحة (YYYY-MM-DD)", "خطأ");
                endDateField.requestFocus();
                return;
            }
        }
        
        User selectedManager = (User) managerComboBox.getSelectedItem();
        if (selectedManager == null) {
            ArabicUIHelper.showError(this, "الرجاء اختيار مدير المشروع", "خطأ");
            return;
        }
        
        int statusId = statusComboBox.getSelectedIndex() + 1;
        
        try {
            if (project == null) {
                
                Project newProject = new Project(name, descriptionArea.getText().trim(), 
                    startDate, endDate, selectedManager.getId());
                newProject.setStatusId(statusId);
                
                int projectId = projectDAO.create(newProject);
                if (projectId > 0) {
                    confirmed = true;
                    ArabicUIHelper.showInfo(this, "تم إنشاء المشروع بنجاح", "نجاح");
                    dispose();
                } else {
                    ArabicUIHelper.showError(this, "فشل في إنشاء المشروع", "خطأ");
                }
            } else {
                
                project.setProjectName(name);
                project.setDescription(descriptionArea.getText().trim());
                project.setStartDate(startDate);
                project.setEndDate(endDate);
                project.setStatusId(statusId);
                project.setManagerId(selectedManager.getId());
                
                if (projectDAO.update(project)) {
                    confirmed = true;
                    ArabicUIHelper.showInfo(this, "تم تحديث المشروع بنجاح", "نجاح");
                    dispose();
                } else {
                    ArabicUIHelper.showError(this, "فشل في تحديث المشروع", "خطأ");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في حفظ المشروع: " + e.getMessage(), "خطأ");
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
