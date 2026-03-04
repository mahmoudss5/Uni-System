
interface Course {
    courseCode: string;
    courseName: string;
    instructor: string;
    credits: number;
    status: "In Progress" | "Pending" | "Completed";
}

interface EnrolledCoursesProps {
    courses: Course[];
    semester?: string;
}

export default function EnrolledCourses({ courses, semester = "Spring 2026" }: EnrolledCoursesProps) {
    const getStatusColor = (status: string) => {
        switch (status) {
            case "In Progress":
                return "bg-green-100 text-green-700";
            case "Pending":
                return "bg-yellow-100 text-yellow-700";
            case "Completed":
                return "bg-blue-100 text-blue-700";
            default:
                return "bg-gray-100 text-gray-700";
        }
    };

    return (
        <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-lg font-bold text-gray-800">Currently Enrolled Courses</h2>
                <span className="text-md text-blue-500 border border-blue-200 rounded-full px-4 py-1">{semester}</span>
            </div>
            
            <div className="overflow-x-auto">
                <table className="w-full">
                    <thead>
                        <tr className="border-b text-left">
                            <th className="pb-3 text-md font-semibold text-gray-500 uppercase tracking-wider">COURSE NAME</th>
                            <th className="pb-3 text-md font-semibold text-gray-500 uppercase tracking-wider">INSTRUCTOR</th>
                            <th className="pb-3 text-md font-semibold text-gray-500 uppercase tracking-wider text-center">CREDITS</th>
                            <th className="pb-3 text-md font-semibold text-gray-500 uppercase tracking-wider text-center">STATUS</th>
                            <th className="pb-3 text-md font-semibold text-gray-500 uppercase tracking-wider">ACTION</th>
                        </tr>
                    </thead>
                    <tbody>
                        {courses.map((course, index) => (
                            <tr key={index} className="border-b last:border-b-0 hover:bg-gray-50">
                                <td className="py-4 text-sm text-gray-700">{course.courseName}</td>
                                <td className="py-4 text-sm text-gray-700">{course.instructor}</td>
                                <td className="py-4 text-sm text-gray-700 text-center">{course.credits}</td>
                                <td className="py-4 text-center">
                                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(course.status)}`}>
                                        {course.status}
                                    </span>
                                </td>
                                <td className="py-4">
                                    <button className="text-blue-500 text-sm font-medium hover:underline">
                                        View Materials
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

            </div>
        </div>
    );
}
