export const ApiUrl: string = "http://localhost:8080";
export const Token: string  = "authToken";

export const getHeaders = () => {
    const headers: Record<string, string> = { "Content-Type": "application/json" };

    if (localStorage.getItem(Token)) {
        headers["Authorization"] = `Bearer ${localStorage.getItem(Token)}`;
    }

    return headers;
}
