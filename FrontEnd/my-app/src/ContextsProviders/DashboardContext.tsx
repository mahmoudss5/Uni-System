import { createContext, useContext, useMemo } from "react";
import { useAuth } from "./AuthContext";
import type { Student } from "../Interfaces/student";
import type { Teacher, TeacherCourse } from "../Interfaces/teacher";
import {
    getStudentEnrolledCourses,
    getStudentAnnouncements,
    getStudentUpcomingEvents,
    getTeacherAnnouncements,
    getTeacherUpcomingEvents,
    type DashboardCourse,
    type DashboardAnnouncement,
    type DashboardEvent,
} from "../Services/dashboardService";

// ─── Student Dashboard ──────────────────────────────────────────────────────

export interface StudentDashboardContextType {
    gpa: number | string;
    totalCredits: number | string;
    enrolledCoursesCount: number | string;
    academicStanding: string;
    courses: DashboardCourse[];
    announcements: DashboardAnnouncement[];
    events: DashboardEvent[];
}

const StudentDashboardContext = createContext<StudentDashboardContextType | undefined>(undefined);

export function StudentDashboardProvider({ children }: { children: React.ReactNode }) {
    const { user } = useAuth();
    const student = user?.role === "student" ? (user as Student) : null;

    const value = useMemo<StudentDashboardContextType>(() => ({
        gpa:                  student?.gpa               ?? "0.00",
        totalCredits:         student?.totalCredits       ?? 0,
        enrolledCoursesCount: student?.enrolledCoursesCount ?? 0,
        academicStanding:     student?.academicStanding   ?? "Excellent",
        courses:              getStudentEnrolledCourses(),
        announcements:        getStudentAnnouncements(),
        events:               getStudentUpcomingEvents(),
    }), [student]);

    return (
        <StudentDashboardContext.Provider value={value}>
            {children}
        </StudentDashboardContext.Provider>
    );
}

export function useStudentDashboard(): StudentDashboardContextType {
    const ctx = useContext(StudentDashboardContext);
    if (!ctx) throw new Error("useStudentDashboard must be used within a StudentDashboardProvider");
    return ctx;
}

// ─── Teacher Dashboard ──────────────────────────────────────────────────────

export interface TeacherDashboardContextType {
    coursesCount: number;
    numberOfStudents: number;
    department: string;
    courses: TeacherCourse[];
    announcements: DashboardAnnouncement[];
    events: DashboardEvent[];
}

const TeacherDashboardContext = createContext<TeacherDashboardContextType | undefined>(undefined);

export function TeacherDashboardProvider({ children }: { children: React.ReactNode }) {
    const { user } = useAuth();
    const teacher = user?.role === "teacher" ? (user as Teacher) : null;

    const normalizedCourses: TeacherCourse[] = useMemo(() => {
        if (!teacher?.courses) return [];
        return Array.isArray(teacher.courses)
            ? teacher.courses
            : Array.from(teacher.courses as Iterable<TeacherCourse>);
    }, [teacher]);

    const value = useMemo<TeacherDashboardContextType>(() => ({
        coursesCount:     teacher?.coursesCount     ?? 0,
        numberOfStudents: teacher?.numberOfStudents  ?? 0,
        department:       teacher?.department        ?? "—",
        courses:          normalizedCourses,
        announcements:    getTeacherAnnouncements(),
        events:           getTeacherUpcomingEvents(),
    }), [teacher, normalizedCourses]);

    return (
        <TeacherDashboardContext.Provider value={value}>
            {children}
        </TeacherDashboardContext.Provider>
    );
}

export function useTeacherDashboard(): TeacherDashboardContextType {
    const ctx = useContext(TeacherDashboardContext);
    if (!ctx) throw new Error("useTeacherDashboard must be used within a TeacherDashboardProvider");
    return ctx;
}
