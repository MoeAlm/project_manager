# نظام إدارة المشاريع | Project Management System

نظام متكامل لإدارة المشاريع مبني باستخدام Java Swing وقاعدة بيانات PostgreSQL مع دعم كامل للغة العربية.

A comprehensive project management system built with Java Swing and PostgreSQL database with full Arabic language support.

---

## المميزات | Features

### المميزات الأساسية | Core Features
- ✅ **نظام المصادقة** - تسجيل الدخول والتسجيل مع أدوار متعددة (مدير مشروع - عضو فريق)
- ✅ **إدارة المشاريع** - إنشاء وتعديل وحذف المشاريع مع تتبع الحالة والتقدم
- ✅ **إدارة الفريق** - إضافة أعضاء وتعيين أدوار داخل كل مشروع
- ✅ **إدارة المهام** - تعيين المسؤول، الأولوية، الحالة، وتاريخ الاستحقاق
- ✅ **تتبع التقدم** - نسبة الإكمال لكل مهمة ومشروع
- ✅ **التقارير** - المشاريع المكتملة، المهام المتأخرة، أداء الأعضاء

### المميزات المتقدمة | Advanced Features
- ✅ **الإشعارات** - نظام إشعارات للمستخدمين
- ✅ **سجل الأنشطة** - تتبع جميع العمليات في النظام
- ✅ **البحث والتصفية** - بحث في المشاريع والمهام مع فلاتر متعددة
- ✅ **تصدير التقارير** - تصدير إلى ملفات CSV

### دعم اللغة العربية | Arabic Language Support
- ✅ واجهة مستخدم كاملة باللغة العربية
- ✅ دعم الكتابة من اليمين لليسار (RTL)
- ✅ خطوط عربية واضحة في جميع المكونات

---

## المتطلبات | Requirements

- **Java JDK** 11 أو أحدث
- **PostgreSQL** 12 أو أحدث
- **PostgreSQL JDBC Driver** (postgresql-42.x.x.jar)

---

## التثبيت | Installation

### 1. إعداد قاعدة البيانات | Database Setup

```sql
-- إنشاء قاعدة البيانات
CREATE DATABASE project_management_db;

-- الاتصال بقاعدة البيانات
\c project_management_db

-- تنفيذ ملف المخطط
\i sql/schema.sql
```

أو يمكنك تنفيذ محتوى ملف `sql/schema.sql` مباشرة في pgAdmin أو أي أداة إدارة PostgreSQL.

### 2. تكوين الاتصال | Connection Configuration

عدّل ملف `src/util/DatabaseConnection.java` حسب إعدادات قاعدة البيانات لديك:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/project_management_db";
private static final String USER = "postgres";
private static final String PASSWORD = "your_password";
```

### 3. إضافة مكتبة PostgreSQL JDBC

قم بتحميل PostgreSQL JDBC Driver من:
https://jdbc.postgresql.org/download/

ثم أضفه إلى مسار المشروع (Project Structure > Libraries).

### 4. تشغيل التطبيق | Run Application

```bash
# من IntelliJ IDEA
# اضغط على زر Run أو Shift+F10

# أو من سطر الأوامر
javac -cp ".;postgresql-42.x.x.jar" src/*.java src/**/*.java
java -cp ".;postgresql-42.x.x.jar;src" Main
```

---

## بيانات الدخول الافتراضية | Default Login

| اسم المستخدم | كلمة المرور | الدور |
|-------------|------------|------|
| admin | admin123 | مدير مشروع |

---

## هيكل المشروع | Project Structure

```
src/
├── Main.java                    # نقطة الدخول الرئيسية
├── model/                       # نماذج البيانات
│   ├── User.java
│   ├── Project.java
│   ├── Task.java
│   ├── ProjectMember.java
│   ├── Notification.java
│   └── ActivityLog.java
├── dao/                         # طبقة الوصول للبيانات
│   ├── UserDAO.java
│   ├── ProjectDAO.java
│   ├── TaskDAO.java
│   ├── ProjectMemberDAO.java
│   ├── NotificationDAO.java
│   └── ActivityLogDAO.java
├── controller/                  # المتحكمات
│   ├── AuthController.java
│   ├── ProjectController.java
│   └── TaskController.java
├── view/                        # واجهات المستخدم
│   ├── LoginView.java
│   ├── RegisterView.java
│   ├── MainDashboard.java
│   ├── ProjectsPanel.java
│   ├── TasksPanel.java
│   ├── TeamPanel.java
│   ├── ReportsPanel.java
│   ├── ProjectDialog.java
│   ├── ProjectDetailsDialog.java
│   ├── TaskDialog.java
│   ├── TaskDetailsDialog.java
│   ├── AddMemberDialog.java
│   └── NotificationsDialog.java
└── util/                        # الأدوات المساعدة
    ├── DatabaseConnection.java
    ├── ArabicUIHelper.java
    ├── ValidationHelper.java
    └── SessionManager.java

sql/
└── schema.sql                   # مخطط قاعدة البيانات
```

---

## قاعدة البيانات | Database Schema

### الجداول الرئيسية | Main Tables

| الجدول | الوصف |
|--------|-------|
| `roles` | أدوار المستخدمين في النظام |
| `users` | بيانات المستخدمين |
| `project_status` | حالات المشاريع |
| `projects` | بيانات المشاريع |
| `project_roles` | أدوار أعضاء المشروع |
| `project_members` | أعضاء المشاريع |
| `task_status` | حالات المهام |
| `task_priority` | أولويات المهام |
| `tasks` | بيانات المهام |
| `activity_logs` | سجل الأنشطة |
| `notifications` | الإشعارات |

---

## صلاحيات المستخدمين | User Permissions

### مدير المشروع | Project Manager
- إنشاء وتعديل وحذف المشاريع
- إضافة وإزالة أعضاء الفريق
- إنشاء وتعديل وحذف المهام
- تعيين المهام للأعضاء
- عرض جميع التقارير

### عضو الفريق | Team Member
- عرض المشاريع المشارك فيها
- عرض المهام المعينة له
- تحديث تقدم المهام
- عرض تقارير الأداء الخاصة به

---

## لقطات الشاشة | Screenshots

### شاشة تسجيل الدخول
واجهة بسيطة وواضحة لتسجيل الدخول باللغة العربية.

### لوحة التحكم الرئيسية
عرض إحصائيات سريعة للمشاريع والمهام.

### إدارة المشاريع
جدول يعرض جميع المشاريع مع خيارات الإضافة والتعديل والحذف.

### إدارة المهام
عرض وإدارة المهام مع فلاتر للحالة والأولوية والمشروع.

### التقارير
تقارير المشاريع المكتملة، المهام المتأخرة، وأداء الأعضاء.

---

## التطوير المستقبلي | Future Development

- [ ] إضافة مرفقات للمهام
- [ ] نظام التعليقات على المهام
- [ ] تكامل مع البريد الإلكتروني للإشعارات
- [ ] لوحة Gantt Chart للمشاريع
- [ ] تطبيق ويب مرافق
- [ ] تطبيق موبايل

---

## المساهمة | Contributing

نرحب بمساهماتكم! يرجى فتح Issue أو Pull Request.

---

## الترخيص | License

هذا المشروع مرخص تحت رخصة MIT.

---

## الدعم | Support

للأسئلة والدعم الفني، يرجى فتح Issue في المستودع.

---

**تم التطوير بـ ❤️ باستخدام Java Swing**
