import { Bell, Search } from "lucide-react";
import { useAuth } from "../../ContextsProviders/AuthContext";

export default function DashboardHeader() {
    const { user } = useAuth();

    const displayName = user?.name || user?.email.split("@")[0] || "User";
    console.log("DashboardHeader user:", user);
    const initial = displayName.charAt(0).toUpperCase();
    const role = user?.role === "teacher" ? "Teacher" : "Student";

    return (
        <header className="w-full bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between shrink-0">
            <div>
                <h1 className="text-xl font-bold text-gray-800 italic">
                    Welcome back, {displayName}!
                </h1>
                <p className="text-md text-blue-500 mt-0.5 italic">
                    {user?.role === "teacher" ? "Have a great day teaching" : "Have a great day learning"}
                </p>
            </div>

            <div className="flex items-center gap-4">
                <div className="relative hidden sm:block">
                    <Search
                        size={16}
                        className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
                    />
                    <input
                        type="text"
                        placeholder="Search..."
                        className="pl-9 pr-4 py-2 text-sm border border-gray-200 rounded-lg bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent w-56 transition"
                    />
                </div>

                <button className="relative p-2 rounded-full hover:bg-gray-100 transition-colors">
                    <Bell size={20} className="text-gray-600" />
                    <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full ring-2 ring-white" />
                </button>

                <div className="flex items-center gap-2 cursor-pointer">
                    <div className="w-9 h-9 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-sm select-none">
                        {initial}
                    </div>
                    <div className="hidden sm:block leading-tight">
                        <p className="text-md font-semibold text-gray-800">{displayName}</p>
                        <p className="text-s text-gray-500">{role}</p>
                    </div>
                </div>
            </div>
        </header>
    );
}
