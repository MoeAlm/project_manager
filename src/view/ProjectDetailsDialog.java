package view;

import dao.ProjectMemberDAO;
import dao.TaskDAO;
import model.Project;
import model.ProjectMember;
import model.Task;
import util.ArabicUIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * حوار تفاصيل المشروع
 * Project Details Dialog
 */
public class ProjectDetailsDialog extends JDialog {
    
    private Project project;
    private JTabbedPane tabbedPane;
    private TaskDAO taskDAO;
    private ProjectMemberDAO memberDAO;
    
    public ProjectDetailsDialog(JFrame parent, Project project) {
        super(parent, "تفاصيل المشروع: " + project.getProjectName(), true);
        this.project = project;
        this.taskDAO = new TaskDAO();
        this.memberDAO = new ProjectMemberDAO();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        ArabicUIHelper.applyRTL(this);
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header with project info
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for tasks and members
        tabbedPane = ArabicUIHelper.createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeBtn = ArabicUIHelper.createSecondaryButton("إغلاق");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Title
        JLabel titleLabel = ArabicUIHelper.createLabel(project.getProjectName(), 20);
        titleLabel.setFont(ArabicUIHelper.getArabicFontBold(20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Info grid
        JPanel infoPanel = ArabicUIHelper.createPanel(new GridLayout(2, 4, 20, 10));
        infoPanel.setOpaque(false);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        addInfoItem(infoPanel, "المدير:", project.getManagerName());
        addInfoItem(infoPanel, "الحالة:", project.getStatusNameAr());
        addInfoItem(infoPanel, "تاريخ البدء:", 
            project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "-");
        addInfoItem(infoPanel, "تاريخ الانتهاء:", 
            project.getEndDate() != null ? dateFormat.format(project.getEndDate()) : "-");
        addInfoItem(infoPanel, "نسبة الإكمال:", 
            String.format("%.1f%%", project.getCompletionPercentage()));
        addInfoItem(infoPanel, "عدد المهام:", String.valueOf(project.getTaskCount()));
        addInfoItem(infoPanel, "عدد الأعضاء:", String.valueOf(project.getMemberCount()));
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Description
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            JPanel descPanel = ArabicUIHelper.createPanel(new BorderLayout());
            descPanel.setOpaque(false);
            JLabel descLabel = ArabicUIHelper.createLabel("الوصف: " + project.getDescription());
            descLabel.setForeground(new Color(100, 100, 100));
            descPanel.add(descLabel, BorderLayout.EAST);
            panel.add(descPanel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private void addInfoItem(JPanel panel, String label, String value) {
        JPanel itemPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        itemPanel.setOpaque(false);
        
        JLabel labelComp = ArabicUIHelper.createLabel(label);
        labelComp.setForeground(new Color(100, 100, 100));
        itemPanel.add(labelComp);
        
        JLabel valueComp = ArabicUIHelper.createLabel(value != null ? value : "-");
        valueComp.setFont(ArabicUIHelper.getArabicFontBold());
        itemPanel.add(valueComp);
        
        panel.add(itemPanel);
    }
    
    private void loadData() {
        loadTasks();
        loadMembers();
    }
    
    private void loadTasks() {
        try {
            List<Task> tasks = taskDAO.findByProject(project.getId());
            
            JPanel tasksPanel = ArabicUIHelper.createPanel(new BorderLayout());
            tasksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            String[] columns = {"#", "المهمة", "المسؤول", "الأولوية", "الحالة", "تاريخ الاستحقاق", "نسبة الإكمال"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int index = 1;
            for (Task task : tasks) {
                Object[] row = {
                    index++,
                    task.getTaskName(),
                    task.getAssignedToName() != null ? task.getAssignedToName() : "-",
                    task.getPriorityNameAr(),
                    task.getStatusNameAr(),
                    task.getDueDate() != null ? dateFormat.format(task.getDueDate()) : "-",
                    String.format("%.1f%%", task.getCompletionPercentage())
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            ArabicUIHelper.applyRTLToTable(table);
            table.setRowHeight(30);
            
            tasksPanel.add(ArabicUIHelper.createScrollPane(table), BorderLayout.CENTER);
            
            // Summary
            JPanel summaryPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT));
            summaryPanel.add(ArabicUIHelper.createLabel("إجمالي المهام: " + tasks.size()));
            tasksPanel.add(summaryPanel, BorderLayout.SOUTH);
            
            tabbedPane.addTab("المهام (" + tasks.size() + ")", tasksPanel);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadMembers() {
        try {
            List<ProjectMember> members = memberDAO.findByProjectWithPerformance(project.getId());
            
            JPanel membersPanel = ArabicUIHelper.createPanel(new BorderLayout());
            membersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            String[] columns = {"#", "العضو", "الدور", "المهام الكلية", "المهام المكتملة", "نسبة الإنجاز"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            int index = 1;
            for (ProjectMember member : members) {
                Object[] row = {
                    index++,
                    member.getUserFullName(),
                    member.getProjectRoleNameAr(),
                    member.getTotalTasks(),
                    member.getCompletedTasks(),
                    String.format("%.1f%%", member.getCompletionRate())
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            ArabicUIHelper.applyRTLToTable(table);
            table.setRowHeight(30);
            
            membersPanel.add(ArabicUIHelper.createScrollPane(table), BorderLayout.CENTER);
            
            // Summary
            JPanel summaryPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT));
            summaryPanel.add(ArabicUIHelper.createLabel("إجمالي الأعضاء: " + members.size()));
            membersPanel.add(summaryPanel, BorderLayout.SOUTH);
            
            tabbedPane.addTab("الأعضاء (" + members.size() + ")", membersPanel);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
