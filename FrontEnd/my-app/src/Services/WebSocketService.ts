// Services/webSocketService.ts
import { Client, type StompSubscription } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { NotificationMessage } from "../Interfaces/Notification";
import type { AnnouncementCourseResponse } from "../Interfaces/announcement";
import { getToken } from "./authService";
import { ApiUrl } from "./config";

type NotificationHandler = (notification: NotificationMessage) => void;
type AnnouncementHandler = (announcement: AnnouncementCourseResponse) => void;

class WebSocketService {
    private client: Client | null = null;
    private subscriptions: StompSubscription[] = [];
    private connected = false;
    private token = getToken() || "";

    // queue of callbacks waiting for connection to be ready
    // both connect() and subscribeToAnnouncements() use this
    private pendingCallbacks: (() => void)[] = [];

    // ── THE KEY METHOD ──────────────────────────────────────────────────────
    // guarantees connection exists before running the callback
    // used by BOTH connect() and subscribeToAnnouncements()
    private ensureConnected(onReady: () => void): void {
        // already connected → run immediately, no waiting
        if (this.connected) {
            onReady();
            return;
        }

        // not connected yet → queue the callback
        // it will run once onConnect fires
        this.pendingCallbacks.push(onReady);

        // already connecting (client exists but handshake not done)
        // just wait — pendingCallbacks will fire when ready
        if (this.client) return;

        // nothing started yet → create the client and start connection
        this.client = new Client({
            webSocketFactory: () => new SockJS(`${ApiUrl}/ws`),
            connectHeaders: {
                Authorization: `Bearer ${this.token}`,
            },
            reconnectDelay: 5000,

            onConnect: () => {
                console.log("🔔 WebSocket connected");
                this.connected = true;

                // drain the queue — run all pending callbacks
                // this handles BOTH notification and announcement subscriptions
                this.pendingCallbacks.forEach((cb) => cb());
                this.pendingCallbacks = [];
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

    connect(
        _userId: number,
        onNotification: NotificationHandler,
        onConnected?: () => void,
    ): void {
        this.ensureConnected(() => {
            const notifSub = this.client!.subscribe(
                `/user/queue/notifications`,
                (frame) => {
                    const notification: NotificationMessage = JSON.parse(frame.body);
                    onNotification(notification);
                },
            );
            this.subscriptions.push(notifSub);
            onConnected?.();
        });
    }
    private announcementSub: StompSubscription | null = null;

    subscribeToAnnouncements(onAnnouncement: AnnouncementHandler): void {
        this.ensureConnected(() => {
            const destination = `/user/queue/announcements`;
            this.announcementSub = this.client!.subscribe(
                destination,
                (frame) => {
                    const announcement: AnnouncementCourseResponse = JSON.parse(
                        frame.body,
                    );
                    onAnnouncement(announcement);
                },
            );
            this.subscriptions.push(this.announcementSub);
        });
    }

    // ✅ unsubscribes ONLY from announcements
    // notifications and connection stay alive
    unsubscribeFromAnnouncements(): void {
        if (this.announcementSub) {
            this.announcementSub.unsubscribe();
            // remove from subscriptions array
            this.subscriptions = this.subscriptions.filter(
                (sub) => sub !== this.announcementSub,
            );
            this.announcementSub = null;
        }
    }

    // ── FULL DISCONNECT ─────────────────────────────────────────────────────
    // only call this on logout
    disconnect(): void {
        this.subscriptions.forEach((sub) => sub.unsubscribe());
        this.subscriptions = [];
        this.pendingCallbacks = [];
        this.announcementSub = null;
        this.client?.deactivate();
        this.connected = false;
    }

    isConnected(): boolean {
        return this.connected;
    }
}

export const webSocketService = new WebSocketService();
