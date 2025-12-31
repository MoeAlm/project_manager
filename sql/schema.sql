-- نظام إدارة المشاريع - مخطط قاعدة البيانات
-- Project Management System - Database Schema

-- إنشاء قاعدة البيانات
-- CREATE DATABASE project_management_db;

-- جدول الأدوار
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_name_ar VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول المستخدمين
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role_id INTEGER REFERENCES roles(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول حالات المشروع
CREATE TABLE IF NOT EXISTS project_status (
    id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    status_name_ar VARCHAR(50) NOT NULL,
    color VARCHAR(20) DEFAULT '#808080'
);

-- جدول المشاريع
CREATE TABLE IF NOT EXISTS projects (
    id SERIAL PRIMARY KEY,
    project_name VARCHAR(200) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    status_id INTEGER REFERENCES project_status(id) DEFAULT 1,
    manager_id INTEGER REFERENCES users(id),
    completion_percentage DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول أدوار أعضاء المشروع
CREATE TABLE IF NOT EXISTS project_roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_name_ar VARCHAR(50) NOT NULL
);

-- جدول أعضاء المشروع
CREATE TABLE IF NOT EXISTS project_members (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    project_role_id INTEGER REFERENCES project_roles(id),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, user_id)
);

-- جدول حالات المهام
CREATE TABLE IF NOT EXISTS task_status (
    id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    status_name_ar VARCHAR(50) NOT NULL,
    color VARCHAR(20) DEFAULT '#808080'
);

-- جدول أولويات المهام
CREATE TABLE IF NOT EXISTS task_priority (
    id SERIAL PRIMARY KEY,
    priority_name VARCHAR(50) NOT NULL UNIQUE,
    priority_name_ar VARCHAR(50) NOT NULL,
    priority_level INTEGER NOT NULL,
    color VARCHAR(20) DEFAULT '#808080'
);

-- جدول المهام
CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
    task_name VARCHAR(200) NOT NULL,
    description TEXT,
    assigned_to INTEGER REFERENCES users(id),
    priority_id INTEGER REFERENCES task_priority(id) DEFAULT 2,
    status_id INTEGER REFERENCES task_status(id) DEFAULT 1,
    due_date DATE,
    completion_percentage DECIMAL(5,2) DEFAULT 0.00,
    created_by INTEGER REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول سجل الأنشطة
CREATE TABLE IF NOT EXISTS activity_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    action_type VARCHAR(50) NOT NULL,
    action_description TEXT,
    entity_type VARCHAR(50),
    entity_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول الإشعارات
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    notification_type VARCHAR(50),
    related_entity_type VARCHAR(50),
    related_entity_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- إدخال البيانات الأولية

-- الأدوار الأساسية
INSERT INTO roles (role_name, role_name_ar, description) VALUES
('PROJECT_MANAGER', 'مدير مشروع', 'مدير المشروع - صلاحيات كاملة'),
('TEAM_MEMBER', 'عضو فريق', 'عضو في فريق العمل')
ON CONFLICT (role_name) DO NOTHING;

-- حالات المشروع
INSERT INTO project_status (status_name, status_name_ar, color) VALUES
('NOT_STARTED', 'لم يبدأ', '#9E9E9E'),
('IN_PROGRESS', 'قيد التنفيذ', '#2196F3'),
('ON_HOLD', 'متوقف', '#FF9800'),
('COMPLETED', 'مكتمل', '#4CAF50'),
('CANCELLED', 'ملغي', '#F44336')
ON CONFLICT (status_name) DO NOTHING;

-- أدوار أعضاء المشروع
INSERT INTO project_roles (role_name, role_name_ar) VALUES
('LEADER', 'قائد الفريق'),
('DEVELOPER', 'مطور'),
('DESIGNER', 'مصمم'),
('TESTER', 'مختبر'),
('ANALYST', 'محلل')
ON CONFLICT (role_name) DO NOTHING;

-- حالات المهام
INSERT INTO task_status (status_name, status_name_ar, color) VALUES
('TODO', 'للتنفيذ', '#9E9E9E'),
('IN_PROGRESS', 'قيد التنفيذ', '#2196F3'),
('REVIEW', 'قيد المراجعة', '#FF9800'),
('COMPLETED', 'مكتملة', '#4CAF50'),
('BLOCKED', 'معلقة', '#F44336')
ON CONFLICT (status_name) DO NOTHING;

-- أولويات المهام
INSERT INTO task_priority (priority_name, priority_name_ar, priority_level, color) VALUES
('LOW', 'منخفضة', 1, '#4CAF50'),
('MEDIUM', 'متوسطة', 2, '#FF9800'),
('HIGH', 'عالية', 3, '#F44336'),
('CRITICAL', 'حرجة', 4, '#9C27B0')
ON CONFLICT (priority_name) DO NOTHING;

-- مستخدم افتراضي (مدير النظام)
-- كلمة المرور: admin123
INSERT INTO users (username, password, full_name, email, role_id, is_active) VALUES
('admin', 'admin123', 'مدير النظام', 'admin@system.com', 1, TRUE)
ON CONFLICT (username) DO NOTHING;

-- إنشاء الفهارس لتحسين الأداء
CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX IF NOT EXISTS idx_tasks_status_id ON tasks(status_id);
CREATE INDEX IF NOT EXISTS idx_project_members_project_id ON project_members(project_id);
CREATE INDEX IF NOT EXISTS idx_project_members_user_id ON project_members(user_id);
CREATE INDEX IF NOT EXISTS idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
