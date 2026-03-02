import { Navigate } from "react-router-dom";
import { useAuth } from "../../ContextsProviders/AuthContext";
interface ProtectedRouteProps {
    children: React.ReactNode;
}

export default function ProtectedRoute({ children }: ProtectedRouteProps) {
    const { user, isLoading, isError } = useAuth();

    if (isLoading) {
        return (
            <div className="min-h-screen bg-[#242424] flex items-center justify-center">
                <div className="flex flex-col items-center gap-4">
                    <div className="w-12 h-12 border-4 border-amber-900/30 border-t-amber-500 rounded-full animate-spin"></div>
                    <p className="text-gray-400 text-sm">Loading...</p>
                </div>
            </div>
        );
    }
 // if the user is not logged in, redirect to the login page
    if (!user) {
        return <Navigate to="/" replace />;
    }

    return children;



}

