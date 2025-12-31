package view;

import dao.ActivityLogDAO;
import model.ActivityLog;
import model.Task;
import util.ArabicUIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * حوار تفاصيل المهمة
 * Task Details Dialog
 */
public class TaskDetailsDialog extends JDialog {
    
    private Task task;
    private ActivityLogDAO activityLogDAO;
    
    public TaskDetailsDialog(JFrame parent, Task task) {
        super(parent, "تفاصيل المهمة: " + task.getTaskName(), true);
        this.task = task;
        this.activityLogDAO = new ActivityLogDAO();
        initComponents();
    }
    
    private void initComponents() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        ArabicUIHelper.applyRTL(this);
        
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header with task info
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Activity log
        JPanel activityPanel = createActivityPanel();
        mainPanel.add(activityPanel, BorderLayout.CENTER);
        
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
        JLabel titleLabel = ArabicUIHelper.createLabel(task.getTaskName(), 18);
        titleLabel.setFont(ArabicUIHelper.getArabicFontBold(18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Info grid
        JPanel infoPanel = ArabicUIHelper.createPanel(new GridLayout(3, 3, 15, 8));
        infoPanel.setOpaque(false);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        addInfoItem(infoPanel, "المشروع:", task.getProjectName());
        addInfoItem(infoPanel, "المسؤول:", 
            task.getAssignedToName() != null ? task.getAssignedToName() : "غير محدد");
        addInfoItem(infoPanel, "الأولوية:", task.getPriorityNameAr());
        addInfoItem(infoPanel, "الحالة:", task.getStatusNameAr());
        addInfoItem(infoPanel, "تاريخ الاستحقاق:", 
            task.getDueDate() != null ? dateFormat.format(task.getDueDate()) : "غير محدد");
        addInfoItem(infoPanel, "نسبة الإكمال:", 
            String.format("%.1f%%", task.getCompletionPercentage()));
        addInfoItem(infoPanel, "أنشأها:", task.getCreatedByName());
        addInfoItem(infoPanel, "تاريخ الإنشاء:", 
            task.getCreatedAt() != null ? dateFormat.format(task.getCreatedAt()) : "-");
        
        // Overdue indicator
        if (task.isOverdue()) {
            JLabel overdueLabel = ArabicUIHelper.createLabel("⚠ متأخرة");
            overdueLabel.setForeground(new Color(244, 67, 54));
            overdueLabel.setFont(ArabicUIHelper.getArabicFontBold());
            infoPanel.add(overdueLabel);
        }
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            JPanel descPanel = ArabicUIHelper.createPanel(new BorderLayout());
            descPanel.setOpaque(false);
            descPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            JLabel descTitleLabel = ArabicUIHelper.createLabel("الوصف:");
            descTitleLabel.setForeground(new Color(100, 100, 100));
            descPanel.add(descTitleLabel, BorderLayout.NORTH);
            
            JTextArea descArea = ArabicUIHelper.createTextArea(2, 30);
            descArea.setText(task.getDescription());
            descArea.setEditable(false);
            descArea.setBackground(new Color(245, 245, 245));
            descPanel.add(descArea, BorderLayout.CENTER);
            
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
    
    private JPanel createActivityPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            "سجل الأنشطة",
            javax.swing.border.TitledBorder.RIGHT,
            javax.swing.border.TitledBorder.TOP,
            ArabicUIHelper.getArabicFont()
        ));
        
        try {
            List<ActivityLog> logs = activityLogDAO.findByEntity(
                ActivityLog.EntityTypes.TASK, task.getId());
            
            String[] columns = {"التاريخ", "المستخدم", "النشاط"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (ActivityLog log : logs) {
                Object[] row = {
                    dateFormat.format(log.getCreatedAt()),
                    log.getUserName(),
                    log.getActionDescription()
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            ArabicUIHelper.applyRTLToTable(table);
            table.setRowHeight(28);
            
            panel.add(ArabicUIHelper.createScrollPane(table), BorderLayout.CENTER);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = ArabicUIHelper.createLabel("خطأ في تحميل سجل الأنشطة");
            panel.add(errorLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }
}
