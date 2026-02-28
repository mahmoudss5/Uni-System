export function getBackgroundColor(department: string) {
    switch (department) {
        case "Computer Science":
            return "bg-gradient-to-br from-blue-400 to-blue-600";
        case "Mathematics":
            return "bg-gradient-to-br from-green-400 to-green-600";
        case "Physics":
            return "bg-gradient-to-br from-red-400 to-red-600";
        case "Chemistry":
            return "bg-gradient-to-br from-yellow-400 to-yellow-600";
        case "Information Systems":
            return "bg-gradient-to-br from-purple-400 to-purple-600";
        case "Software Engineering":
            return "bg-gradient-to-br from-orange-400 to-orange-600";
        case "Artificial Intelligence":
            return "bg-gradient-to-br from-pink-400 to-pink-600";
        case "Data Science":
            return "bg-gradient-to-br from-teal-400 to-teal-600";
        case "Cybersecurity":
            return "bg-gradient-to-br from-indigo-400 to-indigo-600";
    }
    return "bg-gradient-to-br from-gray-400 to-gray-600";
}

export function getDepartmentIcon(department: string) {
    switch (department) {
        case "Computer Science":
            return "💻";
        case "Mathematics":
            return "📐";
        case "Physics":
            return "⚛️";
        case "Chemistry":
            return "🧪";
        case "Computer Science":
            return "💻";
        case "Information Systems":
            return "📱";
        case "Software Engineering":
            return "👨‍💻";
        case "Artificial Intelligence":
            return "🤖";
        case "Data Science":
            return "💻";
        case "Cybersecurity":
            return "🛡️";
    }
    return "📚";
}

export function isCourseFull(enrolledStudents: number, maxStudents: number) {
    return enrolledStudents >= maxStudents;
}

export function getCourseEnrollButtonStyle(enrolledStudents: number, maxStudents: number) {
    if (isCourseFull(enrolledStudents, maxStudents)) {
        return "bg-red-400 cursor-not-allowed";
    }
    return "bg-blue-500  cursor-pointer";
}