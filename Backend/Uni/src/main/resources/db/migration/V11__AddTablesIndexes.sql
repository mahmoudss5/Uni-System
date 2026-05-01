-- enrolled_courses
ALTER TABLE enrolled_courses
    ADD CONSTRAINT uq_enrolled_student_course UNIQUE (student_id, course_id),
    ADD INDEX idx_enrolled_course_student (course_id, student_id);

-- notifications
ALTER TABLE notifications
    ADD INDEX idx_notifications_user_read_created (user_id, is_read, created_at);

-- messages
ALTER TABLE messages
    ADD INDEX idx_messages_course_created (course_id, created_at),
    ADD INDEX idx_messages_sender_created (sender_id, created_at);

-- audit_logs
ALTER TABLE audit_logs
    ADD INDEX idx_audit_user_created (user_id, created_at),
    ADD INDEX idx_audit_action_created (action, created_at);

-- feedbacks
ALTER TABLE feedbacks
    ADD INDEX idx_feedback_user_created (user_id, created_at),
    ADD INDEX idx_feedback_role_created (role, created_at);

-- courses (optional but likely useful)
ALTER TABLE courses
    ADD INDEX idx_courses_teacher (teacher_id),
    ADD INDEX idx_courses_dep (course_dep);