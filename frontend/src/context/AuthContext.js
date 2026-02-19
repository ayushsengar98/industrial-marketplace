import React, { createContext, useState, useEffect } from 'react';
import API from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const extractApiError = (error, fallback) => {
    const data = error?.response?.data;
    if (typeof data === 'string' && data.trim()) return data;
    if (data?.message) return data.message;
    if (data?.error) return data.error;
    return fallback;
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setUser(JSON.parse(userData));
    }
    setLoading(false);
  }, []);

  const persistUserSession = (nextUser) => {
    localStorage.setItem('user', JSON.stringify(nextUser));
    if (nextUser.accessToken) {
      localStorage.setItem('token', nextUser.accessToken);
    }
    if (nextUser.refreshToken) {
      localStorage.setItem('refreshToken', nextUser.refreshToken);
    }
    setUser(nextUser);
  };

  const register = async (email, password) => {
    try {
      const response = await API.post('/auth/register', { email, password });
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: extractApiError(error, 'Registration failed') 
      };
    }
  };

  const login = async (email, password) => {
    try {
      const response = await API.post('/auth/login', { email, password });
      const { accessToken, refreshToken, role, userId } = response.data;
      
      const userData = { email, role, userId, accessToken, refreshToken };
      persistUserSession(userData);
      return { success: true, role };
    } catch (error) {
      return { 
        success: false, 
        error: extractApiError(error, 'Login failed') 
      };
    }
  };

  const refreshSession = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      const storedUserRaw = localStorage.getItem('user');
      const activeUser = user || (storedUserRaw ? JSON.parse(storedUserRaw) : null);
      if (!refreshToken || !activeUser) {
        return { success: false, error: 'No active session found' };
      }

      const response = await API.post('/auth/refresh', { refreshToken });
      const { accessToken, role, email } = response.data;
      const updatedUser = {
        ...activeUser,
        accessToken,
        role: role || activeUser.role,
        email: email || activeUser.email
      };
      persistUserSession(updatedUser);
      return { success: true, role: updatedUser.role };
    } catch (error) {
      return {
        success: false,
        error: extractApiError(error, 'Session refresh failed')
      };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, register, login, logout, refreshSession, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
