import { apiConfig } from '../config/apiConfig';
import type { CreateOrderRequest, OrderResponse } from '../types/order';
import { requestJson } from './http';

export const createOrder = (request: CreateOrderRequest): Promise<OrderResponse> =>
  requestJson<OrderResponse>(`${apiConfig.orderServiceUrl}/api/orders`, {
    method: 'POST',
    body: JSON.stringify(request)
  });
