import axiosClient from './axiosClient';

export interface UserProfile {
  id: number;
  username: string;
  fullName: string | null;
  email: string | null;
  phone: string | null;
  gender: string | null;
  dateOfBirth: string | null;
  role: { id: number; code: string; name: string };
}

export interface Position {
  id: number;
  code: string;
  name: string;
  description: string | null;
}

export interface Employee {
  id: number;
  code: string;
  status: boolean;
  user: UserProfile;
  position: Position | null;
}

export const userApi = {
  getMe: () => axiosClient.get<UserProfile>('/users/me'),
  updateMe: (data: Partial<UserProfile> & { dateOfBirth?: string }) =>
    axiosClient.put<UserProfile>('/users/me', data),
  changePassword: (currentPassword: string, newPassword: string) =>
    axiosClient.put('/users/me/password', { currentPassword, newPassword }),
  search: (params: { q?: string; page?: number; size?: number }) =>
    axiosClient.get('/users', { params }),
};

export const employeeApi = {
  getMyProfile: () => axiosClient.get<Employee>('/employees/me'),
  search: (params: { q?: string; status?: boolean; page?: number; size?: number }) =>
    axiosClient.get('/employees', { params }),
  create: (data: {
    username: string;
    password: string;
    fullName?: string;
    email?: string;
    phone?: string;
    positionId?: number;
    role: string;
  }) => axiosClient.post<Employee>('/employees', data),
  update: (id: number, data: Partial<{ fullName: string; email: string; phone: string; positionId: number; status: boolean }>) =>
    axiosClient.put<Employee>(`/employees/${id}`, data),
};

export const positionApi = {
  getAll: () => axiosClient.get<Position[]>('/positions'),
  create: (data: { name: string; description?: string }) => axiosClient.post<Position>('/positions', data),
};