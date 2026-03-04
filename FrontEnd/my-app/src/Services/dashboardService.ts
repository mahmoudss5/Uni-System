// ─── Shared types ──────────────────────────────────────────────────────────
export interface DashboardCourse {
    courseCode: string;
    courseName: string;
    instructor: string;
    credits: number;
    status: "In Progress" | "Pending" | "Completed";
}

export interface DashboardAnnouncement {
    id: string;
    title: string;
    description: string;
    timeAgo: string;
    type: "info" | "warning" | "success" | "default";
}

export interface DashboardEvent {
    id: string;
    date: { month: string; day: string };
    title: string;
    subtitle: string;
    type: "High Priority" | "Exam" | "Event";
}

// ─── Student data ────────────────────────────────────────────────────────────
export function getStudentEnrolledCourses(): DashboardCourse[] {
    return [
        { courseCode: "CS-301",  courseName: "Software Engineering",  instructor: "Dr. Sarah Johnson",   credits: 3, status: "In Progress" },
        { courseCode: "CS-305",  courseName: "Database Systems",       instructor: "Prof. Michael Chen",  credits: 4, status: "In Progress" },
        { courseCode: "MATH-401",courseName: "Linear Algebra",         instructor: "Dr. Emily Watson",    credits: 3, status: "In Progress" },
        { courseCode: "CS-310",  courseName: "Web Development",        instructor: "Prof. James Miller",  credits: 3, status: "Pending"     },
        { courseCode: "ENG-201", courseName: "Technical Writing",      instructor: "Dr. Lisa Anderson",   credits: 2, status: "In Progress" },
    ];
}

export function getStudentAnnouncements(): DashboardAnnouncement[] {
    return [
        { id: "1", title: "Midterm Exam Schedule Released", description: "Check your courses pages for exam dates and locations",    timeAgo: "2 days ago",  type: "info"    },
        { id: "2", title: "Tuition Fee Deadline",           description: "Payment for spring 2026 semester is due by March 15",      timeAgo: "4 days ago",  type: "warning" },
        { id: "3", title: "Career Fair Registration Open",  description: "Annual career fair scheduled for March 20–21",             timeAgo: "1 week ago",  type: "success" },
        { id: "4", title: "Library Extended Hours",         description: "Open 24/7 during exam period starting March 10",           timeAgo: "1 week ago",  type: "default" },
    ];
}

export function getStudentUpcomingEvents(): DashboardEvent[] {
    return [
        { id: "1", date: { month: "MAR", day: "15" }, title: "CS-301 Project Due",    subtitle: "Software Engineering", type: "High Priority" },
        { id: "2", date: { month: "MAR", day: "18" }, title: "CS-305 Midterm Exam",   subtitle: "Database Systems",     type: "Exam"          },
        { id: "3", date: { month: "MAR", day: "20" }, title: "Career Fair 2026",       subtitle: "Main Campus Hall",     type: "Event"         },
    ];
}

// ─── Teacher data ────────────────────────────────────────────────────────────
export function getTeacherAnnouncements(): DashboardAnnouncement[] {
    return [
        { id: "1", title: "Grades Submission Deadline",    description: "Midterm grades must be submitted by March 20",              timeAgo: "1 day ago",   type: "warning" },
        { id: "2", title: "Department Meeting",             description: "Monthly faculty meeting scheduled for March 12 at 10:00 AM",timeAgo: "3 days ago",  type: "info"    },
        { id: "3", title: "New Lab Equipment Available",   description: "Computer lab upgraded — reserve time slots via the portal", timeAgo: "5 days ago",  type: "success" },
        { id: "4", title: "Research Grant Applications Open", description: "Apply for university research funding before April 1",   timeAgo: "1 week ago",  type: "default" },
    ];
}

export function getTeacherUpcomingEvents(): DashboardEvent[] {
    return [
        { id: "1", date: { month: "MAR", day: "20" }, title: "Midterm Grades Due",        subtitle: "Portal submission deadline",    type: "High Priority" },
        { id: "2", date: { month: "MAR", day: "12" }, title: "Faculty Department Meeting", subtitle: "Conference Room B, 10:00 AM",   type: "Event"         },
        { id: "3", date: { month: "MAR", day: "25" }, title: "CS-305 Final Exam",          subtitle: "Exam supervision required",     type: "Exam"          },
    ];
}
