// src/hooks/useNotifications.ts
import { useState, useEffect, useCallback } from "react";
import { toast } from "sonner";
import type { NotificationMessage } from "../../Interfaces/Notification";
import { getToken, getUserId } from "../../Services/authService";
import { webSocketService } from "../../Services/WebSocket Service";
import {
    getNotifications,
    markNotificationAsRead as markNotificationAsReadApi,
    markAllNotificationsAsRead as markAllNotificationsAsReadApi,
} from "../../Services/notifications";

const TYPE_ICONS: Record<string, string> = {
    ENROLLMENT:   "🎓",
    ANNOUNCEMENT: "📢",
    COURSE_UPDATE: "📝",
    GRADE:        "✅",
    SYSTEM:       "🔔",
};

export const useNotifications = () => {
    const [notifications, setNotifications] = useState<NotificationMessage[]>([]);
    const [unreadCount, setUnreadCount]     = useState(0);
    const [connected, setConnected]         = useState(false);

    const token  = getToken();
    const userId = getUserId();

    const handleNewNotification = useCallback((notification: NotificationMessage) => {
        setNotifications((prev) => [notification, ...prev]);

        setUnreadCount((prev) => prev + 1);

        toast(notification.title, {
            description: notification.message,
            id: notification.id.toString(),
            duration: 4000,
            icon: TYPE_ICONS[notification.type] ?? "🔔",
            closeButton: true,
        });
    }, []);

    const handleCountUpdate = useCallback((count: number) => {
        setUnreadCount(count);
    }, []);

    useEffect(() => {
        if (!token || !userId) return;

        webSocketService.connect(userId, handleNewNotification, handleCountUpdate, () => setConnected(true));

        const fetchHistory = async () => {
            try {
                const response = await getNotifications();
                setNotifications(response);
                const unread = response.filter((n: NotificationMessage) => !n.read).length;
                setUnreadCount(unread);
            } catch (err) {
                console.error("Failed to load notification history:", err);
            }
        };

        fetchHistory();

        return () => {
            webSocketService.disconnect();
            setConnected(false);
        };
    }, [userId, token, handleNewNotification, handleCountUpdate]);

    const markNotificationAsRead = async (id: number) => {
        if (!token) return;

 
        setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
        setUnreadCount((prev) => Math.max(0, prev - 1));

        try {
            await markNotificationAsReadApi(id);
        } catch (err) {
            console.error("Failed to mark as read:", err);
        
            setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: false } : n)));
            setUnreadCount((prev) => prev + 1);
        }
    };

    const markAllNotificationsAsRead = async () => {
        if (!token || unreadCount === 0) return;
        toast.success("All notifications marked as read");
    
        setUnreadCount(0);
        setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));

        try {
            await markAllNotificationsAsReadApi();
        } catch (err) {
            console.error("Failed to mark all as read:", err);
            toast.error("Failed to mark all as read");
            const response = await getNotifications();
            setNotifications(response);
            setUnreadCount(response.filter((n: NotificationMessage) => !n.read).length);
        }
    };

    return {
        notifications,
        unreadCount,
        connected,
        markNotificationAsRead,
        markAllNotificationsAsRead,
    };
};