import { apiConfig } from '../config/apiConfig';
import type { NotificationResponse } from '../types/notification';
import { requestJson } from './http';

export const getNotificationsByOrderId = (orderId: string): Promise<NotificationResponse[]> =>
  requestJson<NotificationResponse[]>(`${apiConfig.notificationServiceUrl}/api/notifications/order/${orderId}`);
