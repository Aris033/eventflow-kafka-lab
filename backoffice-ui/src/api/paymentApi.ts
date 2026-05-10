import { apiConfig } from '../config/apiConfig';
import type { PaymentResponse } from '../types/payment';
import { requestJson } from './http';

export const getPaymentByOrderId = (orderId: string): Promise<PaymentResponse> =>
  requestJson<PaymentResponse>(`${apiConfig.paymentServiceUrl}/api/payments/order/${orderId}`);
