import { Client, type StompSubscription } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { NotificationMessage } from "../Interfaces/Notification";
import { getToken } from "../Services/authService";
import { ApiUrl } from "./config";

type NotificationHandler = (notification: NotificationMessage) => void;
type CounterHandler = (count: number) => void;

class WebSocketService {
    private client: Client | null = null;

    private subscriptions: StompSubscription[] = [];

    private connected = false;
    private token = getToken() || "";

    connect(
        _userId: number,
        onNotification: NotificationHandler,
        onCountUpdated: CounterHandler,
        onConnected?: () => void,
    ): void {
        if (this.connected) return;
        this.client = new Client({
            webSocketFactory: () => new SockJS(`${ApiUrl}/ws`),
            connectHeaders: {
                Authorization: `Bearer ${this.token}`,
            },
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("🔔 WebSocket connected");
                this.connected = true;
                onConnected?.();

                const notifSub = this.client!.subscribe(
                    `/user/queue/notifications`,
                    (frame) => {
                        const notification: NotificationMessage = JSON.parse(frame.body);
                        onNotification(notification);
                    }
                );
                const countSub = this.client!.subscribe(
                    `/user/queue/notification-count`,
                    (frame) => {
                        const { unreadCount } = JSON.parse(frame.body);
                        onCountUpdated(unreadCount);
                    }
                );
                this.subscriptions.push(notifSub, countSub);
            },

            onDisconnect: () => {
                console.log("WebSocket disconnected");
                this.connected = false;
            },
            onStompError: (frame) => {
                console.error("STOMP error:", frame.headers["message"]);
            },
        });
        this.client.activate();
    }
    disconnect(): void {
        this.subscriptions.forEach((sub) => sub.unsubscribe());
        this.subscriptions = [];

        this.client?.deactivate();
        this.connected = false;
    }

    isConnected(): boolean {
        return this.connected;
    }
}
export const webSocketService = new WebSocketService();