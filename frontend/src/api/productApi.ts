import axiosClient from './axiosClient';

export interface Category {
  id: number;
  name: string;
  code: string;
}

export interface Brand {
  id: number;
  name: string;
  code: string;
}

export interface ProductVariant {
  id: number;
  sku: string;
  price: number;
  quantity: number;
  reservedQuantity: number;
  imageUrl: string | null;
  status: boolean;
}

export interface Product {
  id: number;
  code: string;
  name: string;
  description: string | null;
  brand: Brand | null;
  category: Category | null;
  status: boolean;
  isGiveaway: boolean;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

export const productApi = {
  search: (params: { q?: string; categoryId?: number; brandId?: number; page?: number; size?: number }) =>
    axiosClient.get<PageResponse<Product>>('/products', { params: { status: true, ...params } }),

  getById: (id: number) => axiosClient.get<Product>(`/products/${id}`),

  getImages: (id: number) => axiosClient.get<{ id: number; url: string; sortOrder: number }[]>(`/products/${id}/images`),

  getVariants: (id: number) => axiosClient.get<ProductVariant[]>(`/products/${id}/variants`),

  getVariantAttributes: (variantId: number) =>
    axiosClient.get(`/products/variants/${variantId}/attributes`),
};

export const categoryApi = {
  getAllActive: () => axiosClient.get<Category[]>('/categories/all-active'),
};

export const brandApi = {
  getAllActive: () => axiosClient.get<Brand[]>('/brands/all-active'),
};