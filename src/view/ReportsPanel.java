package view;

import dao.ProjectDAO;
import dao.TaskDAO;
import dao.UserDAO;
import model.Project;
import model.Task;
import model.User;
import util.ArabicUIHelper;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * لوحة التقارير
 * Reports Panel
 */
public class ReportsPanel extends JPanel {
    
    private MainDashboard parent;
    private JTabbedPane reportsTabs;
    private ProjectDAO projectDAO;
    private TaskDAO taskDAO;
    private UserDAO userDAO;
    
    private JTable completedProjectsTable;
    private JTable overdueTasksTable;
    private JTable memberPerformanceTable;
    private DefaultTableModel completedProjectsModel;
    private DefaultTableModel overdueTasksModel;
    private DefaultTableModel memberPerformanceModel;
    
    public ReportsPanel(MainDashboard parent) {
        this.parent = parent;
        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
        this.userDAO = new UserDAO();
        initComponents();
        loadReports();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(250, 250, 250));
        ArabicUIHelper.applyRTL(this);
        
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("التقارير");
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(titleLabel, BorderLayout.NORTH);
        
        reportsTabs = ArabicUIHelper.createTabbedPane();
        
        JPanel completedProjectsPanel = createCompletedProjectsPanel();
        reportsTabs.addTab("المشاريع المكتملة", completedProjectsPanel);
        
        JPanel overdueTasksPanel = createOverdueTasksPanel();
        reportsTabs.addTab("المهام المتأخرة", overdueTasksPanel);
        
        JPanel memberPerformancePanel = createMemberPerformancePanel();
        reportsTabs.addTab("أداء الأعضاء", memberPerformancePanel);
        
        add(reportsTabs, BorderLayout.CENTER);
        
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = ArabicUIHelper.createPrimaryButton("تحديث التقارير");
        refreshBtn.setPreferredSize(new Dimension(130, 35));
        refreshBtn.addActionListener(e -> refresh());
        buttonPanel.add(refreshBtn);
        
        JButton exportBtn = ArabicUIHelper.createSecondaryButton("تصدير");
        exportBtn.setPreferredSize(new Dimension(100, 35));
        exportBtn.addActionListener(e -> exportReport());
        buttonPanel.add(exportBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCompletedProjectsPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel summaryPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(232, 245, 233));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel summaryLabel = ArabicUIHelper.createLabel("المشاريع المكتملة خلال الفترة الحالية", 14);
        summaryPanel.add(summaryLabel);
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        String[] columns = {"#", "اسم المشروع", "المدير", "تاريخ البدء", "تاريخ الانتهاء", "عدد المهام"};
        completedProjectsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        completedProjectsTable = new JTable(completedProjectsModel);
        ArabicUIHelper.applyRTLToTable(completedProjectsTable);
        completedProjectsTable.setRowHeight(32);
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(completedProjectsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOverdueTasksPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel summaryPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(255, 235, 238));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel summaryLabel = ArabicUIHelper.createLabel("المهام التي تجاوزت موعد استحقاقها", 14);
        summaryPanel.add(summaryLabel);
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        String[] columns = {"#", "المهمة", "المشروع", "المسؤول", "الأولوية", "تاريخ الاستحقاق", "أيام التأخير"};
        overdueTasksModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        overdueTasksTable = new JTable(overdueTasksModel);
        ArabicUIHelper.applyRTLToTable(overdueTasksTable);
        overdueTasksTable.setRowHeight(32);
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(overdueTasksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMemberPerformancePanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel summaryPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(227, 242, 253));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel summaryLabel = ArabicUIHelper.createLabel("تقرير أداء أعضاء الفريق", 14);
        summaryPanel.add(summaryLabel);
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        String[] columns = {"#", "العضو", "الدور", "المهام الكلية", "المهام المكتملة", 
                           "المهام المتأخرة", "نسبة الإنجاز"};
        memberPerformanceModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberPerformanceTable = new JTable(memberPerformanceModel);
        ArabicUIHelper.applyRTLToTable(memberPerformanceTable);
        memberPerformanceTable.setRowHeight(32);
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(memberPerformanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadReports() {
        loadCompletedProjects();
        loadOverdueTasks();
        loadMemberPerformance();
    }
    
    private void loadCompletedProjects() {
        SwingWorker<List<Project>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Project> doInBackground() throws Exception {
                return projectDAO.findCompleted();
            }
            
            @Override
            protected void done() {
                try {
                    List<Project> projects = get();
                    updateCompletedProjectsTable(projects);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateCompletedProjectsTable(List<Project> projects) {
        completedProjectsModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        int index = 1;
        for (Project project : projects) {
            Object[] row = {
                index++,
                project.getProjectName(),
                project.getManagerName(),
                project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "-",
                project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "-",
                project.getTaskCount()
            };
            completedProjectsModel.addRow(row);
        }
    }
    
    private void loadOverdueTasks() {
        SwingWorker<List<Task>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Task> doInBackground() throws Exception {
                return taskDAO.findOverdue();
            }
            
            @Override
            protected void done() {
                try {
                    List<Task> tasks = get();
                    updateOverdueTasksTable(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateOverdueTasksTable(List<Task> tasks) {
        overdueTasksModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date today = new java.util.Date();
        
        int index = 1;
        for (Task task : tasks) {
            long diffInMillies = today.getTime() - task.getDueDate().getTime();
            long diffDays = diffInMillies / (1000 * 60 * 60 * 24);
            
            Object[] row = {
                index++,
                task.getTaskName(),
                task.getProjectName(),
                task.getAssignedToName() != null ? task.getAssignedToName() : "-",
                task.getPriorityNameAr(),
                dateFormat.format(task.getDueDate()),
                diffDays + " يوم"
            };
            overdueTasksModel.addRow(row);
        }
    }
    
    private void loadMemberPerformance() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.findAllActive();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    updateMemberPerformanceTable(users);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateMemberPerformanceTable(List<User> users) {
        memberPerformanceModel.setRowCount(0);
        
        int index = 1;
        for (User user : users) {
            try {
                int totalTasks = taskDAO.countTotalByUser(user.getId());
                int completedTasks = taskDAO.countCompletedByUser(user.getId());
                List<Task> overdueTasks = taskDAO.findOverdueByUser(user.getId());
                double completionRate = totalTasks > 0 ? 
                    (double) completedTasks / totalTasks * 100 : 0;
                
                Object[] row = {
                    index++,
                    user.getFullName(),
                    user.getRoleNameAr(),
                    totalTasks,
                    completedTasks,
                    overdueTasks.size(),
                    String.format("%.1f%%", completionRate)
                };
                memberPerformanceModel.addRow(row);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void exportReport() {
        int selectedTab = reportsTabs.getSelectedIndex();
        String reportName;
        JTable table;
        
        switch (selectedTab) {
            case 0:
                reportName = "المشاريع_المكتملة";
                table = completedProjectsTable;
                break;
            case 1:
                reportName = "المهام_المتأخرة";
                table = overdueTasksTable;
                break;
            case 2:
                reportName = "أداء_الأعضاء";
                table = memberPerformanceTable;
                break;
            default:
                return;
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            sb.append(table.getColumnName(i));
            if (i < table.getColumnCount() - 1) sb.append(",");
        }
        sb.append("\n");
        
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(row, col);
                sb.append(value != null ? value.toString() : "");
                if (col < table.getColumnCount() - 1) sb.append(",");
            }
            sb.append("\n");
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(reportName + ".csv"));
        fileChooser.setDialogTitle("تصدير التقرير");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write("\uFEFF"); // BOM for UTF-8
                writer.write(sb.toString());
                writer.close();
                ArabicUIHelper.showInfo(this, "تم تصدير التقرير بنجاح", "نجاح");
            } catch (Exception e) {
                ArabicUIHelper.showError(this, "خطأ في تصدير التقرير: " + e.getMessage(), "خطأ");
            }
        }
    }
    
    public void refresh() {
        loadReports();
    }
}
