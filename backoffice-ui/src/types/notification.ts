export interface NotificationResponse {
  id: string;
  orderId: string;
  recipient: string;
  channel: string;
  status: string;
  message: string;
  failureReason?: string | null;
  createdAt: string;
}
