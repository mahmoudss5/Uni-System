import axios from "axios";
import { ApiUrl, getHeaders } from "./config";
export async function getTeacherInfo(id: number) {
    try {
        const response = await axios.get(`${ApiUrl}/api/teachers/${id}`, {
            headers: getHeaders(),
        });
        console.log("response", response.data);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            if (error.response) {
                console.log("error.response", error.response.data);
                throw new Error(error.response.data.message || "Teacher info failed");
            }
            if (error.request) {
                console.log("error.request", error.request);
                throw new Error("No response from server");
            }
        }
        console.log("error", error);
        throw new Error("Teacher info failed. Please try again.");
    }
}