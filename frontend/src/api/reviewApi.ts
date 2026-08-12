import axiosClient from './axiosClient';

export interface Review {
  id: number;
  rating: number;
  content: string | null;
  createdAt: string;
  isEdited: boolean;
  replyContent: string | null;
  repliedAt: string | null;
  user: { id: number; username: string; fullName: string | null } | null;
}

export const reviewApi = {
  getByProduct: (productId: number, page = 0, size = 10) =>
    axiosClient.get<{ content: Review[]; totalPages: number }>(`/reviews/product/${productId}`, {
      params: { page, size },
    }),
  getMyReviews: () => axiosClient.get<Review[]>('/reviews/my-reviews'),
  create: (data: { orderItemId: number; rating: number; content?: string }) =>
    axiosClient.post<Review>('/reviews', data),
  reply: (id: number, replyContent: string) => axiosClient.post(`/reviews/${id}/reply`, { replyContent }),
};