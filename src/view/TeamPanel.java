package view;

import dao.ProjectDAO;
import dao.ProjectMemberDAO;
import dao.UserDAO;
import model.Project;
import model.ProjectMember;
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
 * لوحة إدارة الفريق
 * Team Management Panel
 */
public class TeamPanel extends JPanel {
    
    private MainDashboard parent;
    private JTable membersTable;
    private DefaultTableModel tableModel;
    private JComboBox<Project> projectFilter;
    private ProjectDAO projectDAO;
    private ProjectMemberDAO memberDAO;
    private UserDAO userDAO;
    private List<ProjectMember> currentMembers;
    private Project selectedProject;
    
    public TeamPanel(MainDashboard parent) {
        this.parent = parent;
        this.projectDAO = new ProjectDAO();
        this.memberDAO = new ProjectMemberDAO();
        this.userDAO = new UserDAO();
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
        JLabel titleLabel = ArabicUIHelper.createTitleLabel("إدارة الفريق");
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setOpaque(false);
        
        filterPanel.add(ArabicUIHelper.createLabel("المشروع:"));
        projectFilter = ArabicUIHelper.createComboBox();
        projectFilter.setPreferredSize(new Dimension(250, 30));
        projectFilter.addActionListener(e -> {
            selectedProject = (Project) projectFilter.getSelectedItem();
            if (selectedProject != null) {
                loadMembers(selectedProject.getId());
            }
        });
        filterPanel.add(projectFilter);
        
        panel.add(filterPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Table columns
        String[] columns = {"#", "الاسم", "اسم المستخدم", "الدور في المشروع", 
                           "تاريخ الانضمام", "المهام الكلية", "المهام المكتملة", "نسبة الإنجاز"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        membersTable = new JTable(tableModel);
        ArabicUIHelper.applyRTLToTable(membersTable);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        membersTable.setRowHeight(35);
        
        // Column widths
        membersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        membersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        membersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        membersTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        membersTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        membersTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        membersTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        membersTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        JScrollPane scrollPane = ArabicUIHelper.createScrollPane(membersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);
        
        JButton addBtn = ArabicUIHelper.createSuccessButton("إضافة عضو");
        addBtn.setPreferredSize(new Dimension(120, 35));
        addBtn.addActionListener(e -> addMember());
        panel.add(addBtn);
        
        JButton changeRoleBtn = ArabicUIHelper.createPrimaryButton("تغيير الدور");
        changeRoleBtn.setPreferredSize(new Dimension(120, 35));
        changeRoleBtn.addActionListener(e -> changeRole());
        panel.add(changeRoleBtn);
        
        JButton removeBtn = ArabicUIHelper.createDangerButton("إزالة عضو");
        removeBtn.setPreferredSize(new Dimension(120, 35));
        removeBtn.addActionListener(e -> removeMember());
        panel.add(removeBtn);
        
        JButton refreshBtn = ArabicUIHelper.createSecondaryButton("تحديث");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.addActionListener(e -> refresh());
        panel.add(refreshBtn);
        
        return panel;
    }
    
    private void loadProjects() {
        SwingWorker<List<Project>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Project> doInBackground() throws Exception {
                return projectDAO.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Project> projects = get();
                    projectFilter.removeAllItems();
                    for (Project p : projects) {
                        projectFilter.addItem(p);
                    }
                    if (!projects.isEmpty()) {
                        selectedProject = projects.get(0);
                        loadMembers(selectedProject.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void loadMembers(int projectId) {
        SwingWorker<List<ProjectMember>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProjectMember> doInBackground() throws Exception {
                return memberDAO.findByProjectWithPerformance(projectId);
            }
            
            @Override
            protected void done() {
                try {
                    currentMembers = get();
                    updateTable(currentMembers);
                } catch (Exception e) {
                    e.printStackTrace();
                    ArabicUIHelper.showError(TeamPanel.this, 
                        "خطأ في تحميل الأعضاء: " + e.getMessage(), "خطأ");
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<ProjectMember> members) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        int index = 1;
        for (ProjectMember member : members) {
            Object[] row = {
                index++,
                member.getUserFullName(),
                member.getUserName(),
                member.getProjectRoleNameAr(),
                member.getJoinedAt() != null ? dateFormat.format(member.getJoinedAt()) : "-",
                member.getTotalTasks(),
                member.getCompletedTasks(),
                String.format("%.1f%%", member.getCompletionRate())
            };
            tableModel.addRow(row);
        }
    }
    
    private void addMember() {
        if (selectedProject == null) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مشروع أولاً", "تنبيه");
            return;
        }
        
        AddMemberDialog dialog = new AddMemberDialog(parent, selectedProject);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadMembers(selectedProject.getId());
        }
    }
    
    private void changeRole() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار عضو", "تنبيه");
            return;
        }
        
        if (selectedProject == null) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار مشروع أولاً", "تنبيه");
            return;
        }
        
        ProjectMember member = currentMembers.get(selectedRow);
        
        try {
            List<Object[]> roles = memberDAO.getAllProjectRoles();
            String[] roleNames = roles.stream()
                .map(r -> (String) r[2])
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(
                this,
                "اختر الدور الجديد:",
                "تغيير الدور",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roleNames,
                member.getProjectRoleNameAr()
            );
            
            if (selected != null) {
                int newRoleId = -1;
                for (Object[] role : roles) {
                    if (role[2].equals(selected)) {
                        newRoleId = (int) role[0];
                        break;
                    }
                }
                
                if (newRoleId > 0) {
                    memberDAO.updateRole(member.getId(), newRoleId);
                    ArabicUIHelper.showInfo(this, "تم تغيير الدور بنجاح", "نجاح");
                    loadMembers(selectedProject.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ArabicUIHelper.showError(this, "خطأ في تغيير الدور: " + e.getMessage(), "خطأ");
        }
    }
    
    private void removeMember() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            ArabicUIHelper.showWarning(this, "الرجاء اختيار عضو", "تنبيه");
            return;
        }
        
        ProjectMember member = currentMembers.get(selectedRow);
        boolean confirm = ArabicUIHelper.showConfirm(this, 
            "هل تريد إزالة العضو: " + member.getUserFullName() + " من المشروع؟", "تأكيد");
        
        if (confirm) {
            try {
                memberDAO.removeMember(member.getId());
                ArabicUIHelper.showInfo(this, "تم إزالة العضو بنجاح", "نجاح");
                loadMembers(selectedProject.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                ArabicUIHelper.showError(this, "خطأ في إزالة العضو: " + e.getMessage(), "خطأ");
            }
        }
    }
    
    public void refresh() {
        loadProjects();
    }
}
