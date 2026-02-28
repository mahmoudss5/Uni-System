import { ApiUrl, Token, getHeaders } from "./config";

interface RegisterRequest {
    email: string;
    password: string;
    username: string;
}
export async function HandleRegister(email: string, password: string, username: string) {
    const request: RegisterRequest = {
        email,
        password,
        username,
    }
    const response = await fetch(`${ApiUrl}/api/auth/register`, {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(request),
    })
}