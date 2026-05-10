export interface CreateOrderRequest {
  customerId: string;
  totalAmount: number;
}

export interface OrderResponse {
  id: string;
  customerId: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}
