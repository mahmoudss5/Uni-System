import { createContext, useContext } from "react";
import {
    HandleLogin, HandleRegister, setToken, setUserCache,
    removeToken, removeUserCache
} from "../Services/authService";
import { getUser } from "../Services/userService";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Token } from "../Services/config";
import type { AuthContextType } from "../Interfaces/Auth";


export const AuthContext = createContext<AuthContextType | undefined>(undefined);


export const AuthProvider = ({ children }: { children: React.ReactNode }) => {

    const queryClient = useQueryClient();
    const token = localStorage.getItem(Token);

    const { data: user, isLoading, isError } = useQuery({
        queryKey: ["user"],
        queryFn: () => getUser(token ?? ""),
        enabled: !!token
    })

    const loginMutation = useMutation({
        mutationFn: ({ email, password }: { email: string, password: string }) => HandleLogin(email, password),
        onSuccess: async (data) => {
            const token = data.token || data;
            setToken(token);
            const user = await getUser(token);
            setUserCache(queryClient, user);
        }
    })

    const registerMutation = useMutation({
        mutationFn: ({ email, password, username }: { email: string, password: string, username: string }) => HandleRegister(email, password, username),
        onSuccess: async (data) => {
            const token = data.token || data;
            setToken(token);
            const user = await getUser(token);
            setUserCache(queryClient, user);
        }
    })

    const register = async (email: string, password: string, username: string) => {
        return registerMutation.mutateAsync({ email, password, username });
    }
    const logout = () => {
        removeToken()
        removeUserCache(queryClient)
    }

    const login = async (email: string, password: string) => {
        return loginMutation.mutateAsync({ email, password });
    }

    return (
        <AuthContext.Provider value={{ user: user ?? null, login, logout, isLoading, isError, register }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}