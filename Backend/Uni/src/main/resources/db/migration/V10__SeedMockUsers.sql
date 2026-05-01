-- =============================================================
-- Mock users seed (1000 users)
-- Plain-text password for every generated user: test1234
-- BCrypt hash below is compatible with Spring BCryptPasswordEncoder
-- =============================================================

SET @mock_password_hash = '$2b$10$SwAc3v.7bnF.hbZkuKPNLONmvJ3wiZqT0t4xtYpUAl3OtdlVOFpT2';

-- Ensure required roles exist.
INSERT IGNORE INTO roles (name) VALUES ('Student'), ('Teacher'), ('Admin');

-- Build sequence 1..1000 in a temp table (works on MySQL versions without CTE support).
CREATE TEMPORARY TABLE tmp_mock_seq (
    n INT NOT NULL PRIMARY KEY
);

INSERT INTO tmp_mock_seq (n)
SELECT
    ones.d + tens.d * 10 + hundreds.d * 100 + 1 AS n
FROM
    (SELECT 0 AS d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) ones
CROSS JOIN
    (SELECT 0 AS d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) tens
CROSS JOIN
    (SELECT 0 AS d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) hundreds
WHERE ones.d + tens.d * 10 + hundreds.d * 100 < 1000;

-- 1) Insert 1000 users (idempotent via unique email/user_name + INSERT IGNORE).
INSERT IGNORE INTO users (user_name, email, password_hash, active, created_at)
SELECT
    CONCAT('mock_user_', LPAD(n, 4, '0')) AS user_name,
    CONCAT('mock.user.', LPAD(n, 4, '0'), '@unisystem.local') AS email,
    @mock_password_hash AS password_hash,
    TRUE AS active,
    TIMESTAMPADD(DAY, -(n % 365), CURRENT_TIMESTAMP) AS created_at
FROM tmp_mock_seq;

-- 2) Create student profiles for users 0001..0800.
INSERT INTO students (user_id, gpa, enrollment_year, total_credits)
SELECT
    u.id,
    ROUND(2.00 + ((n * 37) % 201) / 100, 2) AS gpa,   -- 2.00..4.00
    2021 + (n % 6) AS enrollment_year,                 -- 2021..2026
    15 + ((n * 7) % 136) AS total_credits              -- 15..150
FROM tmp_mock_seq
JOIN users u ON u.user_name = CONCAT('mock_user_', LPAD(n, 4, '0'))
LEFT JOIN students s ON s.user_id = u.id
WHERE n BETWEEN 1 AND 800
  AND s.user_id IS NULL;

-- 3) Create teacher profiles for users 0801..0980.
-- Salary value is AES/GCM-encrypted in the same format expected by SalaryEncryptionConverter.
INSERT INTO teachers (user_id, office_location, salary)
SELECT
    u.id,
    CONCAT('Building ', CHAR(65 + ((n - 801) % 6)), ', Room ', LPAD(100 + ((n * 13) % 300), 3, '0')) AS office_location,
    'SV7t2wb6Jkl0gkWbP/CRTn9CBqy+2CF6Z5pAux7MsKA77GLf' AS salary
FROM tmp_mock_seq
JOIN users u ON u.user_name = CONCAT('mock_user_', LPAD(n, 4, '0'))
LEFT JOIN teachers t ON t.user_id = u.id
WHERE n BETWEEN 801 AND 980
  AND t.user_id IS NULL;

-- 4) Assign Student role to 0001..0800.
INSERT INTO user_roles (user_id, role_id)
SELECT
    u.id,
    r.id
FROM users u
JOIN roles r ON r.name = 'Student'
LEFT JOIN user_roles ur ON ur.user_id = u.id AND ur.role_id = r.id
WHERE u.user_name LIKE 'mock_user_%'
  AND CAST(SUBSTRING(u.user_name, 11) AS UNSIGNED) BETWEEN 1 AND 800
  AND ur.user_id IS NULL;

-- 5) Assign Teacher role to 0801..0980.
INSERT INTO user_roles (user_id, role_id)
SELECT
    u.id,
    r.id
FROM users u
JOIN roles r ON r.name = 'Teacher'
LEFT JOIN user_roles ur ON ur.user_id = u.id AND ur.role_id = r.id
WHERE u.user_name LIKE 'mock_user_%'
  AND CAST(SUBSTRING(u.user_name, 11) AS UNSIGNED) BETWEEN 801 AND 980
  AND ur.user_id IS NULL;

-- 6) Assign Admin role to 0981..1000.
INSERT INTO user_roles (user_id, role_id)
SELECT
    u.id,
    r.id
FROM users u
JOIN roles r ON r.name = 'Admin'
LEFT JOIN user_roles ur ON ur.user_id = u.id AND ur.role_id = r.id
WHERE u.user_name LIKE 'mock_user_%'
  AND CAST(SUBSTRING(u.user_name, 11) AS UNSIGNED) BETWEEN 981 AND 1000
  AND ur.user_id IS NULL;

-- 7) Create mock courses (smaller set than other tables).
-- Uses teachers from mock range and stable course codes for idempotency.
INSERT IGNORE INTO courses (
    course_name,
    course_description,
    course_dep,
    teacher_id,
    credits,
    capacity,
    course_code,
    start_date,
    end_date
)
SELECT
    CONCAT('Mock Course ', LPAD(n, 3, '0')) AS course_name,
    CONCAT('Generated mock course description for module ', LPAD(n, 3, '0')) AS course_description,
    ((n - 1) % 3) + 1 AS course_dep,
    t.user_id AS teacher_id,
    2 + (n % 3) AS credits,
    25 + (n % 76) AS capacity,
    CONCAT('MCK', LPAD(n, 4, '0')) AS course_code,
    DATE_ADD(CURDATE(), INTERVAL -((n % 180) + 30) DAY) AS start_date,
    DATE_ADD(CURDATE(), INTERVAL (120 + (n % 120)) DAY) AS end_date
FROM tmp_mock_seq seq
JOIN teachers t
  ON t.user_id = (
      SELECT u.id
      FROM users u
      WHERE u.user_name = CONCAT('mock_user_', LPAD(800 + seq.n, 4, '0'))
  )
WHERE seq.n BETWEEN 1 AND 120;

-- 8) Create around 1000 enrollments for mock students -> mock courses.
-- Uses deterministic mapping + anti-join for rerun safety.
INSERT INTO enrolled_courses (student_id, course_id)
SELECT
    su.id AS student_id,
    c.id AS course_id
FROM tmp_mock_seq seq
JOIN users su
  ON su.user_name = CONCAT('mock_user_', LPAD(((seq.n - 1) % 800) + 1, 4, '0'))
JOIN courses c
  ON c.course_code = CONCAT('MCK', LPAD(((seq.n * 7 - 1) % 120) + 1, 4, '0'))
LEFT JOIN enrolled_courses ec
  ON ec.student_id = su.id
 AND ec.course_id = c.id
WHERE ec.id IS NULL;

-- 9) Create around 1000 audit logs for mock users.
-- Uniqueness for reruns is enforced through a stable details token.
INSERT INTO audit_logs (user_id, action, details, ip_address)
SELECT
    u.id AS user_id,
    CASE (seq.n % 6)
        WHEN 0 THEN 'LOGIN_SUCCESS'
        WHEN 1 THEN 'PROFILE_UPDATE'
        WHEN 2 THEN 'COURSE_VIEW'
        WHEN 3 THEN 'COURSE_REGISTER'
        WHEN 4 THEN 'ANNOUNCEMENT_READ'
        ELSE 'LOGOUT'
    END AS action,
    CONCAT('MOCK_AUDIT_', LPAD(seq.n, 4, '0'), ': generated activity for ', u.user_name) AS details,
    CONCAT(
        '192.168.',
        LPAD((seq.n % 200), 1, '0'),
        '.',
        LPAD((seq.n % 250) + 1, 1, '0')
    ) AS ip_address
FROM tmp_mock_seq seq
JOIN users u
  ON u.user_name = CONCAT('mock_user_', LPAD(((seq.n - 1) % 1000) + 1, 4, '0'))
LEFT JOIN audit_logs al
  ON al.details = CONCAT('MOCK_AUDIT_', LPAD(seq.n, 4, '0'), ': generated activity for ', u.user_name)
WHERE al.id IS NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_mock_seq;
