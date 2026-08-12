import axiosClient from './axiosClient';

export interface Supplier {
  id: number;
  code: string;
  name: string;
  phone: string | null;
  email: string | null;
  address: string | null;
  status: boolean;
}

export interface PurchaseOrder {
  id: number;
  code: string;
  supplier: Supplier;
  totalAmount: number;
  note: string | null;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  createdAt: string;
  confirmedAt: string | null;
}

export interface PurchaseOrderItem {
  id: number;
  quantity: number;
  importPrice: number;
  variant: {
    id: number;
    sku: string;
    product: { id: number; name: string };
  };
}

export const supplierApi = {
  search: (params: { q?: string; page?: number; size?: number }) =>
    axiosClient.get('/suppliers', { params }),
  create: (data: { name: string; phone?: string; email?: string; address?: string }) =>
    axiosClient.post<Supplier>('/suppliers', data),
};

export const purchaseOrderApi = {
  search: (params: { status?: string; page?: number; size?: number }) =>
    axiosClient.get('/purchase-orders', { params }),
  getById: (id: number) => axiosClient.get<PurchaseOrder>(`/purchase-orders/${id}`),
  getItems: (id: number) => axiosClient.get<PurchaseOrderItem[]>(`/purchase-orders/${id}/items`),
  create: (data: {
    supplierId: number;
    note?: string;
    items: { variantId: number; quantity: number; importPrice: number }[];
  }) => axiosClient.post<PurchaseOrder>('/purchase-orders', data),
  confirm: (id: number) => axiosClient.patch(`/purchase-orders/${id}/confirm`),
  cancel: (id: number) => axiosClient.patch(`/purchase-orders/${id}/cancel`),
};