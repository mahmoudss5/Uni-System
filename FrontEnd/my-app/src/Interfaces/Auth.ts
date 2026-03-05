import type { Student } from "./student";
import type { Teacher } from "./teacher";

export type AuthUser = Student | Teacher;

export interface AuthContextType {
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    register: (email: string, password: string, username: string, TeacherCode?: string) => Promise<void>;
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
    TeacherCode?: string;
}

export interface AuthRequest {
    email: string;
    password: string;
}
