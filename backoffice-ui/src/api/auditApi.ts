import { apiConfig } from '../config/apiConfig';
import type { AuditEventResponse } from '../types/audit';
import { requestJson } from './http';

export const getAuditEventsByOrderId = (orderId: string): Promise<AuditEventResponse[]> =>
  requestJson<AuditEventResponse[]>(`${apiConfig.auditServiceUrl}/api/audit/events/order/${orderId}`);
