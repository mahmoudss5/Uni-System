import { useQuery } from "@tanstack/react-query";
import { getAllCourses } from "../../Services/CourseService";
export function useGetAllCourses() {

    const { data, isLoading, error } = useQuery({
        queryKey: ["allCourses"],
        queryFn: getAllCourses,
    });

    return {
        courses: data || [],
        isLoading,
        error: error instanceof Error ? error.message : "Unknown error",
    };
}