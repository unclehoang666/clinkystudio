import axiosClient from './axiosClient';

export interface WishlistItem {
  id: number;
  createdAt: string;
  product: {
    id: number;
    name: string;
    isGiveaway: boolean;
    brand: { id: number; name: string } | null;
  };
}

export const wishlistApi = {
  getMyWishlist: () => axiosClient.get<WishlistItem[]>('/wishlist'),
  check: (productId: number) => axiosClient.get<{ wishlisted: boolean }>(`/wishlist/check/${productId}`),
  add: (productId: number) => axiosClient.post(`/wishlist/${productId}`),
  remove: (productId: number) => axiosClient.delete(`/wishlist/${productId}`),
};