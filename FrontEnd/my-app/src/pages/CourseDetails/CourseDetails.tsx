import { useParams } from "react-router-dom";
import { useGetCourseById } from "../../CustomeHooks/CoursesHooks/UseGetCourseById.tsx";
import LoadingSpinner from "../../components/common/LodingSpinner.tsx";
import ErrorPage from "../../components/common/ErrorPage.tsx";
export default function CourseDetails() {
    const { id } = useParams();
    const { course, isLoading, error } = useGetCourseById(id || "");
    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorPage />;
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 px-4">
            <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8 text-center">
                <h1 className="text-2xl font-bold text-gray-800 mb-4">{course?.name}</h1>
                <p className="text-gray-600 mb-6">{course?.description}</p>
                <p className="text-gray-600 mb-6">{course?.Teacher}</p>
                <p className="text-gray-600 mb-6">{course?.department}</p>
                <p className="text-gray-600 mb-6">{course?.credits}</p>
                <p className="text-gray-600 mb-6">{course?.maxStudents}</p>
            </div>
        </div>
    )
}