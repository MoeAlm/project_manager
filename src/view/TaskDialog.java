package view;

import dao.ProjectDAO;
import dao.ProjectMemberDAO;
import dao.TaskDAO;
import model.Project;
import model.ProjectMember;
import model.Task;
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
 * حوار إضافة/تعديل مهمة
 * Task Add/Edit Dialog
 */
public class TaskDialog extends JDialog {
    
    private JComboBox<Project> projectComboBox;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<ProjectMember> assigneeComboBox;
    private JComboBox<String> priorityComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField dueDateField;
    private JSpinner completionSpinner;
    
    private Task task;
    private boolean confirmed = false;
    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private ProjectMemberDAO memberDAO;
    private List<Object[]> priorities;
    private List<Object[]> statuses;
    
    public TaskDialog(JFrame parent, Task task) {
        super(parent, task == null ? "مهمة جديدة" : "تعديل المهمة", true);
        this.task = task;
        this.taskDAO = new TaskDAO();
        this.projectDAO = new ProjectDAO();
        this.memberDAO = new ProjectMemberDAO();
        initComponents();
        loadData();
        if (task != null) {
            populateFields();
        }
    }
    
    private void initComponents() {
        setSize(520, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        ArabicUIHelper.applyRTL(this);
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        String title = task == null ? "إضافة مهمة جديدة" : "تعديل المهمة";
        JLabel titleLabel = ArabicUIHelper.createTitleLabel(title);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = ArabicUIHelper.createPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 5, 7, 5);
        
        int row = 0;
        
        // Project
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("المشروع: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        projectComboBox = ArabicUIHelper.createComboBox();
        projectComboBox.addActionListener(e -> loadProjectMembers());
        formPanel.add(projectComboBox, gbc);
        
        row++;
        
        // Task name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("اسم المهمة: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        nameField = ArabicUIHelper.createTextField(25);
        formPanel.add(nameField, gbc);
        
        row++;
        
        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الوصف:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        descriptionArea = ArabicUIHelper.createTextArea(3, 25);
        JScrollPane descScrollPane = ArabicUIHelper.createScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(200, 60));
        formPanel.add(descScrollPane, gbc);
        
        row++;
        
        // Assignee
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("المسؤول:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        assigneeComboBox = ArabicUIHelper.createComboBox();
        formPanel.add(assigneeComboBox, gbc);
        
        row++;
        
        // Priority
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الأولوية: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        priorityComboBox = ArabicUIHelper.createComboBox();
        formPanel.add(priorityComboBox, gbc);
        
        row++;
        
        // Status
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("الحالة: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        statusComboBox = ArabicUIHelper.createComboBox();
        formPanel.add(statusComboBox, gbc);
        
        row++;
        
        // Due date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("تاريخ الاستحقاق: (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dueDateField = ArabicUIHelper.createTextField(25);
        formPanel.add(dueDateField, gbc);
        
        row++;
        
        // Completion percentage
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(ArabicUIHelper.createLabel("نسبة الإكمال:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 5);
        completionSpinner = new JSpinner(spinnerModel);
        completionSpinner.setFont(ArabicUIHelper.getArabicFont());
        formPanel.add(completionSpinner, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
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
            // Load projects
            int userId = SessionManager.getInstance().getCurrentUserId();
            List<Project> projects;
            if (SessionManager.getInstance().isProjectManager()) {
                projects = projectDAO.findAll();
            } else {
                projects = projectDAO.findByMember(userId);
            }
            for (Project p : projects) {
                projectComboBox.addItem(p);
            }
            
            // Load priorities
            priorities = taskDAO.getAllPriorities();
            for (Object[] priority : priorities) {
                priorityComboBox.addItem((String) priority[2]); // priority_name_ar
            }
            priorityComboBox.setSelectedIndex(1); // Default: Medium
            
            // Load statuses
            statuses = taskDAO.getAllStatuses();
            for (Object[] status : statuses) {
                statusComboBox.addItem((String) status[2]); // status_name_ar
            }
            
            // Load members for first project
            if (projectComboBox.getItemCount() > 0) {
                loadProjectMembers();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تحميل البيانات", "خطأ");
        }
    }
    
    private void loadProjectMembers() {
        assigneeComboBox.removeAllItems();
        assigneeComboBox.addItem(null); // No assignee option
        
        Project selectedProject = (Project) projectComboBox.getSelectedItem();
        if (selectedProject == null) return;
        
        try {
            List<ProjectMember> members = memberDAO.findByProject(selectedProject.getId());
            for (ProjectMember member : members) {
                assigneeComboBox.addItem(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void populateFields() {
        // Set project
        for (int i = 0; i < projectComboBox.getItemCount(); i++) {
            if (projectComboBox.getItemAt(i).getId() == task.getProjectId()) {
                projectComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        nameField.setText(task.getTaskName());
        descriptionArea.setText(task.getDescription());
        
        // Load members and set assignee
        loadProjectMembers();
        if (task.getAssignedTo() > 0) {
            for (int i = 0; i < assigneeComboBox.getItemCount(); i++) {
                ProjectMember member = assigneeComboBox.getItemAt(i);
                if (member != null && member.getUserId() == task.getAssignedTo()) {
                    assigneeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Set priority
        priorityComboBox.setSelectedIndex(task.getPriorityId() - 1);
        
        // Set status
        statusComboBox.setSelectedIndex(task.getStatusId() - 1);
        
        // Set due date
        if (task.getDueDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dueDateField.setText(dateFormat.format(task.getDueDate()));
        }
        
        // Set completion
        completionSpinner.setValue((int) task.getCompletionPercentage());
        
        // Disable project change for existing task
        projectComboBox.setEnabled(false);
    }
    
    private void save() {
        // Validation
        Project selectedProject = (Project) projectComboBox.getSelectedItem();
        if (selectedProject == null) {
            ArabicUIHelper.showError(this, "الرجاء اختيار المشروع", "خطأ");
            return;
        }
        
        String name = nameField.getText().trim();
        if (!ValidationHelper.isNotEmpty(name)) {
            ArabicUIHelper.showError(this, "الرجاء إدخال اسم المهمة", "خطأ");
            nameField.requestFocus();
            return;
        }
        
        Date dueDate = null;
        String dueDateStr = dueDateField.getText().trim();
        if (!dueDateStr.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            try {
                dueDate = new Date(dateFormat.parse(dueDateStr).getTime());
            } catch (ParseException e) {
                ArabicUIHelper.showError(this, "صيغة التاريخ غير صحيحة (YYYY-MM-DD)", "خطأ");
                dueDateField.requestFocus();
                return;
            }
        }
        
        ProjectMember assignee = (ProjectMember) assigneeComboBox.getSelectedItem();
        int assignedTo = assignee != null ? assignee.getUserId() : 0;
        int priorityId = priorityComboBox.getSelectedIndex() + 1;
        int statusId = statusComboBox.getSelectedIndex() + 1;
        double completion = (Integer) completionSpinner.getValue();
        
        try {
            if (task == null) {
                // Create new task
                Task newTask = new Task(
                    selectedProject.getId(),
                    name,
                    descriptionArea.getText().trim(),
                    assignedTo,
                    priorityId,
                    dueDate,
                    SessionManager.getInstance().getCurrentUserId()
                );
                newTask.setStatusId(statusId);
                newTask.setCompletionPercentage(completion);
                
                int taskId = taskDAO.create(newTask);
                if (taskId > 0) {
                    // Update project completion
                    projectDAO.updateCompletionPercentage(selectedProject.getId());
                    confirmed = true;
                    ArabicUIHelper.showInfo(this, "تم إنشاء المهمة بنجاح", "نجاح");
                    dispose();
                } else {
                    ArabicUIHelper.showError(this, "فشل في إنشاء المهمة", "خطأ");
                }
            } else {
                // Update existing task
                task.setTaskName(name);
                task.setDescription(descriptionArea.getText().trim());
                task.setAssignedTo(assignedTo);
                task.setPriorityId(priorityId);
                task.setStatusId(statusId);
                task.setDueDate(dueDate);
                task.setCompletionPercentage(completion);
                
                if (taskDAO.update(task)) {
                    // Update project completion
                    projectDAO.updateCompletionPercentage(task.getProjectId());
                    confirmed = true;
                    ArabicUIHelper.showInfo(this, "تم تحديث المهمة بنجاح", "نجاح");
                    dispose();
                } else {
                    ArabicUIHelper.showError(this, "فشل في تحديث المهمة", "خطأ");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في حفظ المهمة: " + e.getMessage(), "خطأ");
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
