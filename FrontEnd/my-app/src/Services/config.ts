export const ApiUrl: string = "http://localhost:8080/";
export const Token: string  = "authToken";
export const getHeaders = () => {
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${localStorage.getItem(Token)}`
    }
}
