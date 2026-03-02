interface Event {
    id: string;
    date: {
        month: string;
        day: string;
    };
    title: string;
    subtitle: string;
    type: "High Priority" | "Exam" | "Event";
}

interface UpcomingEventsProps {
    events: Event[];
}

export default function UpcomingEvents({ events }: UpcomingEventsProps) {
    const getTypeColor = (type: string) => {
        switch (type) {
            case "High Priority":
                return "bg-red-100 text-red-700";
            case "Exam":
                return "bg-blue-100 text-blue-700";
            case "Event":
                return "bg-green-100 text-green-700";
            default:
                return "bg-gray-100 text-gray-700";
        }
    };

    const getDateColor = (type: string) => {
        switch (type) {
            case "High Priority":
                return "bg-red-50 border-red-200";
            case "Exam":
                return "bg-blue-50 border-blue-200";
            case "Event":
                return "bg-green-50 border-green-200";
            default:
                return "bg-gray-50 border-gray-200";
        }
    };

    const getDateTextColor = (type: string) => {
        switch (type) {
            case "High Priority":
                return "text-red-600";
            case "Exam":
                return "text-blue-600";
            case "Event":
                return "text-green-600";
            default:
                return "text-gray-600";
        }
    };

    const getDateBgColor = (type: string) => {
        switch (type) {
            case "High Priority":
                return "bg-red-500";
            case "Exam":
                return "bg-blue-500";
            case "Event":
                return "bg-green-500";
            default:
                return "bg-gray-500";
        }
    };

    return (
        <div className="bg-white rounded-xl shadow-sm p-6">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-lg font-bold text-gray-800">Upcoming Deadlines & Events</h2>
                <button className="text-blue-500 text-sm font-medium hover:underline">
                    View Calendar
                </button>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {events.map((event) => (
                    <div key={event.id} className="border border-gray-200 rounded-xl p-4 hover:shadow-md transition">
                        <div className="flex gap-4 items-start">
                            <div className={`flex flex-col items-center justify-center w-14 h-14 rounded-lg overflow-hidden flex-shrink-0 ${getDateColor(event.type)}`}>
                                <span className={`text-[10px] font-bold uppercase w-full text-center py-0.5 ${getDateBgColor(event.type)} text-white`}>
                                    {event.date.month}
                                </span>
                                <span className={`text-xl font-bold ${getDateTextColor(event.type)} flex-1 flex items-center`}>
                                    {event.date.day}
                                </span>
                            </div>
                            
                            <div className="flex-1">
                                <h3 className="font-semibold text-sm text-gray-800">{event.title}</h3>
                                <p className="text-xs text-gray-500 mt-0.5 mb-2">{event.subtitle}</p>
                                <span className={`inline-block px-2.5 py-0.5 rounded text-xs font-medium ${getTypeColor(event.type)}`}>
                                    {event.type}
                                </span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
