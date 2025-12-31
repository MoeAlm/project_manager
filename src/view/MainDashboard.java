package view;

import controller.AuthController;
import dao.*;
import model.*;
import util.ArabicUIHelper;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * لوحة التحكم الرئيسية
 * Main Dashboard View
 */
public class MainDashboard extends JFrame {
    
    private JTabbedPane tabbedPane;
    private JLabel userNameLabel;
    private JLabel roleLabel;
    private JLabel notificationLabel;
    private ProjectDAO projectDAO;
    private TaskDAO taskDAO;
    private NotificationDAO notificationDAO;
    
    // Panels
    private JPanel dashboardPanel;
    private ProjectsPanel projectsPanel;
    private TasksPanel tasksPanel;
    private TeamPanel teamPanel;
    private ReportsPanel reportsPanel;
    
    public MainDashboard() {
        projectDAO = new ProjectDAO();
        taskDAO = new TaskDAO();
        notificationDAO = new NotificationDAO();
        initComponents();
        loadDashboardData();
    }
    
    private void initComponents() {
        setTitle("نظام إدارة المشاريع");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        ArabicUIHelper.applyRTL(this);
        
        // Menu bar
        setJMenuBar(createMenuBar());
        
        // Main panel
        JPanel mainPanel = ArabicUIHelper.createPanel(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = ArabicUIHelper.createTabbedPane();
        tabbedPane.setFont(ArabicUIHelper.getArabicFont(14));
        
        // Dashboard tab
        dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("لوحة التحكم", new ImageIcon(), dashboardPanel);
        
        // Projects tab
        projectsPanel = new ProjectsPanel(this);
        tabbedPane.addTab("المشاريع", new ImageIcon(), projectsPanel);
        
        // Tasks tab
        tasksPanel = new TasksPanel(this);
        tabbedPane.addTab("المهام", new ImageIcon(), tasksPanel);
        
        // Team tab (only for project managers)
        if (SessionManager.getInstance().isProjectManager()) {
            teamPanel = new TeamPanel(this);
            tabbedPane.addTab("الفريق", new ImageIcon(), teamPanel);
        }
        
        // Reports tab
        reportsPanel = new ReportsPanel(this);
        tabbedPane.addTab("التقارير", new ImageIcon(), reportsPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = ArabicUIHelper.createMenuBar();
        
        // File menu
        JMenu fileMenu = ArabicUIHelper.createMenu("ملف");
        
        JMenuItem refreshItem = ArabicUIHelper.createMenuItem("تحديث");
        refreshItem.addActionListener(e -> refreshAll());
        fileMenu.add(refreshItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = ArabicUIHelper.createMenuItem("خروج");
        exitItem.addActionListener(e -> handleLogout());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // Projects menu
        JMenu projectsMenu = ArabicUIHelper.createMenu("المشاريع");
        
        if (SessionManager.getInstance().isProjectManager()) {
            JMenuItem newProjectItem = ArabicUIHelper.createMenuItem("مشروع جديد");
            newProjectItem.addActionListener(e -> {
                tabbedPane.setSelectedIndex(1);
                projectsPanel.showNewProjectDialog();
            });
            projectsMenu.add(newProjectItem);
        }
        
        JMenuItem viewProjectsItem = ArabicUIHelper.createMenuItem("عرض المشاريع");
        viewProjectsItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        projectsMenu.add(viewProjectsItem);
        
        menuBar.add(projectsMenu);
        
        // Tasks menu
        JMenu tasksMenu = ArabicUIHelper.createMenu("المهام");
        
        JMenuItem myTasksItem = ArabicUIHelper.createMenuItem("مهامي");
        myTasksItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        tasksMenu.add(myTasksItem);
        
        menuBar.add(tasksMenu);
        
        // Reports menu
        JMenu reportsMenu = ArabicUIHelper.createMenu("التقارير");
        
        JMenuItem viewReportsItem = ArabicUIHelper.createMenuItem("عرض التقارير");
        viewReportsItem.addActionListener(e -> {
            int tabIndex = SessionManager.getInstance().isProjectManager() ? 4 : 3;
            tabbedPane.setSelectedIndex(tabIndex);
        });
        reportsMenu.add(viewReportsItem);
        
        menuBar.add(reportsMenu);
        
        // Help menu
        JMenu helpMenu = ArabicUIHelper.createMenu("مساعدة");
        
        JMenuItem aboutItem = ArabicUIHelper.createMenuItem("حول البرنامج");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = ArabicUIHelper.createPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 150, 243));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Title
        JLabel titleLabel = ArabicUIHelper.createLabel("نظام إدارة المشاريع", 20);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(ArabicUIHelper.getArabicFontBold(20));
        headerPanel.add(titleLabel, BorderLayout.EAST);
        
        // User info panel
        JPanel userPanel = ArabicUIHelper.createPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        userPanel.setOpaque(false);
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        userNameLabel = ArabicUIHelper.createLabel(currentUser.getFullName());
        userNameLabel.setForeground(Color.WHITE);
        userPanel.add(userNameLabel);
        
        roleLabel = ArabicUIHelper.createLabel("(" + currentUser.getRoleNameAr() + ")");
        roleLabel.setForeground(new Color(200, 230, 255));
        userPanel.add(roleLabel);
        
        // Notification button
        JButton notificationBtn = ArabicUIHelper.createButton("الإشعارات");
        notificationBtn.setForeground(Color.WHITE);
        notificationBtn.setBackground(new Color(25, 118, 210));
        notificationBtn.setBorderPainted(false);
        notificationBtn.addActionListener(e -> showNotifications());
        userPanel.add(notificationBtn);
        
        // Logout button
        JButton logoutBtn = ArabicUIHelper.createButton("تسجيل الخروج");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(211, 47, 47));
        logoutBtn.setBorderPainted(false);
        logoutBtn.addActionListener(e -> handleLogout());
        userPanel.add(logoutBtn);
        
        headerPanel.add(userPanel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = ArabicUIHelper.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 250));
        
        // Welcome message
        JPanel welcomePanel = ArabicUIHelper.createPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        JLabel welcomeLabel = ArabicUIHelper.createLabel("مرحباً، " + currentUser.getFullName(), 18);
        welcomeLabel.setFont(ArabicUIHelper.getArabicFontBold(18));
        welcomePanel.add(welcomeLabel, BorderLayout.EAST);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        
        // Statistics panel
        JPanel statsPanel = ArabicUIHelper.createPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private int totalProjects = 0;
            private int activeProjects = 0;
            private int totalTasks = 0;
            private int overdueTasks = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    int userId = SessionManager.getInstance().getCurrentUserId();
                    
                    List<Project> projects;
                    if (SessionManager.getInstance().isProjectManager()) {
                        projects = projectDAO.findAll();
                    } else {
                        projects = projectDAO.findByMember(userId);
                    }
                    
                    totalProjects = projects.size();
                    activeProjects = (int) projects.stream()
                        .filter(p -> p.getStatusId() == 2)
                        .count();
                    
                    List<Task> tasks;
                    if (SessionManager.getInstance().isProjectManager()) {
                        tasks = taskDAO.findOverdue();
                        overdueTasks = tasks.size();
                        totalTasks = 0;
                        for (Project p : projects) {
                            totalTasks += taskDAO.findByProject(p.getId()).size();
                        }
                    } else {
                        tasks = taskDAO.findByAssignedUser(userId);
                        totalTasks = tasks.size();
                        overdueTasks = (int) tasks.stream()
                            .filter(Task::isOverdue)
                            .count();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void done() {
                updateStatisticsCards(totalProjects, activeProjects, totalTasks, overdueTasks);
            }
        };
        worker.execute();
    }
    
    private void updateStatisticsCards(int totalProjects, int activeProjects, 
                                       int totalTasks, int overdueTasks) {
        JPanel statsPanel = (JPanel) ((JPanel) dashboardPanel.getComponent(1)).getComponent(0);
        if (statsPanel == null) {
            statsPanel = ArabicUIHelper.createPanel(new GridLayout(2, 2, 15, 15));
            statsPanel.setOpaque(false);
        }
        statsPanel.removeAll();
        
        statsPanel.add(createStatCard("إجمالي المشاريع", String.valueOf(totalProjects), 
                                      new Color(33, 150, 243)));
        statsPanel.add(createStatCard("المشاريع النشطة", String.valueOf(activeProjects), 
                                      new Color(76, 175, 80)));
        statsPanel.add(createStatCard("إجمالي المهام", String.valueOf(totalTasks), 
                                      new Color(156, 39, 176)));
        statsPanel.add(createStatCard("المهام المتأخرة", String.valueOf(overdueTasks), 
                                      new Color(244, 67, 54)));
        
        // Find the center component and update
        Component centerComponent = ((BorderLayout) dashboardPanel.getLayout())
            .getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            dashboardPanel.remove(centerComponent);
        }
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = ArabicUIHelper.createPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel valueLabel = ArabicUIHelper.createLabel(value, 36);
        valueLabel.setFont(ArabicUIHelper.getArabicFontBold(36));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        JLabel titleLabel = ArabicUIHelper.createLabel(title);
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void showNotifications() {
        NotificationsDialog dialog = new NotificationsDialog(this);
        dialog.setVisible(true);
    }
    
    private void handleLogout() {
        boolean confirm = ArabicUIHelper.showConfirm(this, 
            "هل تريد تسجيل الخروج؟", "تأكيد");
        if (confirm) {
            AuthController.logout();
            dispose();
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        }
    }
    
    private void showAboutDialog() {
        String message = "نظام إدارة المشاريع\n" +
                        "الإصدار 1.0\n\n" +
                        "تم التطوير باستخدام Java Swing و PostgreSQL\n" +
                        "جميع الحقوق محفوظة © 2024";
        ArabicUIHelper.showInfo(this, message, "حول البرنامج");
    }
    
    public void refreshAll() {
        loadDashboardData();
        if (projectsPanel != null) projectsPanel.refresh();
        if (tasksPanel != null) tasksPanel.refresh();
        if (teamPanel != null) teamPanel.refresh();
        if (reportsPanel != null) reportsPanel.refresh();
    }
    
    public void refreshProjects() {
        if (projectsPanel != null) projectsPanel.refresh();
    }
    
    public void refreshTasks() {
        if (tasksPanel != null) tasksPanel.refresh();
    }
}
