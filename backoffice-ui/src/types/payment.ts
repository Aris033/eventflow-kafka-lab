export interface PaymentResponse {
  id: string;
  orderId: string;
  amount: number;
  status: string;
  failureReason?: string | null;
  createdAt: string;
}
