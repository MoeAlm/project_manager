-- نظام إدارة المشاريع - بيانات أولية للاختبار
-- Project Management System - Seed Data for Testing

-- ملاحظة: تأكد من تشغيل schema.sql أولاً
-- Note: Make sure to run schema.sql first

-- =====================================================
-- المستخدمون (Users)
-- =====================================================

-- مديرو المشاريع
INSERT INTO users (username, password, full_name, email, phone, role_id, is_active) VALUES
('ahmed.ali', 'password123', 'أحمد علي محمد', 'ahmed.ali@company.com', '0501234567', 1, TRUE),
('sara.hassan', 'password123', 'سارة حسن أحمد', 'sara.hassan@company.com', '0509876543', 1, TRUE),
('mohammed.omar', 'password123', 'محمد عمر خالد', 'mohammed.omar@company.com', '0507654321', 1, TRUE)
ON CONFLICT (username) DO NOTHING;

-- أعضاء الفريق
INSERT INTO users (username, password, full_name, email, phone, role_id, is_active) VALUES
('fatima.salem', 'password123', 'فاطمة سالم عبدالله', 'fatima.salem@company.com', '0502345678', 2, TRUE),
('khalid.ibrahim', 'password123', 'خالد إبراهيم محمد', 'khalid.ibrahim@company.com', '0503456789', 2, TRUE),
('nora.abdullah', 'password123', 'نورا عبدالله أحمد', 'nora.abdullah@company.com', '0504567890', 2, TRUE),
('youssef.mohammed', 'password123', 'يوسف محمد علي', 'youssef.mohammed@company.com', '0505678901', 2, TRUE),
('layla.ahmad', 'password123', 'ليلى أحمد حسن', 'layla.ahmad@company.com', '0506789012', 2, TRUE),
('omar.khalid', 'password123', 'عمر خالد سعيد', 'omar.khalid@company.com', '0507890123', 2, TRUE),
('maha.saleh', 'password123', 'مها صالح عبدالرحمن', 'maha.saleh@company.com', '0508901234', 2, TRUE),
('abdullah.nasser', 'password123', 'عبدالله ناصر محمود', 'abdullah.nasser@company.com', '0509012345', 2, TRUE),
('aisha.fahad', 'password123', 'عائشة فهد سليمان', 'aisha.fahad@company.com', '0500123456', 2, TRUE)
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- المشاريع (Projects)
-- =====================================================

INSERT INTO projects (project_name, description, start_date, end_date, status_id, manager_id, completion_percentage) VALUES
(
    'تطوير تطبيق الموبايل',
    'تطوير تطبيق موبايل متكامل لإدارة المبيعات والعملاء مع واجهة مستخدم حديثة ودعم كامل للغة العربية',
    '2024-01-15',
    '2024-06-30',
    2, -- قيد التنفيذ
    2, -- ahmed.ali
    45.50
),
(
    'نظام إدارة المخزون',
    'بناء نظام متكامل لإدارة المخزون والمشتريات مع تقارير تحليلية وإشعارات تلقائية',
    '2024-02-01',
    '2024-08-31',
    2, -- قيد التنفيذ
    3, -- sara.hassan
    30.00
),
(
    'موقع التجارة الإلكترونية',
    'تطوير منصة تجارة إلكترونية شاملة مع نظام دفع آمن وإدارة الطلبات والشحن',
    '2024-03-10',
    '2024-12-31',
    2, -- قيد التنفيذ
    4, -- mohammed.omar
    15.75
),
(
    'تحديث البنية التحتية',
    'ترقية البنية التحتية للخوادم وتحسين الأمان والأداء',
    '2023-11-01',
    '2024-01-31',
    4, -- مكتمل
    2, -- ahmed.ali
    100.00
),
(
    'نظام إدارة الموارد البشرية',
    'تطوير نظام شامل لإدارة الموارد البشرية يشمل الرواتب والإجازات والحضور',
    '2024-04-01',
    '2024-10-31',
    1, -- لم يبدأ
    3, -- sara.hassan
    0.00
),
(
    'تطبيق خدمة العملاء',
    'بناء نظام متكامل لخدمة العملاء مع دعم الدردشة المباشرة والتذاكر',
    '2024-01-20',
    '2024-05-15',
    3, -- متوقف
    4, -- mohammed.omar
    60.00
);

-- =====================================================
-- أعضاء المشاريع (Project Members)
-- =====================================================

-- تطوير تطبيق الموبايل
INSERT INTO project_members (project_id, user_id, project_role_id) VALUES
(1, 5, 2),  -- fatima.salem - مطور
(1, 6, 2),  -- khalid.ibrahim - مطور
(1, 7, 3),  -- nora.abdullah - مصمم
(1, 8, 4),  -- youssef.mohammed - مختبر
(1, 9, 5)   -- layla.ahmad - محلل
ON CONFLICT (project_id, user_id) DO NOTHING;

-- نظام إدارة المخزون
INSERT INTO project_members (project_id, user_id, project_role_id) VALUES
(2, 10, 1), -- omar.khalid - قائد الفريق
(2, 11, 2), -- maha.saleh - مطور
(2, 12, 2), -- abdullah.nasser - مطور
(2, 13, 4)  -- aisha.fahad - مختبر
ON CONFLICT (project_id, user_id) DO NOTHING;

-- موقع التجارة الإلكترونية
INSERT INTO project_members (project_id, user_id, project_role_id) VALUES
(3, 5, 2),  -- fatima.salem - مطور
(3, 7, 3),  -- nora.abdullah - مصمم
(3, 9, 5),  -- layla.ahmad - محلل
(3, 11, 2)  -- maha.saleh - مطور
ON CONFLICT (project_id, user_id) DO NOTHING;

-- تحديث البنية التحتية
INSERT INTO project_members (project_id, user_id, project_role_id) VALUES
(4, 6, 1),  -- khalid.ibrahim - قائد الفريق
(4, 10, 2)  -- omar.khalid - مطور
ON CONFLICT (project_id, user_id) DO NOTHING;

-- نظام إدارة الموارد البشرية
INSERT INTO project_members (project_id, user_id, project_role_id) VALUES
(5, 8, 5),  -- youssef.mohammed - محلل
(5, 12, 2)  -- abdullah.nasser - مطور
ON CONFLICT (project_id, user_id) DO NOTHING;

-- تطبيق خدمة العملاء
INSERT INTO project_members (project_id, user_id, project_role_id) VALUES
(6, 13, 2), -- aisha.fahad - مطور
(6, 7, 3)   -- nora.abdullah - مصمم
ON CONFLICT (project_id, user_id) DO NOTHING;

-- =====================================================
-- المهام (Tasks)
-- =====================================================

-- مهام مشروع تطوير تطبيق الموبايل
INSERT INTO tasks (project_id, task_name, description, assigned_to, priority_id, status_id, due_date, completion_percentage, created_by) VALUES
(1, 'تصميم واجهة المستخدم الرئيسية', 'تصميم الشاشة الرئيسية وشاشات التنقل الأساسية', 7, 3, 4, '2024-02-15', 100.00, 2),
(1, 'تطوير نظام المصادقة', 'بناء نظام تسجيل الدخول والتسجيل مع التحقق الثنائي', 5, 4, 4, '2024-02-28', 100.00, 2),
(1, 'تطوير واجهة إدارة العملاء', 'بناء الواجهات الخاصة بإضافة وتعديل بيانات العملاء', 6, 3, 2, '2024-03-31', 70.00, 2),
(1, 'تطوير نظام التقارير', 'إنشاء تقارير المبيعات والإحصائيات', 5, 2, 2, '2024-04-15', 40.00, 2),
(1, 'اختبار الأداء', 'إجراء اختبارات الأداء والتحميل', 8, 3, 1, '2024-05-01', 0.00, 2),
(1, 'كتابة التوثيق الفني', 'إعداد التوثيق الفني الكامل للنظام', 9, 2, 1, '2024-05-15', 10.00, 2),
(1, 'اختبار التكامل', 'اختبار التكامل بين جميع الوحدات', 8, 3, 1, '2024-05-30', 0.00, 2);

-- مهام نظام إدارة المخزون
INSERT INTO tasks (project_id, task_name, description, assigned_to, priority_id, status_id, due_date, completion_percentage, created_by) VALUES
(2, 'تحليل المتطلبات', 'تحليل وتوثيق جميع متطلبات النظام', 10, 4, 4, '2024-02-15', 100.00, 3),
(2, 'تصميم قاعدة البيانات', 'تصميم مخطط قاعدة البيانات الكامل', 10, 4, 4, '2024-02-28', 100.00, 3),
(2, 'تطوير وحدة المشتريات', 'بناء نظام إدارة المشتريات والموردين', 11, 3, 2, '2024-04-30', 50.00, 3),
(2, 'تطوير وحدة المخزون', 'بناء نظام تتبع المخزون والكميات', 12, 3, 2, '2024-05-31', 35.00, 3),
(2, 'تطوير نظام الإشعارات', 'إنشاء نظام إشعارات للمخزون المنخفض', 11, 2, 1, '2024-06-15', 0.00, 3),
(2, 'اختبار النظام', 'إجراء الاختبارات الشاملة', 13, 3, 1, '2024-07-31', 0.00, 3);

-- مهام موقع التجارة الإلكترونية
INSERT INTO tasks (project_id, task_name, description, assigned_to, priority_id, status_id, due_date, completion_percentage, created_by) VALUES
(3, 'تصميم الهوية البصرية', 'تصميم الشعار والألوان والخطوط', 7, 4, 4, '2024-03-25', 100.00, 4),
(3, 'تطوير صفحة المنتجات', 'بناء صفحة عرض المنتجات والفلترة', 5, 3, 2, '2024-05-15', 25.00, 4),
(3, 'تطوير سلة التسوق', 'بناء نظام سلة التسوق والدفع', 11, 4, 1, '2024-06-30', 0.00, 4),
(3, 'تكامل بوابة الدفع', 'ربط النظام ببوابات الدفع الإلكتروني', 5, 4, 1, '2024-07-31', 0.00, 4),
(3, 'تطوير لوحة التحكم', 'بناء لوحة تحكم البائع', 11, 3, 1, '2024-08-31', 0.00, 4);

-- مهام تحديث البنية التحتية (مكتمل)
INSERT INTO tasks (project_id, task_name, description, assigned_to, priority_id, status_id, due_date, completion_percentage, created_by) VALUES
(4, 'ترقية الخوادم', 'ترقية نظام التشغيل والبرمجيات', 6, 4, 4, '2023-11-30', 100.00, 2),
(4, 'تحسين الأمان', 'تطبيق أحدث معايير الأمان', 10, 4, 4, '2023-12-15', 100.00, 2),
(4, 'إعداد النسخ الاحتياطي', 'إعداد نظام النسخ الاحتياطي التلقائي', 6, 3, 4, '2024-01-15', 100.00, 2),
(4, 'اختبار الأداء', 'قياس وتحسين أداء الخوادم', 10, 3, 4, '2024-01-31', 100.00, 2);

-- مهام نظام إدارة الموارد البشرية (لم يبدأ)
INSERT INTO tasks (project_id, task_name, description, assigned_to, priority_id, status_id, due_date, completion_percentage, created_by) VALUES
(5, 'جمع المتطلبات', 'جمع وتوثيق متطلبات النظام من الإدارة', 8, 4, 1, '2024-04-30', 0.00, 3),
(5, 'تصميم النظام', 'تصميم البنية المعمارية للنظام', 8, 4, 1, '2024-05-31', 0.00, 3),
(5, 'تطوير وحدة الرواتب', 'بناء نظام حساب وإدارة الرواتب', 12, 3, 1, '2024-07-31', 0.00, 3);

-- مهام تطبيق خدمة العملاء (متوقف)
INSERT INTO tasks (project_id, task_name, description, assigned_to, priority_id, status_id, due_date, completion_percentage, created_by) VALUES
(6, 'تطوير نظام التذاكر', 'بناء نظام إدارة تذاكر الدعم', 13, 3, 4, '2024-02-28', 100.00, 4),
(6, 'تطوير الدردشة المباشرة', 'بناء نظام الدردشة الفورية', 13, 4, 2, '2024-03-31', 80.00, 4),
(6, 'تصميم واجهة العميل', 'تصميم واجهة بوابة العملاء', 7, 3, 3, '2024-04-15', 40.00, 4),
(6, 'تطوير نظام التقييم', 'إضافة نظام تقييم الخدمة', 13, 2, 5, '2024-05-01', 20.00, 4);

-- =====================================================
-- الإشعارات (Notifications)
-- =====================================================

INSERT INTO notifications (user_id, title, message, is_read, notification_type, related_entity_type, related_entity_id) VALUES
(5, 'مهمة جديدة', 'تم تعيين مهمة "تطوير نظام التقارير" لك', FALSE, 'TASK_ASSIGNED', 'TASK', 4),
(6, 'مهمة جديدة', 'تم تعيين مهمة "تطوير واجهة إدارة العملاء" لك', FALSE, 'TASK_ASSIGNED', 'TASK', 3),
(8, 'موعد قريب', 'مهمة "اختبار الأداء" تستحق خلال 5 أيام', FALSE, 'DUE_DATE_APPROACHING', 'TASK', 5),
(7, 'تحديث المشروع', 'تم تحديث حالة مشروع "تطوير تطبيق الموبايل"', TRUE, 'PROJECT_UPDATE', 'PROJECT', 1),
(11, 'مهمة جديدة', 'تم تعيين مهمة "تطوير وحدة المشتريات" لك', FALSE, 'TASK_ASSIGNED', 'TASK', 9),
(13, 'مهمة مكتملة', 'تم إكمال مهمة "تطوير نظام التذاكر"', TRUE, 'TASK_COMPLETED', 'TASK', 25),
(10, 'إضافة للمشروع', 'تمت إضافتك لمشروع "نظام إدارة المخزون"', TRUE, 'ADDED_TO_PROJECT', 'PROJECT', 2),
(5, 'موعد متأخر', 'مهمة "تطوير صفحة المنتجات" متأخرة عن الموعد', FALSE, 'OVERDUE', 'TASK', 16);

-- =====================================================
-- سجل الأنشطة (Activity Logs)
-- =====================================================

INSERT INTO activity_logs (user_id, action_type, action_description, entity_type, entity_id) VALUES
(2, 'CREATE', 'إنشاء مشروع جديد: تطوير تطبيق الموبايل', 'PROJECT', 1),
(2, 'CREATE', 'إنشاء مهمة: تصميم واجهة المستخدم الرئيسية', 'TASK', 1),
(2, 'ASSIGN', 'تعيين مهمة "تصميم واجهة المستخدم الرئيسية" إلى نورا عبدالله', 'TASK', 1),
(7, 'UPDATE', 'تحديث حالة المهمة إلى "مكتملة"', 'TASK', 1),
(3, 'CREATE', 'إنشاء مشروع جديد: نظام إدارة المخزون', 'PROJECT', 2),
(3, 'ADD_MEMBER', 'إضافة عمر خالد إلى المشروع', 'PROJECT', 2),
(10, 'UPDATE', 'تحديث نسبة إكمال المهمة إلى 50%', 'TASK', 9),
(4, 'CREATE', 'إنشاء مشروع جديد: موقع التجارة الإلكترونية', 'PROJECT', 3),
(2, 'UPDATE', 'تحديث حالة المشروع إلى "مكتمل"', 'PROJECT', 4),
(13, 'UPDATE', 'تحديث حالة المهمة إلى "مكتملة"', 'TASK', 25),
(5, 'UPDATE', 'تحديث نسبة إكمال المهمة إلى 70%', 'TASK', 3),
(11, 'UPDATE', 'تحديث نسبة إكمال المهمة إلى 50%', 'TASK', 9),
(6, 'UPDATE', 'تحديث نسبة إكمال المهمة إلى 70%', 'TASK', 3),
(4, 'UPDATE', 'تحديث حالة المشروع إلى "متوقف"', 'PROJECT', 6),
(7, 'UPDATE', 'تحديث حالة المهمة إلى "قيد المراجعة"', 'TASK', 27);

-- =====================================================
-- ملاحظات مهمة
-- =====================================================

-- جميع كلمات المرور للمستخدمين هي: password123
-- يمكنك تسجيل الدخول بأي من المستخدمين التاليين:
-- 
-- مديرو المشاريع:
-- - admin / admin123 (مدير النظام)
-- - ahmed.ali / password123
-- - sara.hassan / password123
-- - mohammed.omar / password123
--
-- أعضاء الفريق:
-- - fatima.salem / password123
-- - khalid.ibrahim / password123
-- - nora.abdullah / password123
-- وغيرهم...
