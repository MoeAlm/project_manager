package util;

import java.util.regex.Pattern;

/**
 * فئة مساعدة للتحقق من صحة المدخلات
 * Input Validation Helper Class
 */
public class ValidationHelper {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9+\\-\\s]{8,15}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{3,50}$"
    );
    
    /**
     * التحقق من أن النص غير فارغ
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
    
    /**
     * التحقق من صحة البريد الإلكتروني
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // البريد اختياري
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * التحقق من صحة رقم الهاتف
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // الهاتف اختياري
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * التحقق من صحة اسم المستخدم
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * التحقق من طول كلمة المرور
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * التحقق من طول النص
     */
    public static boolean isValidLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return minLength == 0;
        }
        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * التحقق من أن النص يحتوي على أرقام فقط
     */
    public static boolean isNumeric(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * التحقق من أن الرقم موجب
     */
    public static boolean isPositiveNumber(String text) {
        if (!isNumeric(text)) {
            return false;
        }
        return Double.parseDouble(text) > 0;
    }
    
    /**
     * التحقق من أن النسبة المئوية صحيحة (0-100)
     */
    public static boolean isValidPercentage(String text) {
        if (!isNumeric(text)) {
            return false;
        }
        double value = Double.parseDouble(text);
        return value >= 0 && value <= 100;
    }
    
    /**
     * تنظيف النص من المسافات الزائدة
     */
    public static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * رسائل الأخطاء بالعربية
     */
    public static class ErrorMessages {
        public static final String FIELD_REQUIRED = "هذا الحقل مطلوب";
        public static final String INVALID_EMAIL = "البريد الإلكتروني غير صحيح";
        public static final String INVALID_PHONE = "رقم الهاتف غير صحيح";
        public static final String INVALID_USERNAME = "اسم المستخدم يجب أن يحتوي على 3-50 حرف (أحرف إنجليزية وأرقام وشرطة سفلية فقط)";
        public static final String INVALID_PASSWORD = "كلمة المرور يجب أن تكون 6 أحرف على الأقل";
        public static final String PASSWORDS_NOT_MATCH = "كلمتا المرور غير متطابقتين";
        public static final String INVALID_DATE = "التاريخ غير صحيح";
        public static final String END_DATE_BEFORE_START = "تاريخ الانتهاء يجب أن يكون بعد تاريخ البدء";
        public static final String INVALID_PERCENTAGE = "النسبة المئوية يجب أن تكون بين 0 و 100";
    }
}
