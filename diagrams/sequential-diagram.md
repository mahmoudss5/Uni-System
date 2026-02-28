# UniSystem - Sequential Diagrams

This document contains sequence diagrams illustrating the main workflows of the UniSystem application.

## 1. User Registration & Authentication Flow

```mermaid
sequenceDiagram
    participant Client as React Client
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepo as User Repository
    participant SecurityConfig as Security Config
    participant JWT as JWT Service
    participant DB as MySQL Database

    Note over Client,DB: User Registration Flow
    Client->>AuthController: POST /api/auth/register
    AuthController->>AuthService: register(UserRequest)
    AuthService->>SecurityConfig: encodePassword()
    SecurityConfig-->>AuthService: hashedPassword
    AuthService->>UserRepo: save(User)
    UserRepo->>DB: INSERT INTO users
    DB-->>UserRepo: User saved
    UserRepo-->>AuthService: User entity
    AuthService->>JWT: generateToken(User)
    JWT-->>AuthService: JWT Token
    AuthService-->>AuthController: AuthResponse (token, user details)
    AuthController-->>Client: 201 Created + AuthResponse

    Note over Client,DB: User Login Flow
    Client->>AuthController: POST /api/auth/login
    AuthController->>AuthService: login(AuthRequest)
    AuthService->>UserRepo: findByEmail(email)
    UserRepo->>DB: SELECT * FROM users WHERE email=?
    DB-->>UserRepo: User data
    UserRepo-->>AuthService: User entity
    AuthService->>SecurityConfig: validatePassword()
    SecurityConfig-->>AuthService: valid/invalid
    alt Valid Credentials
        AuthService->>JWT: generateToken(User)
        JWT-->>AuthService: JWT Token
        AuthService-->>AuthController: AuthResponse (token)
        AuthController-->>Client: 200 OK + AuthResponse
    else Invalid Credentials
        AuthService-->>AuthController: Authentication Failed
        AuthController-->>Client: 401 Unauthorized
    end
```

## 2. Course Management Flow

```mermaid
sequenceDiagram
    participant Client as React Client
    participant CourseController as Course Controller
    participant CourseService as Course Service
    participant CourseRepo as Course Repository
    participant TeacherRepo as Teacher Repository
    participant DeptRepo as Department Repository
    participant Cache as Redis Cache
    participant DB as MySQL Database

    Note over Client,DB: Get All Courses (with Caching)
    Client->>CourseController: GET /api/courses
    CourseController->>CourseService: getAllCourses()
    CourseService->>Cache: checkCache("courses:all")
    alt Cache Hit
        Cache-->>CourseService: Cached courses list
        CourseService-->>CourseController: List<CourseResponse>
    else Cache Miss
        CourseService->>CourseRepo: findAll()
        CourseRepo->>DB: SELECT * FROM courses
        DB-->>CourseRepo: Courses data
        CourseRepo-->>CourseService: List<Course>
        CourseService->>Cache: storeCache("courses:all", courses)
        CourseService-->>CourseController: List<CourseResponse>
    end
    CourseController-->>Client: 200 OK + Courses List

    Note over Client,DB: Create New Course
    Client->>CourseController: POST /api/courses
    CourseController->>CourseService: createCourse(CourseRequest)
    CourseService->>TeacherRepo: findById(teacherId)
    TeacherRepo->>DB: SELECT * FROM teachers WHERE id=?
    DB-->>TeacherRepo: Teacher data
    TeacherRepo-->>CourseService: Teacher entity
    CourseService->>DeptRepo: findById(departmentId)
    DeptRepo->>DB: SELECT * FROM department WHERE id=?
    DB-->>DeptRepo: Department data
    DeptRepo-->>CourseService: Department entity
    CourseService->>CourseRepo: save(Course)
    CourseRepo->>DB: INSERT INTO courses
    DB-->>CourseRepo: Course saved
    CourseRepo-->>CourseService: Course entity
    CourseService->>Cache: invalidateCache("courses:all")
    CourseService-->>CourseController: CourseResponse
    CourseController-->>Client: 201 Created + CourseResponse
```

## 3. Student Enrollment Flow

```mermaid
sequenceDiagram
    participant Client as React Client
    participant EnrollController as Enrolled Course Controller
    participant EnrollService as Enrolled Course Service
    participant StudentRepo as Student Repository
    participant CourseRepo as Course Repository
    participant EnrollRepo as Enrolled Course Repository
    participant AuditService as Audit Log Service
    participant DB as MySQL Database

    Note over Client,DB: Student Enrolls in Course
    Client->>EnrollController: POST /api/enrolled-courses
    EnrollController->>EnrollService: enrollStudent(EnrolledCourseRequest)
    
    EnrollService->>StudentRepo: findById(studentId)
    StudentRepo->>DB: SELECT * FROM students WHERE user_id=?
    DB-->>StudentRepo: Student data
    StudentRepo-->>EnrollService: Student entity
    
    EnrollService->>CourseRepo: findById(courseId)
    CourseRepo->>DB: SELECT * FROM courses WHERE id=?
    DB-->>CourseRepo: Course data
    CourseRepo-->>EnrollService: Course entity
    
    EnrollService->>EnrollRepo: checkExistingEnrollment()
    alt Already Enrolled
        EnrollService-->>EnrollController: Error: Already enrolled
        EnrollController-->>Client: 400 Bad Request
    else Course Full
        EnrollService-->>EnrollController: Error: Course full
        EnrollController-->>Client: 400 Bad Request
    else Can Enroll
        EnrollService->>EnrollRepo: save(EnrolledCourse)
        EnrollRepo->>DB: INSERT INTO enrolled_courses
        DB-->>EnrollRepo: Enrollment saved
        EnrollRepo-->>EnrollService: EnrolledCourse entity
        
        EnrollService->>AuditService: logEnrollment(studentId, courseId)
        AuditService->>DB: INSERT INTO audit_logs
        
        EnrollService-->>EnrollController: EnrolledCourseResponse
        EnrollController-->>Client: 201 Created + EnrollmentResponse
    end
```

## 4. Feedback Submission Flow

```mermaid
sequenceDiagram
    participant Client as React Client
    participant FeedbackController as Feedback Controller
    participant FeedbackService as Feedback Service
    participant UserRepo as User Repository
    participant FeedbackRepo as Feedback Repository
    participant DB as MySQL Database

    Note over Client,DB: Submit Feedback
    Client->>FeedbackController: POST /api/feedbacks
    FeedbackController->>FeedbackService: createFeedback(FeedbackRequest)
    
    FeedbackService->>UserRepo: findById(userId)
    UserRepo->>DB: SELECT * FROM users WHERE id=?
    DB-->>UserRepo: User data
    UserRepo-->>FeedbackService: User entity
    
    FeedbackService->>FeedbackRepo: save(Feedback)
    FeedbackRepo->>DB: INSERT INTO feedbacks
    DB-->>FeedbackRepo: Feedback saved
    FeedbackRepo-->>FeedbackService: Feedback entity
    
    FeedbackService-->>FeedbackController: FeedbackResponse
    FeedbackController-->>Client: 201 Created + FeedbackResponse

    Note over Client,DB: Get All Feedbacks (for display)
    Client->>FeedbackController: GET /api/feedbacks
    FeedbackController->>FeedbackService: getAllFeedbacks()
    FeedbackService->>FeedbackRepo: findAll()
    FeedbackRepo->>DB: SELECT * FROM feedbacks
    DB-->>FeedbackRepo: Feedbacks list
    FeedbackRepo-->>FeedbackService: List<Feedback>
    FeedbackService-->>FeedbackController: List<FeedbackResponse>
    FeedbackController-->>Client: 200 OK + Feedbacks List
```

## 5. Department & Teacher Management Flow

```mermaid
sequenceDiagram
    participant Client as React Client
    participant DeptController as Department Controller
    participant TeacherController as Teacher Controller
    participant DeptService as Department Service
    participant TeacherService as Teacher Service
    participant DeptRepo as Department Repository
    participant TeacherRepo as Teacher Repository
    participant DB as MySQL Database

    Note over Client,DB: Get All Departments
    Client->>DeptController: GET /api/departments
    DeptController->>DeptService: getAllDepartments()
    DeptService->>DeptRepo: findAll()
    DeptRepo->>DB: SELECT * FROM department
    DB-->>DeptRepo: Departments list
    DeptRepo-->>DeptService: List<Department>
    DeptService-->>DeptController: List<DepartmentResponse>
    DeptController-->>Client: 200 OK + Departments

    Note over Client,DB: Create Teacher
    Client->>TeacherController: POST /api/teachers
    TeacherController->>TeacherService: createTeacher(TeacherRequest)
    TeacherService->>TeacherRepo: save(Teacher)
    TeacherRepo->>DB: INSERT INTO teachers
    DB-->>TeacherRepo: Teacher saved
    TeacherRepo-->>TeacherService: Teacher entity
    TeacherService-->>TeacherController: TeacherResponse
    TeacherController-->>Client: 201 Created + TeacherResponse
```

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.4.2
- **Language**: Java 21
- **Security**: Spring Security + JWT (jjwt 0.11.5)
- **Database**: MySQL
- **Cache**: Redis
- **Migration**: Flyway
- **API Documentation**: SpringDoc OpenAPI 2.7.0

### Frontend
- **Framework**: React 19.2.0
- **Build Tool**: Vite 7.3.1
- **Routing**: React Router DOM 7.13.0
- **Styling**: TailwindCSS 4.2.0
- **Animations**: Framer Motion 12.34.3
- **Icons**: Lucide React 0.575.0

### Infrastructure
- **Containerization**: Docker Compose
- **Monitoring**: Spring Boot Actuator
- **Validation**: Jakarta Validation
- **ORM**: Spring Data JPA + Hibernate
