import type { User } from "./user";

export interface AuthContextType {
    user: User | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    register: (email: string, password: string, username: string) => Promise<void>;
    isLoading: boolean;
    isError: boolean;
}
export interface MyTokenPayload {
    roles: string[];
    userName: string;
    userId: number;
    sub: string;     
    iat: number;       
    exp: number;
}

export interface RegisterRequest {
    email: string;
    password: string;
    username: string;
}

export interface AuthRequest {
    email: string;
    password: string;
}
