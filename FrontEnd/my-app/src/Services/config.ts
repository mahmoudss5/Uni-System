export const ApiUrl: string = "http://localhost:8080";
export const Token: string  = "authToken";

export const getHeaders = () => {
    const headers: Record<string, string> = { "Content-Type": "application/json" };

    return headers;
}

export const getAuthHeaders = () => {
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${localStorage.getItem(Token)}`
    }
}