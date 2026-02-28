# UniSystem - Activity Diagrams

This document contains activity diagrams showing the business process flows of the UniSystem application.

## 1. User Registration & Authentication Process

```mermaid
flowchart TD
    Start([User Visits Website]) --> ViewHome[View Home Page]
    ViewHome --> Decision1{User Action?}
    
    Decision1 -->|Register| RegStart[Navigate to Register Page]
    Decision1 -->|Login| LoginStart[Navigate to Login Page]
    Decision1 -->|Browse| Browse[Browse Courses & Departments]
    
    %% Registration Flow
    RegStart --> FillRegForm[Fill Registration Form]
    FillRegForm --> ValidateRegInput{Validate Input?}
    ValidateRegInput -->|Invalid| ShowRegError[Show Validation Errors]
    ShowRegError --> FillRegForm
    ValidateRegInput -->|Valid| SubmitReg[Submit Registration]
    SubmitReg --> CheckEmail{Email Exists?}
    CheckEmail -->|Yes| ShowEmailError[Show Email Already Exists]
    ShowEmailError --> FillRegForm
    CheckEmail -->|No| CreateUser[Create User Account]
    CreateUser --> HashPassword[Hash Password]
    HashPassword --> SaveUser[Save to Database]
    SaveUser --> GenerateToken1[Generate JWT Token]
    GenerateToken1 --> LoginSuccess
    
    %% Login Flow
    LoginStart --> FillLoginForm[Fill Login Form]
    FillLoginForm --> ValidateLoginInput{Validate Input?}
    ValidateLoginInput -->|Invalid| ShowLoginError[Show Validation Errors]
    ShowLoginError --> FillLoginForm
    ValidateLoginInput -->|Valid| SubmitLogin[Submit Login]
    SubmitLogin --> CheckCreds{Credentials Valid?}
    CheckCreds -->|No| ShowAuthError[Show Authentication Error]
    ShowAuthError --> FillLoginForm
    CheckCreds -->|Yes| CheckActive{Account Active?}
    CheckActive -->|No| ShowInactiveError[Show Account Inactive]
    ShowInactiveError --> End1([End])
    CheckActive -->|Yes| GenerateToken2[Generate JWT Token]
    GenerateToken2 --> LoginSuccess[Login Successful]
    
    LoginSuccess --> StoreToken[Store JWT Token]
    StoreToken --> RedirectDashboard[Redirect to Dashboard]
    RedirectDashboard --> End2([End])
    
    Browse --> ViewDetails[View Course/Department Details]
    ViewDetails --> End3([End])
```

## 2. Course Enrollment Process

```mermaid
flowchart TD
    Start([Student Logged In]) --> ViewCourses[View Available Courses]
    ViewCourses --> FilterCourses{Filter Courses?}
    FilterCourses -->|By Department| FilterByDept[Filter by Department]
    FilterCourses -->|View Popular| ViewPopular[View Popular Courses]
    FilterCourses -->|View All| ViewAll[View All Courses]
    
    FilterByDept --> DisplayCourses[Display Courses]
    ViewPopular --> DisplayCourses
    ViewAll --> DisplayCourses
    
    DisplayCourses --> SelectCourse[Select Course]
    SelectCourse --> ViewCourseDetails[View Course Details]
    ViewCourseDetails --> Decision1{Enroll?}
    
    Decision1 -->|No| ViewCourses
    Decision1 -->|Yes| CheckAuth{Authenticated?}
    CheckAuth -->|No| RedirectLogin[Redirect to Login]
    RedirectLogin --> End1([End])
    
    CheckAuth -->|Yes| CheckEnrolled{Already Enrolled?}
    CheckEnrolled -->|Yes| ShowEnrolledError[Show Already Enrolled Message]
    ShowEnrolledError --> ViewCourses
    
    CheckEnrolled -->|No| CheckCapacity{Course Full?}
    CheckCapacity -->|Yes| ShowFullError[Show Course Full Message]
    ShowFullError --> ViewCourses
    
    CheckCapacity -->|No| CheckPrerequisites{Prerequisites Met?}
    CheckPrerequisites -->|No| ShowPrereqError[Show Prerequisites Error]
    ShowPrereqError --> ViewCourses
    
    CheckPrerequisites -->|Yes| ConfirmEnrollment{Confirm Enrollment?}
    ConfirmEnrollment -->|No| ViewCourses
    ConfirmEnrollment -->|Yes| ProcessEnrollment[Process Enrollment]
    
    ProcessEnrollment --> CreateEnrollRecord[Create Enrollment Record]
    CreateEnrollRecord --> UpdateStudentCredits[Update Student Credits]
    UpdateStudentCredits --> LogAudit[Log Audit Entry]
    LogAudit --> SendNotification[Send Confirmation Notification]
    SendNotification --> ShowSuccess[Show Success Message]
    ShowSuccess --> UpdateUI[Update UI - Show Enrolled Status]
    UpdateUI --> End2([End])
```

## 3. Course Management Process (Admin/Teacher)

```mermaid
flowchart TD
    Start([Admin/Teacher Login]) --> Dashboard[View Dashboard]
    Dashboard --> Action{Select Action?}
    
    Action -->|Create Course| CreateFlow[Start Create Course]
    Action -->|Update Course| UpdateFlow[Select Course to Update]
    Action -->|Delete Course| DeleteFlow[Select Course to Delete]
    Action -->|View Enrolled| ViewEnrolled[View Enrolled Students]
    
    %% Create Course Flow
    CreateFlow --> FillCourseForm[Fill Course Form]
    FillCourseForm --> SelectDept[Select Department]
    SelectDept --> SelectTeacher[Assign Teacher]
    SelectTeacher --> SetCapacity[Set Course Capacity]
    SetCapacity --> SetCredits[Set Credit Hours]
    SetCredits --> AddDescription[Add Course Description]
    AddDescription --> ValidateCourse{Validate Course Data?}
    ValidateCourse -->|Invalid| ShowCourseError[Show Validation Errors]
    ShowCourseError --> FillCourseForm
    ValidateCourse -->|Valid| CheckDuplicate{Course Name Exists?}
    CheckDuplicate -->|Yes| ShowDupError[Show Duplicate Error]
    ShowDupError --> FillCourseForm
    CheckDuplicate -->|No| SaveCourse[Save Course to Database]
    SaveCourse --> InvalidateCache[Invalidate Course Cache]
    InvalidateCache --> NotifySuccess1[Show Success Message]
    NotifySuccess1 --> Dashboard
    
    %% Update Course Flow
    UpdateFlow --> LoadCourseData[Load Course Data]
    LoadCourseData --> ModifyCourse[Modify Course Details]
    ModifyCourse --> ValidateUpdate{Validate Changes?}
    ValidateUpdate -->|Invalid| ShowUpdateError[Show Validation Errors]
    ShowUpdateError --> ModifyCourse
    ValidateUpdate -->|Valid| UpdateCourse[Update Course in Database]
    UpdateCourse --> InvalidateCache2[Invalidate Course Cache]
    InvalidateCache2 --> NotifySuccess2[Show Success Message]
    NotifySuccess2 --> Dashboard
    
    %% Delete Course Flow
    DeleteFlow --> ConfirmDelete{Confirm Deletion?}
    ConfirmDelete -->|No| Dashboard
    ConfirmDelete -->|Yes| CheckEnrollments{Has Enrollments?}
    CheckEnrollments -->|Yes| ShowDeleteError[Cannot Delete - Has Enrollments]
    ShowDeleteError --> Dashboard
    CheckEnrollments -->|No| DeleteCourseDB[Delete Course from Database]
    DeleteCourseDB --> InvalidateCache3[Invalidate Course Cache]
    InvalidateCache3 --> NotifySuccess3[Show Success Message]
    NotifySuccess3 --> Dashboard
    
    %% View Enrolled Students
    ViewEnrolled --> LoadEnrollments[Load Enrollment Data]
    LoadEnrollments --> DisplayStudents[Display Student List]
    DisplayStudents --> Dashboard
```

## 4. Student Profile Management Process

```mermaid
flowchart TD
    Start([Student Login]) --> ViewProfile[View Student Profile]
    ViewProfile --> Action{Select Action?}
    
    Action -->|View Enrolled Courses| ViewEnrolled[View My Courses]
    Action -->|Update Profile| UpdateProfile[Edit Profile Information]
    Action -->|Check GPA| ViewGPA[View Academic Performance]
    Action -->|Submit Feedback| SubmitFeedback[Submit Feedback Form]
    
    %% View Enrolled Courses
    ViewEnrolled --> LoadCourses[Load Enrolled Courses]
    LoadCourses --> DisplayCourses[Display Course List]
    DisplayCourses --> CourseAction{Course Action?}
    CourseAction -->|View Details| ViewCourseInfo[View Course Details]
    CourseAction -->|Drop Course| DropCourse{Confirm Drop?}
    DropCourse -->|No| DisplayCourses
    DropCourse -->|Yes| CheckDropDeadline{Within Drop Period?}
    CheckDropDeadline -->|No| ShowDropError[Show Deadline Passed Error]
    ShowDropError --> DisplayCourses
    CheckDropDeadline -->|Yes| RemoveEnrollment[Remove Enrollment]
    RemoveEnrollment --> UpdateCredits[Update Student Credits]
    UpdateCredits --> LogDropAudit[Log Drop Audit]
    LogDropAudit --> ShowDropSuccess[Show Success Message]
    ShowDropSuccess --> ViewProfile
    
    %% Update Profile
    UpdateProfile --> EditForm[Edit Profile Form]
    EditForm --> ValidateProfile{Validate Changes?}
    ValidateProfile -->|Invalid| ShowProfileError[Show Validation Errors]
    ShowProfileError --> EditForm
    ValidateProfile -->|Valid| SaveProfile[Save Profile Changes]
    SaveProfile --> ShowProfileSuccess[Show Success Message]
    ShowProfileSuccess --> ViewProfile
    
    %% View GPA
    ViewGPA --> CalculateGPA[Calculate Current GPA]
    CalculateGPA --> DisplayGPA[Display GPA & Credits]
    DisplayGPA --> ViewProfile
    
    %% Submit Feedback
    SubmitFeedback --> FillFeedbackForm[Fill Feedback Form]
    FillFeedbackForm --> ValidateFeedback{Validate Feedback?}
    ValidateFeedback -->|Invalid| ShowFeedbackError[Show Validation Errors]
    ShowFeedbackError --> FillFeedbackForm
    ValidateFeedback -->|Valid| SaveFeedback[Save Feedback]
    SaveFeedback --> ShowFeedbackSuccess[Show Success Message]
    ShowFeedbackSuccess --> ViewProfile
    
    ViewCourseInfo --> DisplayCourses
    ViewProfile --> End([End])
```

## 5. System Administration Process

```mermaid
flowchart TD
    Start([Admin Login]) --> AdminDashboard[View Admin Dashboard]
    AdminDashboard --> AdminAction{Select Action?}
    
    AdminAction -->|Manage Users| UserMgmt[User Management]
    AdminAction -->|Manage Departments| DeptMgmt[Department Management]
    AdminAction -->|View Audit Logs| ViewAudit[View Audit Logs]
    AdminAction -->|Manage Teachers| TeacherMgmt[Teacher Management]
    AdminAction -->|System Reports| Reports[Generate Reports]
    
    %% User Management
    UserMgmt --> UserAction{User Action?}
    UserAction -->|Create User| CreateUser[Create New User]
    UserAction -->|Update User| UpdateUser[Update User Details]
    UserAction -->|Deactivate User| DeactivateUser[Deactivate User Account]
    UserAction -->|Assign Roles| AssignRoles[Assign User Roles]
    
    CreateUser --> FillUserForm[Fill User Form]
    FillUserForm --> ValidateUser{Validate User Data?}
    ValidateUser -->|Invalid| ShowUserError[Show Validation Errors]
    ShowUserError --> FillUserForm
    ValidateUser -->|Valid| SaveUser[Save User to Database]
    SaveUser --> AdminDashboard
    
    %% Department Management
    DeptMgmt --> DeptAction{Department Action?}
    DeptAction -->|Create Department| CreateDept[Create New Department]
    DeptAction -->|Update Department| UpdateDept[Update Department]
    DeptAction -->|Delete Department| DeleteDept[Delete Department]
    
    CreateDept --> FillDeptForm[Fill Department Form]
    FillDeptForm --> ValidateDept{Validate Department?}
    ValidateDept -->|Invalid| ShowDeptError[Show Validation Errors]
    ShowDeptError --> FillDeptForm
    ValidateDept -->|Valid| CheckDeptDup{Department Exists?}
    CheckDeptDup -->|Yes| ShowDeptDupError[Show Duplicate Error]
    ShowDeptDupError --> FillDeptForm
    CheckDeptDup -->|No| SaveDept[Save Department]
    SaveDept --> AdminDashboard
    
    %% Audit Logs
    ViewAudit --> FilterAudit{Filter Logs?}
    FilterAudit -->|By Date| FilterByDate[Filter by Date Range]
    FilterAudit -->|By User| FilterByUser[Filter by User]
    FilterAudit -->|By Action| FilterByAction[Filter by Action Type]
    FilterByDate --> DisplayAudit[Display Audit Logs]
    FilterByUser --> DisplayAudit
    FilterByAction --> DisplayAudit
    DisplayAudit --> AdminDashboard
    
    %% Teacher Management
    TeacherMgmt --> TeacherAction{Teacher Action?}
    TeacherAction -->|Create Teacher| CreateTeacher[Create New Teacher]
    TeacherAction -->|Update Teacher| UpdateTeacher[Update Teacher Info]
    TeacherAction -->|Assign Courses| AssignCourses[Assign Courses to Teacher]
    
    CreateTeacher --> FillTeacherForm[Fill Teacher Form]
    FillTeacherForm --> ValidateTeacher{Validate Teacher?}
    ValidateTeacher -->|Invalid| ShowTeacherError[Show Validation Errors]
    ShowTeacherError --> FillTeacherForm
    ValidateTeacher -->|Valid| SaveTeacher[Save Teacher]
    SaveTeacher --> AdminDashboard
    
    %% Reports
    Reports --> SelectReport{Report Type?}
    SelectReport -->|Enrollment Stats| EnrollmentReport[Generate Enrollment Statistics]
    SelectReport -->|Course Popularity| PopularityReport[Generate Popularity Report]
    SelectReport -->|Student Performance| PerformanceReport[Generate Performance Report]
    SelectReport -->|Department Stats| DeptReport[Generate Department Statistics]
    
    EnrollmentReport --> DisplayReport[Display Report]
    PopularityReport --> DisplayReport
    PerformanceReport --> DisplayReport
    DeptReport --> DisplayReport
    DisplayReport --> ExportOption{Export Report?}
    ExportOption -->|Yes| ExportReport[Export to PDF/Excel]
    ExportOption -->|No| AdminDashboard
    ExportReport --> AdminDashboard
    
    UpdateUser --> AdminDashboard
    DeactivateUser --> AdminDashboard
    AssignRoles --> AdminDashboard
    UpdateDept --> AdminDashboard
    DeleteDept --> AdminDashboard
    UpdateTeacher --> AdminDashboard
    AssignCourses --> AdminDashboard
```

## Business Rules

### Enrollment Rules
1. Student must be authenticated
2. Student cannot enroll in the same course twice
3. Course must have available capacity
4. Prerequisites must be met (if applicable)
5. Student must have sufficient credit allowance

### Course Management Rules
1. Course name must be unique
2. Course must be assigned to a valid department
3. Course must have an assigned teacher
4. Course cannot be deleted if it has active enrollments
5. Course capacity must be greater than 0

### User Management Rules
1. Email must be unique across all users
2. Username must be unique
3. Password must meet security requirements
4. Users inherit permissions from assigned roles
5. Inactive users cannot login

### Feedback Rules
1. Only authenticated users can submit feedback
2. Feedback must include user role
3. Feedback is timestamped automatically
4. All feedbacks are stored for analytics

### Audit Logging
1. All CRUD operations are logged
2. User actions are tracked with timestamps
3. Changes include before/after states
4. Logs are immutable once created
