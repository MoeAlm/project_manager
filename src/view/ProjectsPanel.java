package view;

import controller.ProjectController;
import dao.ProjectDAO;
import model.Project;
import util.ArabicUIHelper;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * لوحة المشاريع
 * Projects Panel
 */
public class ProjectsPanel extends JPanel {
    
    private MainDashboard parent;
    private JTable projectsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private ProjectDAO projectDAO;
    private ProjectController projectController;
    private List<Project> currentProjects;
    
    public ProjectsPanel(MainDashboard parent) {
        this.parent = parent;
        this.projectDAO = new ProjectDAO();
        this.projectController = new ProjectController(this);
        initComponents();
        loadProjects();
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
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("إدارة المشاريع");
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel filterPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setOpaque(false);
        
        // Status filter
        filterPanel.add(ArabicUIHelper.createLabel("الحالة:"));
        String[] statuses = {"الكل", "لم يبدأ", "قيد التنفيذ", "متوقف", "مكتمل", "ملغي"};
        statusFilter = ArabicUIHelper.createComboBox(statuses);
        statusFilter.setPreferredSize(new Dimension(120, 30));
        statusFilter.addActionListener(e -> filterProjects());
        filterPanel.add(statusFilter);
        
        // Search
        filterPanel.add(ArabicUIHelper.createLabel("بحث:"));
        searchField = ArabicUIHelper.createTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.addActionListener(e -> searchProjects());
        filterPanel.add(searchField);
        
        JButton searchBtn = ArabicUIHelper.createPrimaryButton("بحث");
        searchBtn.setPreferredSize(new Dimension(70, 30));
        searchBtn.addActionListener(e -> searchProjects());
        filterPanel.add(searchBtn);
        
        panel.add(filterPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Table columns
        String[] columns = {"#", "اسم المشروع", "المدير", "تاريخ البدء", "تاريخ الانتهاء", 
                           "الحالة", "نسبة الإكمال", "المهام", "الأعضاء"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projectsTable = new JTable(tableModel);
        ArabicUIHelper.applyRTLToTable(projectsTable);
        projectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectsTable.setRowHeight(35);
        
        // Column widths
        projectsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        projectsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        projectsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        projectsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        projectsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        projectsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        projectsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        projectsTable.getColumnModel().getColumn(7).setPreferredWidth(60);
        projectsTable.getColumnModel().getColumn(8).setPreferredWidth(60);
        
        // Double click to view details
        projectsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewProjectDetails();
                }
            }
        });
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(projectsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);
        
        if (SessionManager.getInstance().isProjectManager()) {
            JButton newBtn = ArabicUIHelper.createSuccessButton("مشروع جديد");
            newBtn.setPreferredSize(new Dimension(120, 35));
            newBtn.addActionListener(e -> showNewProjectDialog());
            panel.add(newBtn);
            
            JButton editBtn = ArabicUIHelper.createPrimaryButton("تعديل");
            editBtn.setPreferredSize(new Dimension(100, 35));
            editBtn.addActionListener(e -> editProject());
            panel.add(editBtn);
            
            JButton deleteBtn = ArabicUIHelper.createDangerButton("حذف");
            deleteBtn.setPreferredSize(new Dimension(100, 35));
            deleteBtn.addActionListener(e -> deleteProject());
            panel.add(deleteBtn);
        }
        
        JButton viewBtn = ArabicUIHelper.createPrimaryButton("عرض التفاصيل");
        viewBtn.setPreferredSize(new Dimension(120, 35));
        viewBtn.addActionListener(e -> viewProjectDetails());
        panel.add(viewBtn);
        
        JButton refreshBtn = ArabicUIHelper.createSecondaryButton("تحديث");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.addActionListener(e -> refresh());
        panel.add(refreshBtn);
        
        return panel;
    }
    
    public void loadProjects() {
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
                    currentProjects = get();
                    updateTable(currentProjects);
                } catch (Exception e) {
                    e.printStackTrace();
                    ArabicUIHelper.showError(ProjectsPanel.this, 
                        "خطأ في تحميل المشاريع: " + e.getMessage(), "خطأ");
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<Project> projects) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        int index = 1;
        for (Project project : projects) {
            Object[] row = {
                index++,
                project.getProjectName(),
                project.getManagerName(),
                project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "-",
                project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "-",
                project.getStatusNameAr(),
                String.format("%.1f%%", project.getCompletionPercentage()),
                project.getTaskCount(),
                project.getMemberCount()
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterProjects() {
        if (currentProjects == null) return;
        
        int selectedStatus = statusFilter.getSelectedIndex();
        if (selectedStatus == 0) {
            updateTable(currentProjects);
        } else {
            List<Project> filtered = currentProjects.stream()
                .filter(p -> p.getStatusId() == selectedStatus)
                .toList();
            updateTable(filtered);
        }
    }
    
    private void searchProjects() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadProjects();
            return;
        }
        
        SwingWorker<List<Project>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Project> doInBackground() throws Exception {
                return projectDAO.search(keyword);
            }
            
            @Override
            protected void done() {
                try {
                    List<Project> results = get();
                    currentProjects = results;
                    updateTable(results);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    public void showNewProjectDialog() {
        ProjectDialog dialog = new ProjectDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    private void editProject() {
        int selectedRow = projectsTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مشروع", "تنبيه");
            return;
        }
        
        Project project = currentProjects.get(selectedRow);
        ProjectDialog dialog = new ProjectDialog(parent, project);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    private void deleteProject() {
        int selectedRow = projectsTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مشروع", "تنبيه");
            return;
        }
        
        Project project = currentProjects.get(selectedRow);
        boolean confirm = ArabicUIHelper.showConfirm(this, 
            "هل تريد حذف المشروع: " + project.getProjectName() + "؟\n" +
            "سيتم حذف جميع المهام والأعضاء المرتبطين بهذا المشروع.", "تأكيد الحذف");
        
        if (confirm) {
            projectController.deleteProject(project.getId());
        }
    }
    
    private void viewProjectDetails() {
        int selectedRow = projectsTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مشروع", "تنبيه");
            return;
        }
        
        Project project = currentProjects.get(selectedRow);
        ProjectDetailsDialog dialog = new ProjectDetailsDialog(parent, project);
        dialog.setVisible(true);
    }
    
    public void refresh() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        loadProjects();
    }
    
    public void showSuccess(String message) {
        ArabicUIHelper.showInfo(this, message, "نجاح");
        refresh();
    }
    
    public void showError(String message) {
        ArabicUIHelper.showError(this, message, "خطأ");
    }
}
