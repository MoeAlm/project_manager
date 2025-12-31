package util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.InputStream;

/**
 * فئة مساعدة لدعم اللغة العربية في واجهة المستخدم
 * Arabic UI Helper Class for RTL Support
 */
public class ArabicUIHelper {
    
    private static Font arabicFont;
    private static Font arabicFontBold;
    private static final String FONT_NAME = "Arial";
    private static final int DEFAULT_FONT_SIZE = 14;
    
    static {
        initializeFont();
    }
    
    private static void initializeFont() {
        try {
            // Try to load Amiri font from resources
            InputStream fontStream = ArabicUIHelper.class.getResourceAsStream("/fonts/Amiri-Regular.ttf");
            if (fontStream != null) {
                arabicFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont((float) DEFAULT_FONT_SIZE);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(arabicFont);
            } else {
                // Fallback to system Arabic fonts
                arabicFont = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
            }
            arabicFontBold = arabicFont.deriveFont(Font.BOLD);
        } catch (Exception e) {
            // Fallback to system fonts that support Arabic
            arabicFont = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
            arabicFontBold = new Font("Arial", Font.BOLD, DEFAULT_FONT_SIZE);
        }
    }
    
    public static Font getArabicFont() {
        return arabicFont;
    }
    
    public static Font getArabicFont(int size) {
        return arabicFont.deriveFont((float) size);
    }
    
    public static Font getArabicFontBold() {
        return arabicFontBold;
    }
    
    public static Font getArabicFontBold(int size) {
        return arabicFontBold.deriveFont((float) size);
    }
    
    /**
     * تطبيق إعدادات RTL على المكون
     */
    public static void applyRTL(JComponent component) {
        component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        component.setFont(arabicFont);
    }
    
    /**
     * تطبيق إعدادات RTL على النافذة
     */
    public static void applyRTL(JFrame frame) {
        frame.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }
    
    /**
     * تطبيق إعدادات RTL على الحوار
     */
    public static void applyRTL(JDialog dialog) {
        dialog.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dialog.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }
    
    /**
     * إنشاء JLabel بدعم العربية
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(arabicFont);
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    /**
     * إنشاء JLabel بدعم العربية مع حجم خط مخصص
     */
    public static JLabel createLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(getArabicFont(fontSize));
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    /**
     * إنشاء JLabel عنوان
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(getArabicFontBold(18));
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * إنشاء JTextField بدعم العربية
     */
    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(arabicFont);
        textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        return textField;
    }
    
    /**
     * إنشاء JTextField بدعم العربية مع عدد أعمدة
     */
    public static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(arabicFont);
        textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        return textField;
    }
    
    /**
     * إنشاء JPasswordField بدعم العربية
     */
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(arabicFont);
        passwordField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        passwordField.setHorizontalAlignment(JTextField.RIGHT);
        return passwordField;
    }
    
    /**
     * إنشاء JPasswordField بدعم العربية مع عدد أعمدة
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(arabicFont);
        passwordField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        passwordField.setHorizontalAlignment(JTextField.RIGHT);
        return passwordField;
    }
    
    /**
     * إنشاء JTextArea بدعم العربية
     */
    public static JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(arabicFont);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    
    /**
     * إنشاء JTextArea بدعم العربية مع أبعاد
     */
    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(arabicFont);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    
    /**
     * إنشاء JButton بدعم العربية
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(arabicFont);
        button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return button;
    }
    
    /**
     * إنشاء JButton أساسي (Primary)
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = createButton(text);
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    /**
     * إنشاء JButton ثانوي (Secondary)
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = createButton(text);
        button.setBackground(new Color(158, 158, 158));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    /**
     * إنشاء JButton نجاح (Success)
     */
    public static JButton createSuccessButton(String text) {
        JButton button = createButton(text);
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    /**
     * إنشاء JButton خطر (Danger)
     */
    public static JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(new Color(244, 67, 54));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    /**
     * إنشاء JComboBox بدعم العربية
     */
    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(arabicFont);
        comboBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return comboBox;
    }
    
    /**
     * إنشاء JComboBox بدعم العربية مع عناصر
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(arabicFont);
        comboBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return comboBox;
    }
    
    /**
     * تطبيق إعدادات RTL على الجدول
     */
    public static void applyRTLToTable(JTable table) {
        table.setFont(arabicFont);
        table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        table.setRowHeight(30);
        
        // تعيين محاذاة الخلايا لليمين
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        rightRenderer.setFont(arabicFont);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }
        
        // تعيين خط رأس الجدول
        JTableHeader header = table.getTableHeader();
        header.setFont(arabicFontBold);
        header.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
    }
    
    /**
     * إنشاء JPanel بدعم العربية
     */
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return panel;
    }
    
    /**
     * إنشاء JPanel بدعم العربية مع تخطيط
     */
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return panel;
    }
    
    /**
     * إنشاء JScrollPane بدعم العربية
     */
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return scrollPane;
    }
    
    /**
     * إنشاء JTabbedPane بدعم العربية
     */
    public static JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(arabicFont);
        tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return tabbedPane;
    }
    
    /**
     * إنشاء JMenu بدعم العربية
     */
    public static JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(arabicFont);
        menu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return menu;
    }
    
    /**
     * إنشاء JMenuItem بدعم العربية
     */
    public static JMenuItem createMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(arabicFont);
        menuItem.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return menuItem;
    }
    
    /**
     * إنشاء JMenuBar بدعم العربية
     */
    public static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(arabicFont);
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return menuBar;
    }
    
    /**
     * إظهار رسالة معلومات
     */
    public static void showInfo(Component parent, String message, String title) {
        UIManager.put("OptionPane.font", arabicFont);
        UIManager.put("OptionPane.messageFont", arabicFont);
        UIManager.put("OptionPane.buttonFont", arabicFont);
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        optionPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dialog.setVisible(true);
    }
    
    /**
     * إظهار رسالة خطأ
     */
    public static void showError(Component parent, String message, String title) {
        UIManager.put("OptionPane.font", arabicFont);
        UIManager.put("OptionPane.messageFont", arabicFont);
        UIManager.put("OptionPane.buttonFont", arabicFont);
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        optionPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dialog.setVisible(true);
    }
    
    /**
     * إظهار رسالة تحذير
     */
    public static void showWarning(Component parent, String message, String title) {
        UIManager.put("OptionPane.font", arabicFont);
        UIManager.put("OptionPane.messageFont", arabicFont);
        UIManager.put("OptionPane.buttonFont", arabicFont);
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
        optionPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dialog.setVisible(true);
    }
    
    /**
     * إظهار رسالة تأكيد
     */
    public static boolean showConfirm(Component parent, String message, String title) {
        UIManager.put("OptionPane.font", arabicFont);
        UIManager.put("OptionPane.messageFont", arabicFont);
        UIManager.put("OptionPane.buttonFont", arabicFont);
        UIManager.put("OptionPane.yesButtonText", "نعم");
        UIManager.put("OptionPane.noButtonText", "لا");
        
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        optionPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dialog.setVisible(true);
        
        Object selectedValue = optionPane.getValue();
        return selectedValue != null && selectedValue.equals(JOptionPane.YES_OPTION);
    }
    
    /**
     * تطبيق نمط عربي على كل واجهة المستخدم
     */
    public static void setArabicLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // تعيين الخطوط الافتراضية
        UIManager.put("Label.font", arabicFont);
        UIManager.put("Button.font", arabicFont);
        UIManager.put("TextField.font", arabicFont);
        UIManager.put("TextArea.font", arabicFont);
        UIManager.put("ComboBox.font", arabicFont);
        UIManager.put("Table.font", arabicFont);
        UIManager.put("TableHeader.font", arabicFontBold);
        UIManager.put("TabbedPane.font", arabicFont);
        UIManager.put("Menu.font", arabicFont);
        UIManager.put("MenuItem.font", arabicFont);
        UIManager.put("MenuBar.font", arabicFont);
        UIManager.put("OptionPane.messageFont", arabicFont);
        UIManager.put("OptionPane.buttonFont", arabicFont);
        UIManager.put("List.font", arabicFont);
        UIManager.put("Tree.font", arabicFont);
        UIManager.put("CheckBox.font", arabicFont);
        UIManager.put("RadioButton.font", arabicFont);
        
        // ترجمة أزرار OptionPane
        UIManager.put("OptionPane.yesButtonText", "نعم");
        UIManager.put("OptionPane.noButtonText", "لا");
        UIManager.put("OptionPane.cancelButtonText", "إلغاء");
        UIManager.put("OptionPane.okButtonText", "موافق");
        
        // ترجمة FileChooser
        UIManager.put("FileChooser.openButtonText", "فتح");
        UIManager.put("FileChooser.saveButtonText", "حفظ");
        UIManager.put("FileChooser.cancelButtonText", "إلغاء");
        UIManager.put("FileChooser.lookInLabelText", "البحث في:");
        UIManager.put("FileChooser.fileNameLabelText", "اسم الملف:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "نوع الملفات:");
    }
}
