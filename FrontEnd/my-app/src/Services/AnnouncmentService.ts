import axios from "axios";
import { ApiUrl } from "./config";
import { getAuthHeaders } from "./config";


export async function getAllAnnouncements() {
    try {
        const response = await axios.get(`${ApiUrl}/api/announcements`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    }catch(error){
        if(axios.isAxiosError(error)){
            if(error.response){
                throw new Error(error.response.data.message);
            }
            if(error.request){
                throw new Error("No response from server");
            }
        }
        throw new Error("Error fetching announcements");
    }
    throw new Error("Error fetching announcements");
}


export async function getAnnouncementByCourseId(courseId: number) {
    try {
        const response = await axios.get(`${ApiUrl}/api/announcements/getAllByCourseId/${courseId}`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            if (error.response) {
                throw new Error(error.response.data.message);
            }
            if (error.request) {
                throw new Error("No response from server");
            }
        }
        throw new Error("Error fetching announcements");
    }
    throw new Error("Error fetching announcements");
}
