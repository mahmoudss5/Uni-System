import { CheckCircle, Clock, BookOpen, Star } from "lucide-react";
import StatsCard from "../components/Dashboard/StatsCard";
import EnrolledCourses from "../components/Dashboard/EnrolledCourses";
import RecentAnnouncements from "../components/Dashboard/RecentAnnouncements";
import UpcomingEvents from "../components/Dashboard/UpcomingEvents";

const enrolledCourses = [
    { courseCode: "CS-301", courseName: "Software Engineering", instructor: "Dr. Sarah Johnson", credits: 3, status: "In Progress" as const },
    { courseCode: "CS-305", courseName: "Database Systems", instructor: "Prof. Michael Chen", credits: 4, status: "In Progress" as const },
    { courseCode: "MATH-401", courseName: "Linear Algebra", instructor: "Dr. Emily Watson", credits: 3, status: "In Progress" as const },
    { courseCode: "CS-310", courseName: "Web Development", instructor: "Prof. James Miller", credits: 3, status: "Pending" as const },
    { courseCode: "ENG-201", courseName: "Technical Writing", instructor: "Dr. Lisa Anderson", credits: 2, status: "In Progress" as const },
];

const announcements = [
    {
        id: "1",
        title: "Midterm Exam Schedule Released",
        description: "Check your courses pages for exam dates and locations",
        timeAgo: "2 days ago",
        type: "info" as const,
    },
    {
        id: "2",
        title: "Tuition Fee Deadline",
        description: "Payment for spring 2026 semester is due by March 15",
        timeAgo: "4 days ago",
        type: "warning" as const,
    },
    {
        id: "3",
        title: "Career Fair Registration Open",
        description: "Annual career fair scheduled for March 20–21",
        timeAgo: "1 week ago",
        type: "success" as const,
    },
    {
        id: "4",
        title: "Library Extended Hours",
        description: "Open 24/7 during exam period starting March 10",
        timeAgo: "1 week ago",
        type: "default" as const,
    },
];

const upcomingEvents = [
    {
        id: "1",
        date: { month: "MAR", day: "15" },
        title: "CS-301 Project Due",
        subtitle: "Software Engineering",
        type: "High Priority" as const,
    },
    {
        id: "2",
        date: { month: "MAR", day: "18" },
        title: "CS-305 Midterm Exam",
        subtitle: "Database Systems",
        type: "Exam" as const,
    },
    {
        id: "3",
        date: { month: "MAR", day: "20" },
        title: "Career Fair 2026",
        subtitle: "Main Campus Hall",
        type: "Event" as const,
    },
];

export default function Dashboard() {
    return (
        <main className="flex flex-col bg-gray-100 min-h-full py-8 px-8 gap-6">
            {/* Stats Row */}
            <div className="grid grid-cols-4 gap-5">
                <StatsCard
                    title="Current GPA"
                    value="3.85"
                    subtitle="Out of 4.00"
                    icon={CheckCircle}
                    iconBgColor="bg-blue-100"
                    iconColor="text-blue-500"
                    valueColor="text-blue-600"
                />
                <StatsCard
                    title="Total Credits"
                    value="85 / 130"
                    subtitle="65% Completed"
                    icon={Clock}
                    iconBgColor="bg-cyan-100"
                    iconColor="text-cyan-500"
                    valueColor="text-cyan-600"
                />
                <StatsCard
                    title="Enrolled Courses"
                    value="5"
                    subtitle="Current Semester"
                    icon={BookOpen}
                    iconBgColor="bg-purple-100"
                    iconColor="text-purple-500"
                    valueColor="text-purple-600"
                />
                <StatsCard
                    title="Academic Standing"
                    value="Excellent"
                    subtitle="Keep it up!"
                    icon={Star}
                    iconBgColor="bg-yellow-100"
                    iconColor="text-yellow-500"
                    valueColor="text-yellow-500"
                />
            </div>

            {/* Middle Row: Enrolled Courses + Announcements */}
            <div className="grid grid-cols-3 gap-5">
                <div className="col-span-2 bg-white rounded-lg shadow-md p-6">
                    <EnrolledCourses courses={enrolledCourses} semester="Spring 2026" />
                </div>
                <div className="col-span-1 bg-white rounded-lg shadow-md p-6">
                    <RecentAnnouncements announcements={announcements} />
                </div>
            </div>

            {/* Bottom Row: Upcoming Events */}
            <UpcomingEvents events={upcomingEvents} />
        </main>
    );
}
