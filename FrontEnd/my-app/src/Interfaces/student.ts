export interface Student {
    role: "student";
    id: number;
    name: string;
    email: string;
    gpa: number;
    totalCredits: number;
    enrolledCoursesCount: number;
    enrollmentYear: number;
    academicStanding: string;
}