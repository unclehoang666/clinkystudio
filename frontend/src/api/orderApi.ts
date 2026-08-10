import axiosClient from './axiosClient';

export interface CheckoutPayload {
  receiverName: string;
  receiverPhone: string;
  shippingAddress: string;
  shippingWard?: string;
  shippingDistrict?: string;
  shippingProvince?: string;
  note?: string;
  couponCode?: string;
  deliveryMethod?: string;
}

export interface Order {
  id: number;
  code: string;
  subtotal: number;
  shippingFee: number;
  discountAmount: number;
  totalAmount: number;
  receiverName: string;
  receiverPhone: string;
  shippingAddress: string;
  createdAt: string;
  status: { id: number; code: string; name: string } | null;
}

export const orderApi = {
  checkout: (payload: CheckoutPayload) => axiosClient.post<Order>('/orders/checkout', payload),
  getMyOrders: () => axiosClient.get<Order[]>('/orders/my-orders'),
  getById: (id: number) => axiosClient.get<Order>(`/orders/${id}`),
  getItems: (id: number) => axiosClient.get(`/orders/${id}/items`),
};