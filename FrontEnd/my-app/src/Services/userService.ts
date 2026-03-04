import { decodeToken } from "./authService";
import { getTeacherDetails } from "./teacherService";
import { getStudentInfo } from "./studentService";
import type { Student } from "../Interfaces/student";
import type { Teacher } from "../Interfaces/teacher";

export async function getUser(token: string): Promise<Student | Teacher> {
    const decoded = await decodeToken(token);
    const userId = decoded.userId;

    if (decoded.roles.includes("Teacher")) {
        const data = await getTeacherDetails(userId);
        console.log("Teacher data:", data);
        return { ...data, role: "teacher" } as Teacher;
    }

    const data = await getStudentInfo(userId);
    console.log("Student data:", data);
    return { ...data, role: "student" } as Student;
}
