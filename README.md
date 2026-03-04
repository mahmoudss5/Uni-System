# UniSystem - University Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.0-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9.3-blue.svg)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

A comprehensive, full-stack university management system built with modern technologies for managing students, teachers, courses, departments, enrollments, announcements, feedback, and upcoming events.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

UniSystem is a modern, full-stack university management system designed to streamline academic operations. It provides a comprehensive platform for managing students, teachers, courses, departments, enrollments, announcements, feedback, audit logs, and per-user upcoming events — with a focus on security, clean architecture, and a responsive user interface.

### Key Highlights

- **Full-Stack Solution**: React + TypeScript frontend with Spring Boot backend
- **RESTful API**: Well-structured REST APIs following best practices
- **Security**: JWT-based authentication, role-based access control, and GitHub OAuth2 login
- **Detail Views**: Rich detail endpoints for students (with GPA-based academic standing) and teachers (with full course list)
- **Upcoming Events**: Per-user event/deadline tracking with type classification
- **Announcements**: Course-linked announcement system
- **Documentation**: Full API documentation via Swagger/OpenAPI
- **Modern UI**: Responsive dashboard with TailwindCSS, Framer Motion animations, and protected routes

## ✨ Features

### Authentication & Authorization

- User registration and JWT login
- GitHub OAuth2 login (auto-creates account on first login)
- Role-based access control: `Admin`, `Teacher`, `Student`
- Account activation / deactivation
- Stateless security with `OncePerRequestFilter` JWT validation

### Student Management

- Full CRUD for student profiles
- GPA, enrollment year, and total credits tracking
- **Student Details endpoint**: returns enrolled courses + academic standing (Excellent / Very Good / Good / Satisfactory / Probation) computed from GPA

### Teacher Management

- Full CRUD for teacher profiles (office location, salary)
- **Teacher Details endpoint**: returns full course list with department and enrollment counts

### Course Management

- Full CRUD for courses
- Department and teacher assignment
- Capacity management
- Popular courses ranking (ordered by enrollment count)

### Department Management

- Full CRUD for departments
- Department-to-course relationships

### Enrollment System

- Student course enrollment and drop
- Enrollment history per student and per course

### Announcement System

- Course-linked announcements (title, description, timestamp)
- CRUD per course

### Feedback System

- User feedback with role label and comment
- Filter by role or user

### Upcoming Events

- Per-user event cards (owned by a `user_id` FK)
- Event types: `HIGH_PRIORITY`, `EXAM`, `EVENT`
- Filter by type or by user
- `GET /api/events/upcoming` returns events from now onwards, ordered by date

### Audit Logging

- Tracks user actions (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.)
- Filter by username, action, or combination

## 🛠 Technology Stack

### Frontend

| Technology       | Version |
| ---------------- | ------- |
| React            | 19.2.0  |
| TypeScript       | 5.9.3   |
| Vite             | 7.3.1   |
| React Router DOM | 7.13.0  |
| TailwindCSS      | 4.2.0   |
| Framer Motion    | 12.34.3 |
| Lucide React     | 0.575.0 |

### Backend

| Technology         | Details               |
| ------------------ | --------------------- |
| Spring Boot        | 3.4.2                 |
| Java               | 21                    |
| Spring Security    | JWT + OAuth2 GitHub   |
| Spring Data JPA    | Hibernate ORM         |
| MySQL              | 8.0                   |
| Flyway             | Database migrations   |
| SpringDoc OpenAPI  | 2.7.0 — Swagger UI    |
| Lombok             | Boilerplate reduction |
| Jakarta Validation | Request validation    |

### Infrastructure

- **Docker + Docker Compose**: containerised MySQL
- **Maven**: backend build tool
- **npm**: frontend build tool

## 🏗 System Architecture

```
┌─────────────────────────────────────────────┐
│           Client Layer (Browser)            │
├─────────────────────────────────────────────┤
│         React Application (Frontend)        │
│  Components · Services · AuthContext        │
│  Protected Routes · Dashboard Layout        │
├─────────────────────────────────────────────┤
│         Spring Boot Backend (Java)          │
│  ┌───────────────────────────────────────┐  │
│  │    REST Controllers (API Endpoints)   │  │
│  ├───────────────────────────────────────┤  │
│  │  Security Layer (JWT + OAuth2 + CORS) │  │
│  ├───────────────────────────────────────┤  │
│  │       Business Logic (Services)       │  │
│  ├───────────────────────────────────────┤  │
│  │   Data Access Layer (Repositories)    │  │
│  ├───────────────────────────────────────┤  │
│  │       DTOs · Entities · Enums         │  │
│  └───────────────────────────────────────┘  │
├─────────────────────────────────────────────┤
│               MySQL Database                │
│     (schema managed by Flyway V1–V5)        │
└─────────────────────────────────────────────┘
```

For detailed diagrams, see:

- [System Design](./diagrams/system-design.md)
- [Sequential Diagrams](./diagrams/sequential-diagram.md)
- [Activity Diagrams](./diagrams/activity-diagram.md)

## 🚀 Getting Started

### Prerequisites

- Java JDK 21+
- Node.js v18+
- MySQL 8.0+
- Maven 3.8+
- npm 9.0+

### 1. Clone the Repository

```bash
git clone https://github.com/mahmoudss5/UniSystem.git
cd UniSystem
```

### 2. Backend Setup

```bash
cd Backend/Uni
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/helwanuni
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your-256-bit-secret
jwt.expiration=864000000
teacherCode=teacher123
```

Run the application:

```bash
mvn clean install
mvn spring-boot:run
```

Backend starts on `http://localhost:8080`. Flyway runs all migrations automatically on startup.

### 3. Frontend Setup

```bash
cd FrontEnd/my-app
npm install
npm run dev
```

Frontend starts on `http://localhost:5173`.

### 4. Docker (MySQL only)

```bash
cd Backend/Uni
docker-compose up -d
```

This spins up a MySQL container matching the `application.properties` configuration.

## 📁 Project Structure

```
UniSystem/
├── Backend/
│   └── Uni/
│       ├── src/main/java/UnitSystem/demo/
│       │   ├── Controllers/
│       │   │   ├── AuthController.java
│       │   │   ├── StudentController.java
│       │   │   ├── TeacherController.java
│       │   │   ├── CourseController.java
│       │   │   ├── DepartmentController.java
│       │   │   ├── EnrolledCourseController.java
│       │   │   ├── AnnouncementController.java
│       │   │   ├── FeedbackController.java
│       │   │   ├── AuditLogController.java
│       │   │   ├── UserController.java
│       │   │   └── UpcomingEventController.java
│       │   ├── BusinessLogic/
│       │   │   ├── InterfaceServiceLayer/    # Service interfaces
│       │   │   └── ImpServiceLayer/          # Service implementations
│       │   ├── DataAccessLayer/
│       │   │   ├── Entities/                 # JPA entities + enums
│       │   │   ├── Repositories/             # Spring Data repositories
│       │   │   └── Dto/                      # Request / Response DTOs
│       │   │       ├── Auth/
│       │   │       ├── Student/
│       │   │       ├── Teacher/
│       │   │       ├── Course/
│       │   │       ├── Department/
│       │   │       ├── EnrolledCourse/
│       │   │       ├── Announcement/
│       │   │       ├── Feedback/
│       │   │       ├── AuditLog/
│       │   │       ├── User/
│       │   │       ├── UpcomingEvent/
│       │   │       └── UserDetails/          # Detail view DTOs
│       │   ├── Security/
│       │   │   ├── config/                   # AppConfig, SecurityConfiguration
│       │   │   ├── Jwt/                      # JwtService, JwtAuthenticationFilter
│       │   │   ├── Oauth2/                   # OAuth2LoginSuccessHandler
│       │   │   ├── User/                     # SecurityUser, CustomUserDetailsService
│       │   │   └── Util/                     # SecurityUtils
│       │   └── Config/
│       │       ├── DataSeeder.java           # Seeds roles on startup
│       │       └── SwaggerConfig/
│       └── src/main/resources/
│           ├── application.properties
│           └── db/migration/
│               ├── V1__init_schema.sql
│               ├── V2__AddCourseDescriptionCoulmn.schema.sql
│               ├── V3__AddFeedBackTable.schema.sql
│               ├── V4__AddAnouncmentTable.schema.sql
│               └── V5__AddUpcomingEventsTable.schema.sql
├── FrontEnd/
│   └── my-app/
│       └── src/
│           ├── components/
│           │   ├── Auth/                     # LoginForm, RegisterForm
│           │   ├── common/                   # Nav, AsideNav, Footer, ProtectedRoute
│           │   ├── Dashboard/                # StatsCard, EnrolledCourses,
│           │   │                             # RecentAnnouncements, UpcomingEvents
│           │   └── Home/                     # CourseCard, Departments, FeedBacks, etc.
│           ├── pages/                        # Home, Auth, Dashboard, RootLayout
│           ├── Services/                     # authService, courseService, etc.
│           ├── ContextsProviders/            # AuthContext
│           └── Interfaces/                   # TypeScript interfaces
├── diagrams/
└── README.md
```

## 📚 API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the backend is running.

### Authentication

| Method | Endpoint             | Description                   |
| ------ | -------------------- | ----------------------------- |
| POST   | `/api/auth/register` | Register a new user           |
| POST   | `/api/auth/login`    | Login and receive a JWT token |

### Users

| Method | Endpoint                           | Description             |
| ------ | ---------------------------------- | ----------------------- |
| GET    | `/api/users`                       | Get all users           |
| POST   | `/api/users`                       | Create user             |
| PUT    | `/api/users`                       | Update user             |
| DELETE | `/api/users/{userId}`              | Delete user             |
| PATCH  | `/api/users/{userId}/deactivate`   | Deactivate user         |
| PATCH  | `/api/users/deactivate-current`    | Deactivate current user |
| POST   | `/api/users/{userId}/roles/{role}` | Assign role to user     |

### Students

| Method | Endpoint                            | Description                                                     |
| ------ | ----------------------------------- | --------------------------------------------------------------- |
| GET    | `/api/students`                     | Get all students                                                |
| GET    | `/api/students/{id}`                | Get student by ID                                               |
| GET    | `/api/students/username/{userName}` | Get student by username                                         |
| GET    | `/api/students/details/{id}`        | Get full student details (enrolled courses + academic standing) |
| POST   | `/api/students`                     | Create student                                                  |
| PUT    | `/api/students/{id}`                | Update student                                                  |
| DELETE | `/api/students/{id}`                | Delete student                                                  |

### Teachers

| Method | Endpoint                            | Description                                          |
| ------ | ----------------------------------- | ---------------------------------------------------- |
| GET    | `/api/teachers`                     | Get all teachers                                     |
| GET    | `/api/teachers/{id}`                | Get teacher by ID                                    |
| GET    | `/api/teachers/username/{userName}` | Get teacher by username                              |
| GET    | `/api/teachers/details/{id}`        | Get full teacher details (courses + salary + office) |
| POST   | `/api/teachers`                     | Create teacher                                       |
| PUT    | `/api/teachers/{id}`                | Update teacher                                       |
| DELETE | `/api/teachers/{id}`                | Delete teacher                                       |

### Courses

| Method | Endpoint                      | Description               |
| ------ | ----------------------------- | ------------------------- |
| GET    | `/api/courses`                | Get all courses           |
| GET    | `/api/courses/{id}`           | Get course by ID          |
| GET    | `/api/courses/popular`        | Get top 4 popular courses |
| GET    | `/api/courses/popular/{topN}` | Get top N popular courses |
| POST   | `/api/courses`                | Create course             |
| PUT    | `/api/courses/{id}`           | Update course             |
| DELETE | `/api/courses/{id}`           | Delete course             |

### Departments

| Method | Endpoint                       | Description         |
| ------ | ------------------------------ | ------------------- |
| GET    | `/api/departments/all`         | Get all departments |
| GET    | `/api/departments/{id}`        | Get by ID           |
| GET    | `/api/departments/name/{name}` | Get by name         |
| POST   | `/api/departments`             | Create department   |
| PUT    | `/api/departments/{id}`        | Update department   |
| DELETE | `/api/departments/{id}`        | Delete department   |

### Enrollments

| Method | Endpoint                                    | Description              |
| ------ | ------------------------------------------- | ------------------------ |
| GET    | `/api/enrolled-courses`                     | Get all enrollments      |
| GET    | `/api/enrolled-courses/{id}`                | Get by ID                |
| GET    | `/api/enrolled-courses/student/{studentId}` | Get by student           |
| GET    | `/api/enrolled-courses/course/{courseId}`   | Get by course            |
| POST   | `/api/enrolled-courses`                     | Enroll student in course |
| DELETE | `/api/enrolled-courses/{id}`                | Drop enrollment          |

### Announcements

| Method | Endpoint                                      | Description         |
| ------ | --------------------------------------------- | ------------------- |
| POST   | `/api/announcements/create`                   | Create announcement |
| POST   | `/api/announcements/delete/{id}`              | Delete announcement |
| POST   | `/api/announcements/get/{id}`                 | Get by ID           |
| POST   | `/api/announcements/getByCourseId/{courseId}` | Get by course       |
| POST   | `/api/announcements/getAll`                   | Get all             |

### Feedbacks

| Method | Endpoint                       | Description       |
| ------ | ------------------------------ | ----------------- |
| GET    | `/api/feedbacks`               | Get all feedbacks |
| GET    | `/api/feedbacks/{id}`          | Get by ID         |
| GET    | `/api/feedbacks/role/{role}`   | Get by role       |
| GET    | `/api/feedbacks/user/{userId}` | Get by user       |
| POST   | `/api/feedbacks`               | Create feedback   |
| PUT    | `/api/feedbacks/{id}`          | Update feedback   |
| DELETE | `/api/feedbacks/{id}`          | Delete feedback   |

### Audit Logs

| Method | Endpoint                                              | Description      |
| ------ | ----------------------------------------------------- | ---------------- |
| GET    | `/api/audit-logs`                                     | Get all logs     |
| GET    | `/api/audit-logs/{id}`                                | Get by ID        |
| GET    | `/api/audit-logs/username/{userName}`                 | Get by username  |
| GET    | `/api/audit-logs/action/{action}`                     | Get by action    |
| GET    | `/api/audit-logs/action/{action}/username/{userName}` | Filter by both   |
| POST   | `/api/audit-logs`                                     | Create log entry |
| DELETE | `/api/audit-logs/{id}`                                | Delete log       |

### Upcoming Events

| Method | Endpoint                    | Description                                       |
| ------ | --------------------------- | ------------------------------------------------- |
| GET    | `/api/events`               | Get all events                                    |
| GET    | `/api/events/upcoming`      | Get events from now onwards (ordered by date)     |
| GET    | `/api/events/type/{type}`   | Filter by type (`HIGH_PRIORITY`, `EXAM`, `EVENT`) |
| GET    | `/api/events/user/{userId}` | Get events belonging to a user                    |
| GET    | `/api/events/{id}`          | Get event by ID                                   |
| POST   | `/api/events`               | Create event                                      |
| PUT    | `/api/events/{id}`          | Update event                                      |
| DELETE | `/api/events/{id}`          | Delete event                                      |

### Request / Response Examples

#### Login

```json
POST /api/auth/login
{ "email": "student@uni.edu", "password": "pass123" }

Response 200:
{ "Username": "john.doe", "Token": "eyJhbGci..." }
```

#### Get Student Details

```json
GET /api/students/details/1
Authorization: Bearer {token}

Response 200:
{
  "id": 1,
  "username": "john.doe",
  "email": "john@uni.edu",
  "gpa": 3.7,
  "enrollmentYear": 2023,
  "totalCredits": 60,
  "enrolledCoursesCount": 4,
  "enrolledCourses": [...],
  "academicStanding": "Excellent"
}
```

#### Create Upcoming Event

```json
POST /api/events
Authorization: Bearer {token}
{
  "title": "Midterm Exam - Algorithms",
  "subtitle": "Covers chapters 1-6",
  "eventDate": "2026-04-15T09:00:00",
  "type": "EXAM",
  "userId": 1
}

Response 201:
{
  "id": 5,
  "title": "Midterm Exam - Algorithms",
  "subtitle": "Covers chapters 1-6",
  "eventDate": "2026-04-15T09:00:00",
  "type": "EXAM",
  "userId": 1,
  "userName": "john.doe",
  "createdAt": "2026-03-02T10:00:00"
}
```

## 🗄 Database Schema

### Flyway Migrations

| Version | File                                        | Description                                                                                       |
| ------- | ------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| V1      | `V1__init_schema.sql`                       | Core tables: users, students, teachers, roles, courses, departments, enrolled_courses, audit_logs |
| V2      | `V2__AddCourseDescriptionCoulmn.schema.sql` | Adds `course_description` to courses                                                              |
| V3      | `V3__AddFeedBackTable.schema.sql`           | Creates `feedbacks` table                                                                         |
| V4      | `V4__AddAnouncmentTable.schema.sql`         | Creates `announcements` table (linked to courses)                                                 |
| V5      | `V5__AddUpcomingEventsTable.schema.sql`     | Creates `upcoming_events` table (linked to users)                                                 |

### Entity Relationships

```
users  (JOINED inheritance parent)
  ├── students        (user_id PK/FK)
  ├── teachers        (user_id PK/FK)
  ├── user_roles      (M:N with roles)
  ├── feedbacks       (1:N)
  ├── audit_logs      (1:N)
  └── upcoming_events (1:N)  ← each event is owned by a user

departments
  └── courses (1:N)

teachers
  └── courses (1:N)

courses
  ├── enrolled_courses (1:N)
  └── announcements    (1:N)

students
  └── enrolled_courses (1:N)
```

### Key Tables

| Table              | Description                                             |
| ------------------ | ------------------------------------------------------- |
| `users`            | Base user table (JOINED inheritance)                    |
| `students`         | Student-specific fields (GPA, enrollment year, credits) |
| `teachers`         | Teacher-specific fields (office location, salary)       |
| `roles`            | Role definitions (Student, Teacher, Admin)              |
| `user_roles`       | User↔Role join table                                    |
| `courses`          | Course catalogue (name, description, capacity, credits) |
| `departments`      | Academic departments                                    |
| `enrolled_courses` | Student↔Course enrollment records                       |
| `announcements`    | Course-linked announcements                             |
| `feedbacks`        | User feedback with role label                           |
| `audit_logs`       | User action audit trail                                 |
| `upcoming_events`  | Per-user scheduled events (exam, deadline, event)       |

## 🔒 Security

### Authentication Flow

1. User submits credentials → `POST /api/auth/login`
2. Spring Security validates via `DaoAuthenticationProvider`
3. `JwtService` generates a signed HMAC-SHA token (10-day expiry) embedding `userId`, `userName`, and `roles`
4. Subsequent requests carry `Authorization: Bearer <token>`
5. `JwtAuthenticationFilter` validates the token and populates `SecurityContext`

### OAuth2 (GitHub)

- On first GitHub login, a new `User` is created automatically and a JWT is returned via redirect to `localhost:5173`

### Public Endpoints (no token required)

```
POST /api/auth/**
GET  /api/courses/popular
GET  /api/courses/popular/**
GET  /api/departments/all
GET  /swagger-ui/**
GET  /v3/api-docs/**
```

### Password Storage

- BCrypt hashing via `PasswordEncoder` bean

### CORS

- Configured to allow `http://localhost:5173`

## 🧪 Testing

### Backend

```bash
cd Backend/Uni
mvn test
```

### Frontend

```bash
cd FrontEnd/my-app
npm test
```

## 🚢 Deployment

### Production Builds

```bash
# Backend
cd Backend/Uni
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Frontend
cd FrontEnd/my-app
npm run build
# Serve dist/ with a static web server or NGINX
```

### Key Environment Variables

#### Backend (`application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/helwanuni
spring.datasource.username=root
spring.datasource.password=your_password
jwt.secret=your-256-bit-hex-secret
jwt.expiration=864000000
teacherCode=teacher123
```

#### Frontend

```bash
# vite.config.ts proxy or .env
VITE_API_URL=http://localhost:8080
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m 'feat: add your feature'`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

### Code Conventions

- Java: standard naming conventions, service interface + implementation pattern, manual DTO mapping (no MapStruct)
- TypeScript/React: functional components, typed props, Axios-based services
- Write descriptive commit messages

## 📝 License

This project is licensed under the MIT License.

## 📞 Contact

- GitHub: [@mahmoudss5](https://github.com/mahmoudss5)

---

> This is a learning/portfolio project. Additional security hardening, input validation, and testing are recommended before any production use.
