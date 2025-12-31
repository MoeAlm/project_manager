package view;

import controller.TaskController;
import dao.ProjectDAO;
import dao.TaskDAO;
import model.Project;
import model.Task;
import util.ArabicUIHelper;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * لوحة المهام
 * Tasks Panel
 */
public class TasksPanel extends JPanel {
    
    private MainDashboard parent;
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;
    private JComboBox<Project> projectFilter;
    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private TaskController taskController;
    private List<Task> currentTasks;
    
    public TasksPanel(MainDashboard parent) {
        this.parent = parent;
        this.taskDAO = new TaskDAO();
        this.projectDAO = new ProjectDAO();
        this.taskController = new TaskController(this);
        initComponents();
        loadTasks();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(250, 250, 250));
        ArabicUIHelper.applyRTL(this);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Title
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("إدارة المهام");
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setOpaque(false);
        
        // Project filter
        filterPanel.add(ArabicUIHelper.createLabel("المشروع:"));
        projectFilter = ArabicUIHelper.createComboBox();
        projectFilter.setPreferredSize(new Dimension(150, 30));
        projectFilter.addItem(null); // All projects
        loadProjectsFilter();
        projectFilter.addActionListener(e -> filterTasks());
        filterPanel.add(projectFilter);
        
        // Status filter
        filterPanel.add(ArabicUIHelper.createLabel("الحالة:"));
        String[] statuses = {"الكل", "للتنفيذ", "قيد التنفيذ", "قيد المراجعة", "مكتملة", "معلقة"};
        statusFilter = ArabicUIHelper.createComboBox(statuses);
        statusFilter.setPreferredSize(new Dimension(100, 30));
        statusFilter.addActionListener(e -> filterTasks());
        filterPanel.add(statusFilter);
        
        // Priority filter
        filterPanel.add(ArabicUIHelper.createLabel("الأولوية:"));
        String[] priorities = {"الكل", "منخفضة", "متوسطة", "عالية", "حرجة"};
        priorityFilter = ArabicUIHelper.createComboBox(priorities);
        priorityFilter.setPreferredSize(new Dimension(100, 30));
        priorityFilter.addActionListener(e -> filterTasks());
        filterPanel.add(priorityFilter);
        
        // Search
        filterPanel.add(ArabicUIHelper.createLabel("بحث:"));
        searchField = ArabicUIHelper.createTextField(12);
        searchField.setPreferredSize(new Dimension(120, 30));
        searchField.addActionListener(e -> searchTasks());
        filterPanel.add(searchField);
        
        JButton searchBtn = ArabicUIHelper.createPrimaryButton("بحث");
        searchBtn.setPreferredSize(new Dimension(60, 30));
        searchBtn.addActionListener(e -> searchTasks());
        filterPanel.add(searchBtn);
        
        panel.add(filterPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Table columns
        String[] columns = {"#", "المهمة", "المشروع", "المسؤول", "الأولوية", 
                           "الحالة", "تاريخ الاستحقاق", "نسبة الإكمال"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tasksTable = new JTable(tableModel);
        ArabicUIHelper.applyRTLToTable(tasksTable);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksTable.setRowHeight(35);
        
        // Column widths
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        tasksTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        // Double click to view details
        tasksTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewTaskDetails();
                }
            }
        });
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(tasksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);
        
        if (SessionManager.getInstance().isProjectManager()) {
            JButton newBtn = ArabicUIHelper.createSuccessButton("مهمة جديدة");
            newBtn.setPreferredSize(new Dimension(120, 35));
            newBtn.addActionListener(e -> showNewTaskDialog());
            panel.add(newBtn);
            
            JButton editBtn = ArabicUIHelper.createPrimaryButton("تعديل");
            editBtn.setPreferredSize(new Dimension(100, 35));
            editBtn.addActionListener(e -> editTask());
            panel.add(editBtn);
            
            JButton deleteBtn = ArabicUIHelper.createDangerButton("حذف");
            deleteBtn.setPreferredSize(new Dimension(100, 35));
            deleteBtn.addActionListener(e -> deleteTask());
            panel.add(deleteBtn);
        }
        
        JButton updateProgressBtn = ArabicUIHelper.createPrimaryButton("تحديث التقدم");
        updateProgressBtn.setPreferredSize(new Dimension(120, 35));
        updateProgressBtn.addActionListener(e -> updateTaskProgress());
        panel.add(updateProgressBtn);
        
        JButton viewBtn = ArabicUIHelper.createPrimaryButton("عرض التفاصيل");
        viewBtn.setPreferredSize(new Dimension(120, 35));
        viewBtn.addActionListener(e -> viewTaskDetails());
        panel.add(viewBtn);
        
        JButton refreshBtn = ArabicUIHelper.createSecondaryButton("تحديث");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.addActionListener(e -> refresh());
        panel.add(refreshBtn);
        
        return panel;
    }
    
    private void loadProjectsFilter() {
        SwingWorker<List<Project>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Project> doInBackground() throws Exception {
                int userId = SessionManager.getInstance().getCurrentUserId();
                if (SessionManager.getInstance().isProjectManager()) {
                    return projectDAO.findAll();
                } else {
                    return projectDAO.findByMember(userId);
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Project> projects = get();
                    projectFilter.removeAllItems();
                    projectFilter.addItem(null);
                    for (Project p : projects) {
                        projectFilter.addItem(p);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    public void loadTasks() {
        SwingWorker<List<Task>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Task> doInBackground() throws Exception {
                int userId = SessionManager.getInstance().getCurrentUserId();
                if (SessionManager.getInstance().isProjectManager()) {
                    // Get all tasks from all projects
                    List<Project> projects = projectDAO.findAll();
                    java.util.ArrayList<Task> allTasks = new java.util.ArrayList<>();
                    for (Project p : projects) {
                        allTasks.addAll(taskDAO.findByProject(p.getId()));
                    }
                    return allTasks;
                } else {
                    return taskDAO.findByAssignedUser(userId);
                }
            }
            
            @Override
            protected void done() {
                try {
                    currentTasks = get();
                    updateTable(currentTasks);
                } catch (Exception e) {
                    e.printStackTrace();
                    ArabicUIHelper.showError(TasksPanel.this, 
                        "خطأ في تحميل المهام: " + e.getMessage(), "خطأ");
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<Task> tasks) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        int index = 1;
        for (Task task : tasks) {
            String dueDateStr = task.getDueDate() != null ? 
                dateFormat.format(task.getDueDate()) : "-";
            if (task.isOverdue()) {
                dueDateStr += " (متأخرة)";
            }
            
            Object[] row = {
                index++,
                task.getTaskName(),
                task.getProjectName(),
                task.getAssignedToName() != null ? task.getAssignedToName() : "-",
                task.getPriorityNameAr(),
                task.getStatusNameAr(),
                dueDateStr,
                String.format("%.1f%%", task.getCompletionPercentage())
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterTasks() {
        if (currentTasks == null) return;
        
        List<Task> filtered = currentTasks.stream()
            .filter(t -> {
                // Project filter
                Project selectedProject = (Project) projectFilter.getSelectedItem();
                if (selectedProject != null && t.getProjectId() != selectedProject.getId()) {
                    return false;
                }
                
                // Status filter
                int selectedStatus = statusFilter.getSelectedIndex();
                if (selectedStatus > 0 && t.getStatusId() != selectedStatus) {
                    return false;
                }
                
                // Priority filter
                int selectedPriority = priorityFilter.getSelectedIndex();
                if (selectedPriority > 0 && t.getPriorityId() != selectedPriority) {
                    return false;
                }
                
                return true;
            })
            .toList();
        
        updateTable(filtered);
    }
    
    private void searchTasks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadTasks();
            return;
        }
        
        SwingWorker<List<Task>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Task> doInBackground() throws Exception {
                return taskDAO.search(keyword);
            }
            
            @Override
            protected void done() {
                try {
                    List<Task> results = get();
                    currentTasks = results;
                    updateTable(results);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void showNewTaskDialog() {
        TaskDialog dialog = new TaskDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    private void editTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مهمة", "تنبيه");
            return;
        }
        
        Task task = currentTasks.get(selectedRow);
        TaskDialog dialog = new TaskDialog(parent, task);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    private void deleteTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مهمة", "تنبيه");
            return;
        }
        
        Task task = currentTasks.get(selectedRow);
        boolean confirm = ArabicUIHelper.showConfirm(this, 
            "هل تريد حذف المهمة: " + task.getTaskName() + "؟", "تأكيد الحذف");
        
        if (confirm) {
            taskController.deleteTask(task.getId(), task.getProjectId());
        }
    }
    
    private void updateTaskProgress() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مهمة", "تنبيه");
            return;
        }
        
        Task task = currentTasks.get(selectedRow);
        
        // Show progress update dialog
        String input = JOptionPane.showInputDialog(this, 
            "أدخل نسبة الإكمال (0-100):", 
            String.valueOf((int) task.getCompletionPercentage()));
        
        if (input != null && !input.isEmpty()) {
            try {
                double percentage = Double.parseDouble(input);
                if (percentage < 0 || percentage > 100) {
                    ArabicUIHelper.showError(this, "النسبة يجب أن تكون بين 0 و 100", "خطأ");
                    return;
                }
                taskController.updateTaskProgress(task.getId(), task.getProjectId(), percentage);
            } catch (NumberFormatException e) {
                ArabicUIHelper.showError(this, "الرجاء إدخال رقم صحيح", "خطأ");
            }
        }
    }
    
    private void viewTaskDetails() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مهمة", "تنبيه");
            return;
        }
        
        Task task = currentTasks.get(selectedRow);
        TaskDetailsDialog dialog = new TaskDetailsDialog(parent, task);
        dialog.setVisible(true);
    }
    
    public void refresh() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        priorityFilter.setSelectedIndex(0);
        projectFilter.setSelectedIndex(0);
        loadTasks();
        loadProjectsFilter();
    }
    
    public void showSuccess(String message) {
        ArabicUIHelper.showInfo(this, message, "نجاح");
        refresh();
    }
    
    public void showError(String message) {
        ArabicUIHelper.showError(this, message, "خطأ");
    }
}
