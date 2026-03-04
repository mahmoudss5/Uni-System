export interface TeacherCourse {
    id: number;
    name: string;
    description: string;
    departmentName: string;
    teacherUserName: string;
    creditHours: number;
    maxStudents: number;
    enrolledStudents: number;
}

export interface Teacher {
    role: "teacher";
    teacherId: number;
    name: string;
    email: string;
    salary: number;
    department: string;
    coursesCount: number;
    numberOfStudents: number;
    courses: TeacherCourse[];
}