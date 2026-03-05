export interface EnrolledCourseResponse {
    id: number;
    studentId: number;
    courseId: number;
    courseName: string;
    teacherName: string;
    credits: number;
    enrollmentDate: string;
    status?: "In Progress" | "Pending" | "Completed";
}
