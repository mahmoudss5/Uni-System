-- =============================================================
-- Seed data for permission management
-- =============================================================

-- ---------------------------------------------------------------
-- 1. Permissions (from TeacherPermissions and StudentPermissions enums)
-- ---------------------------------------------------------------
INSERT INTO permissions (id, name, description) VALUES
    (1, 'create_course', 'Allow teacher to create courses'),
    (2, 'update_course', 'Allow teacher to update own courses'),
    (3, 'delete_course', 'Allow teacher to delete own courses'),
    (4, 'unenroll_student', 'Allow teacher to remove students from courses'),
    (5, 'create_announcement', 'Allow teacher to create announcements'),
    (6, 'send_message', 'Allow teacher or student to send messages'),
    (7, 'course_register', 'Allow student to enroll in courses');

-- ---------------------------------------------------------------
-- 2. Role -> Permission mappings
-- ---------------------------------------------------------------
-- Teacher role permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'create_course',
    'update_course',
    'delete_course',
    'unenroll_student',
    'create_announcement',
    'send_message'
)
WHERE r.name = 'Teacher';

-- Student role permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'course_register',
    'send_message'
)
WHERE r.name = 'Student';

-- Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON 1 = 1
WHERE r.name = 'Admin';
