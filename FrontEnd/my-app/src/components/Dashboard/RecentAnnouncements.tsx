import { a } from "framer-motion/client";
import { Clock, DollarSign, Briefcase, BookOpen } from "lucide-react";

interface Announcement {
    id: string;
    title: string;
    description: string;
    timeAgo: string;
    type: "info" | "warning" | "success" | "default";
}

interface RecentAnnouncementsProps {
    announcements: Announcement[];
}

export default function RecentAnnouncements({ announcements }: RecentAnnouncementsProps) {
    const getIconAndColor = (type: string) => {
        switch (type) {
            case "info":
                return { icon: Clock, bgColor: "bg-blue-100", iconColor: "text-blue-500", borderColor: "border-blue-500" };
            case "warning":
                return { icon: DollarSign, bgColor: "bg-yellow-100", iconColor: "text-yellow-500", borderColor: "border-yellow-500" };
            case "success":
                return { icon: Briefcase, bgColor: "bg-green-100", iconColor: "text-green-500", borderColor: "border-green-500" };
            default:
                return { icon: BookOpen, bgColor: "bg-purple-100", iconColor: "text-purple-500", borderColor: "border-purple-500" };
        }
    };

    return (
        <div className="bg-white rounded-xl shadow-sm p-6 h-full flex flex-col">
            <h2 className="text-lg font-bold text-gray-800 mb-5">Recent Announcements</h2>
            
            <div className="space-y-1 flex-1">
                {announcements.map((announcement) => {
                    const { icon: Icon, bgColor, iconColor, borderColor } = getIconAndColor(announcement.type);
                    
                    return (
                        <div key={announcement.id} className={`flex gap-3 pl-4 py-3 border-l-3 ${borderColor}`}>
                            <div className={`w-9 h-9 ${bgColor} rounded-full flex items-center justify-center flex-shrink-0`}>
                                <Icon className={`${iconColor} w-4 h-4`} />
                            </div>
                            <div className="flex-1">
                                <h3 className="font-semibold text-sm text-gray-800">{announcement.title}</h3>
                                <p className="text-xs text-gray-500 mt-0.5">{announcement.description}</p>
                                <span className="text-xs text-gray-400 mt-1 block">{announcement.timeAgo}</span>
                            </div>
                        </div>
                    );
                })}
            </div>
            
            {announcements.length === 0 && (
                <div className="text-center text-gray-500 mt-10 text-xl font-medium">
                    No announcements at the moment.
                </div>
            )
            }
            {announcements.length > 0 && (
                <button className="w-full mt-4 py-2.5 text-sm text-gray-600 hover:bg-gray-50 rounded-lg border border-gray-200 transition font-medium">
                    View All Announcements
                </button>
            )}
        </div>
    );
}
