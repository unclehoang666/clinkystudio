import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';
import axiosClient from '../api/axiosClient';

interface User {
  username: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

interface RegisterData {
  username: string;
  password: string;
  fullName?: string;
  email?: string;
  phone?: string;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (username: string, password: string) => {
    const res = await axiosClient.post('/auth/login', { username, password });
    const { token, username: uname, role } = res.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ username: uname, role }));
    setUser({ username: uname, role });
  };

  const register = async (data: RegisterData) => {
    await axiosClient.post('/auth/register', data);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}