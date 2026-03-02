import { decodeToken } from "./authService";
import { getStudentInfo } from "./studentService";
import { getTeacherInfo } from "./teacherService";

export async function getUser(token: string) {
    const decoded = await decodeToken(token);
    const user = {
        id: decoded.userId.toString(),
        email: decoded.sub,
        username: decoded.userName,
        isAdmin: decoded.roles.includes("Admin"),
        isTeacher: decoded.roles.includes("Teacher"),
        isStudent: decoded.roles.includes("Student"),
    };
    const userId = parseInt(user.id);
    if(user.isTeacher){
        return getTeacherInfo(userId);
    }

    return getStudentInfo(userId);

}