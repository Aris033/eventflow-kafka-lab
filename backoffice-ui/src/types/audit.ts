export interface AuditEventResponse {
  id: string;
  eventId: string;
  correlationId: string;
  orderId?: string | null;
  eventType: string;
  sourceTopic: string;
  messageKey?: string | null;
  payload: string;
  occurredAt: string;
  receivedAt: string;
}
