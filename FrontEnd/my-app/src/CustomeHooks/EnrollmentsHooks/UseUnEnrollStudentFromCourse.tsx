import { useMutation } from "@tanstack/react-query";
import { unenrollStudentFromCourse } from "../../Services/EnrolledCourseService";
import { queryClient } from "../../main";
export function useUnEnrollStudentFromCourse(enrolledCourseId: number) {
    
    const { mutate, isPending, error } = useMutation({
        mutationFn: (enrolledCourseId: number) => unenrollStudentFromCourse(enrolledCourseId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["enrolledCourses", enrolledCourseId] });
        },
    });
    if(error){
        console.error("Error unenrolling student from course:", error instanceof Error ? error.message : error);
    }
    return {
        unenrollStudentFromCourse: mutate,
        isPending,
        error: error ? (error instanceof Error ? error.message : "Unknown error") : null,
    };
}