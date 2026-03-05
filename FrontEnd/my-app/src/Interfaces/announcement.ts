export interface AnnouncementResponse {
    id: number;
    title: string;
    description: string;
    createdAt: string;
    type?: "info" | "warning" | "success" | "default";
}
