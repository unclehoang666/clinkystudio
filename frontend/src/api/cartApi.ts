import axiosClient from './axiosClient';

export interface CartItem {
  id: number;
  quantity: number;
  variant: {
    id: number;
    sku: string;
    price: number;
    quantity: number;
    reservedQuantity: number;
    imageUrl: string | null;
    product: {
      id: number;
      name: string;
    };
  };
}

export const cartApi = {
  getCart: () => axiosClient.get<CartItem[]>('/cart'),
  getTotal: () => axiosClient.get<{ total: number }>('/cart/total'),
  addItem: (variantId: number, quantity: number) =>
    axiosClient.post('/cart/items', { variantId, quantity }),
  updateQuantity: (variantId: number, quantity: number) =>
    axiosClient.put(`/cart/items/${variantId}`, { quantity }),
  removeItem: (variantId: number) => axiosClient.delete(`/cart/items/${variantId}`),
  clearCart: () => axiosClient.delete('/cart'),
};