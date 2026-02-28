# UniSystem - University Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.0-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9.3-blue.svg)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)

A comprehensive, full-stack university management system built with modern technologies for managing students, teachers, courses, departments, and enrollments.

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
- [Caching Strategy](#caching-strategy)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

UniSystem is a modern, enterprise-grade university management system designed to streamline academic operations. It provides a comprehensive platform for managing students, teachers, courses, departments, enrollments, and feedback with a focus on performance, security, and user experience.

### Key Highlights

- **Full-Stack Solution**: React frontend with Spring Boot backend
- **RESTful API**: Well-designed REST APIs following best practices
- **Security**: JWT-based authentication and role-based access control
- **Performance**: Redis caching for optimized data retrieval
- **Scalability**: Designed with microservices principles
- **Documentation**: Comprehensive API documentation with Swagger/OpenAPI
- **Modern UI**: Responsive design with TailwindCSS and smooth animations

## ✨ Features

### User Management
- User registration and authentication
- Role-based access control (Admin, Teacher, Student)
- Profile management
- Account activation/deactivation

### Student Management
- Student registration and profile management
- GPA tracking and academic performance
- Course enrollment history
- Credit hours management

### Teacher Management
- Teacher profiles with office locations
- Salary management
- Course assignment
- Teaching history

### Course Management
- Create, update, and delete courses
- Course capacity management
- Department assignment
- Teacher assignment
- Popular courses tracking

### Department Management
- Department creation and management
- Department-course relationships
- Department statistics

### Enrollment System
- Student course enrollment
- Enrollment validation (capacity, duplicates)
- Enrollment history
- Drop course functionality

### Feedback System
- User feedback submission
- Role-based feedback
- Feedback moderation
- Feedback analytics

### Audit Logging
- Comprehensive activity logging
- User action tracking
- System audit trails
- Change history

### Additional Features
- Redis caching for improved performance
- Database migration with Flyway
- Responsive UI design
- Real-time form validation
- Error handling and user feedback
- API documentation with Swagger UI

## 🛠 Technology Stack

### Frontend
- **Framework**: React 19.2.0
- **Build Tool**: Vite 7.3.1
- **Language**: TypeScript 5.9.3
- **Routing**: React Router DOM 7.13.0
- **Styling**: TailwindCSS 4.2.0
- **Animations**: Framer Motion 12.34.3
- **Icons**: Lucide React 0.575.0

### Backend
- **Framework**: Spring Boot 3.4.2
- **Language**: Java 21
- **Security**: Spring Security + JWT
- **Database**: MySQL 8.0
- **Cache**: Redis
- **ORM**: Spring Data JPA + Hibernate
- **Migration**: Flyway
- **Validation**: Jakarta Validation
- **API Docs**: SpringDoc OpenAPI 2.7.0
- **Monitoring**: Spring Boot Actuator

### Development Tools
- **Build Tool**: Maven (Backend), npm (Frontend)
- **Version Control**: Git
- **Containerization**: Docker + Docker Compose
- **API Testing**: Swagger UI

## 🏗 System Architecture

UniSystem follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│           Client Layer (Browser)            │
├─────────────────────────────────────────────┤
│         React Application (Frontend)        │
│  - Components  - Services  - State Mgmt     │
├─────────────────────────────────────────────┤
│              API Gateway (NGINX)            │
├─────────────────────────────────────────────┤
│         Spring Boot Backend (Java)          │
│  ┌───────────────────────────────────────┐  │
│  │    REST Controllers (API Endpoints)   │  │
│  ├───────────────────────────────────────┤  │
│  │    Security Layer (JWT + Spring Sec)  │  │
│  ├───────────────────────────────────────┤  │
│  │       Business Logic (Services)       │  │
│  ├───────────────────────────────────────┤  │
│  │   Data Access Layer (Repositories)    │  │
│  ├───────────────────────────────────────┤  │
│  │  Cross-Cutting (Audit, Cache, Error)  │  │
│  └───────────────────────────────────────┘  │
├─────────────────────────────────────────────┤
│            Data Layer                       │
│  ┌──────────────┐      ┌──────────────┐    │
│  │    MySQL     │      │    Redis     │    │
│  │   Database   │      │    Cache     │    │
│  └──────────────┘      └──────────────┘    │
└─────────────────────────────────────────────┘
```

For detailed architecture diagrams, see:
- [System Design Diagram](./diagrams/system-design.md)
- [Sequential Diagrams](./diagrams/sequential-diagram.md)
- [Activity Diagrams](./diagrams/activity-diagram.md)

## 🚀 Getting Started

### Prerequisites

- **Java**: JDK 21 or higher
- **Node.js**: v18 or higher
- **MySQL**: 8.0 or higher
- **Redis**: Latest stable version
- **Maven**: 3.8 or higher
- **npm**: 9.0 or higher

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/UniSystem.git
cd UniSystem
```

#### 2. Backend Setup

```bash
cd Backend/Uni

# Configure database connection
# Edit src/main/resources/application.properties
# Set your MySQL credentials:
spring.datasource.url=jdbc:mysql://localhost:3306/unisystem
spring.datasource.username=your_username
spring.datasource.password=your_password

# Configure Redis
spring.redis.host=localhost
spring.redis.port=6379

# Build and run
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

#### 3. Frontend Setup

```bash
cd FrontEnd/my-app

# Install dependencies
npm install

# Configure API endpoint (if needed)
# Edit .env file or vite.config.ts

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

#### 4. Database Migration

Flyway will automatically run migrations on startup. Migration files are located in:
```
Backend/Uni/src/main/resources/db/migration/
```

### Docker Setup (Alternative)

```bash
# Start all services with Docker Compose
docker-compose up -d

# This will start:
# - MySQL database
# - Redis cache
# - Backend Spring Boot application
# - Frontend React application
```

## 📁 Project Structure

```
UniSystem/
├── Backend/
│   └── Uni/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── UnitSystem/demo/
│       │   │   │       ├── Controllers/          # REST Controllers
│       │   │   │       ├── BusinessLogic/
│       │   │   │       │   ├── InterfaceServiceLayer/   # Service Interfaces
│       │   │   │       │   └── ImpServiceLayer/         # Service Implementations
│       │   │   │       ├── DataAccessLayer/
│       │   │   │       │   ├── Entities/         # JPA Entities
│       │   │   │       │   ├── Repositories/     # JPA Repositories
│       │   │   │       │   └── Dto/              # Data Transfer Objects
│       │   │   │       └── Security/             # Security Configuration
│       │   │   └── resources/
│       │   │       ├── application.properties
│       │   │       └── db/migration/             # Flyway Migrations
│       │   └── test/                             # Unit & Integration Tests
│       └── pom.xml
├── FrontEnd/
│   └── my-app/
│       ├── src/
│       │   ├── components/
│       │   │   ├── common/                       # Reusable Components
│       │   │   ├── Home/                         # Home Page Components
│       │   │   └── Auth/                         # Authentication Components
│       │   ├── pages/                            # Page Components
│       │   ├── Services/                         # API Services
│       │   ├── App.tsx                           # Main App Component
│       │   └── main.tsx                          # Entry Point
│       ├── package.json
│       └── vite.config.ts
├── diagrams/                                      # System Documentation
│   ├── system-design.md
│   ├── sequential-diagram.md
│   └── activity-diagram.md
└── README.md
```

## 📚 API Documentation

### Accessing API Documentation

Once the backend is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

### Main API Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

#### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

#### Students
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `GET /api/students/username/{userName}` - Get student by username
- `POST /api/students` - Create new student
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

#### Teachers
- `GET /api/teachers` - Get all teachers
- `GET /api/teachers/{id}` - Get teacher by ID
- `POST /api/teachers` - Create new teacher
- `PUT /api/teachers/{id}` - Update teacher
- `DELETE /api/teachers/{id}` - Delete teacher

#### Courses
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `GET /api/courses/popular/{topN}` - Get most popular courses
- `POST /api/courses` - Create new course
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

#### Enrollments
- `GET /api/enrolled-courses` - Get all enrollments
- `GET /api/enrolled-courses/{id}` - Get enrollment by ID
- `POST /api/enrolled-courses` - Create new enrollment
- `DELETE /api/enrolled-courses/{id}` - Delete enrollment

#### Departments
- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create new department
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department

#### Feedbacks
- `GET /api/feedbacks` - Get all feedbacks
- `GET /api/feedbacks/{id}` - Get feedback by ID
- `POST /api/feedbacks` - Create new feedback
- `PUT /api/feedbacks/{id}` - Update feedback
- `DELETE /api/feedbacks/{id}` - Delete feedback

#### Audit Logs
- `GET /api/audit-logs` - Get all audit logs
- `GET /api/audit-logs/{id}` - Get audit log by ID

### Request/Response Examples

#### Register User
```json
POST /api/auth/register
{
  "userName": "john.doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "active": true
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userName": "john.doe",
  "email": "john.doe@example.com",
  "roles": ["ROLE_STUDENT"]
}
```

#### Enroll in Course
```json
POST /api/enrolled-courses
Authorization: Bearer {JWT_TOKEN}
{
  "studentId": 1,
  "courseId": 5
}

Response: 201 Created
{
  "id": 1,
  "studentId": 1,
  "courseId": 5,
  "enrollmentDate": "2026-02-27T10:30:00"
}
```

## 🗄 Database Schema

### Entity Relationships

```
Users (Parent)
  ├─ Students (Child via JOINED inheritance)
  ├─ Teachers (Child via JOINED inheritance)
  ├─ User_Roles (Many-to-Many with Roles)
  ├─ Feedbacks (One-to-Many)
  └─ Audit_Logs (One-to-Many)

Departments
  └─ Courses (One-to-Many)

Teachers
  └─ Courses (One-to-Many)

Students
  └─ Enrolled_Courses (One-to-Many)

Courses
  └─ Enrolled_Courses (One-to-Many)
```

### Key Tables

- **users**: Base table for all users (Students, Teachers, Admins)
- **students**: Extended user information for students
- **teachers**: Extended user information for teachers
- **courses**: Course catalog
- **departments**: Academic departments
- **enrolled_courses**: Student-course enrollments
- **feedbacks**: User feedback submissions
- **audit_logs**: System activity logs
- **roles**: User roles
- **user_roles**: User-role mappings

## 🔒 Security

### Authentication
- JWT (JSON Web Tokens) for stateless authentication
- BCrypt password hashing
- Token expiration and refresh mechanisms

### Authorization
- Role-based access control (RBAC)
- Method-level security with `@PreAuthorize`
- Endpoint protection with Spring Security

### Security Headers
- CORS configuration
- CSRF protection
- XSS prevention
- Content Security Policy

### Protected Endpoints
All endpoints except `/api/auth/**` require valid JWT tokens:

```java
Authorization: Bearer <JWT_TOKEN>
```

## ⚡ Caching Strategy

### Redis Caching
- Course listings cached with TTL
- Popular courses cached
- Department data cached
- Cache invalidation on updates

### Cache Keys
```
courses:all              - All courses list
courses:popular:{topN}   - Popular courses
departments:all          - All departments
student:{id}            - Student details
```

### Cache Invalidation
Automatic cache invalidation on:
- Course creation/update/deletion
- Department changes
- Student enrollment changes

## 🧪 Testing

### Backend Tests

```bash
cd Backend/Uni

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=StudentServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### Frontend Tests

```bash
cd FrontEnd/my-app

# Run tests
npm test

# Run with coverage
npm run test:coverage
```

## 🚢 Deployment

### Production Build

#### Backend
```bash
cd Backend/Uni
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd FrontEnd/my-app
npm run build
# Serve the dist/ folder with a web server
```

### Docker Deployment

```bash
# Build images
docker build -t unisystem-backend ./Backend/Uni
docker build -t unisystem-frontend ./FrontEnd/my-app

# Run containers
docker-compose up -d
```

### Environment Variables

#### Backend
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/unisystem
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

#### Frontend
```bash
VITE_API_URL=http://localhost:8080/api
```

## 📊 Monitoring

### Spring Boot Actuator Endpoints

```bash
# Health check
GET /actuator/health

# Application info
GET /actuator/info

# Metrics
GET /actuator/metrics

# Environment
GET /actuator/env
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Java naming conventions for backend
- Follow React/TypeScript best practices for frontend
- Write unit tests for new features
- Update documentation as needed

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Your Name** - *Initial work*

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the amazing frontend library
- All open-source contributors

## 📞 Contact

For questions or support, please contact:
- Email: your.email@example.com
- GitHub: [@yourusername](https://github.com/yourusername)

---

**Note**: This is a learning/demonstration project. For production use, additional security hardening and testing are recommended.
