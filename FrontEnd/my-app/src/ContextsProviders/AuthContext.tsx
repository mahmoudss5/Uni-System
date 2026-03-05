import { createContext, useContext, useMemo } from "react";
import {
    HandleLogin, HandleRegister, setToken, setUserCache,
    removeToken, removeUserCache,
} from "../Services/authService";
import { getUserDashboardData } from "../Services/userService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { AuthContextType } from "../Interfaces/Auth";

// ─── Context ────────────────────────────────────────────────────────────────

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

// ─── Provider ────────────────────────────────────────────────────────────────

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const queryClient = useQueryClient();

    const loginMutation = useMutation({
        mutationFn: ({ email, password }: { email: string; password: string }) =>
            HandleLogin(email, password),
        onSuccess: async (data) => {
            const token = data.token || data;
            setToken(token);
            const user = await getUserDashboardData(token);
            setUserCache(queryClient, user);
        },
    });

    const registerMutation = useMutation({
        mutationFn: ({
            email, password, username, TeacherCode,
        }: { email: string; password: string; username: string; TeacherCode?: string }) =>
            HandleRegister(email, password, username, TeacherCode),
        onSuccess: async (data) => {
            const token = data.token || data;
            setToken(token);
            const user = await getUserDashboardData(token);
            setUserCache(queryClient, user);
        },
    });

    const login = (email: string, password: string) =>
        loginMutation.mutateAsync({ email, password });

    const register = (email: string, password: string, username: string, TeacherCode?: string) =>
        registerMutation.mutateAsync({ email, password, username, TeacherCode });

    const logout = () => {
        removeToken();
        removeUserCache(queryClient);
    };

    const value = useMemo<AuthContextType>(
        () => ({ login, logout, register }),
    
        [],
    );

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

// ─── Hook ────────────────────────────────────────────────────────────────────

export function useAuth(): AuthContextType {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
    return ctx;
}
