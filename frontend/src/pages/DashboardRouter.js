import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import UserDashboard from './UserDashboard';

function DashboardRouter() {
  const { user } = useContext(AuthContext);

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role === 'ADMIN') {
    return <Navigate to="/admin" replace />;
  }

  if (user.role === 'VENDOR') {
    return <Navigate to="/vendor/dashboard" replace />;
  }

  return <UserDashboard />;
}

export default DashboardRouter;
