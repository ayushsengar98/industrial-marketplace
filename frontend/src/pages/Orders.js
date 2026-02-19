import React, { useContext, useEffect, useState } from 'react';
import API from '../services/api';
import { AuthContext } from '../context/AuthContext';

function Orders() {
  const { user } = useContext(AuthContext);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingOrderId, setUpdatingOrderId] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const endpoint =
        user?.role === 'ADMIN'
          ? '/api/orders/all'
          : user?.role === 'VENDOR'
            ? '/api/orders/vendor'
            : '/api/orders/my';
      const response = await API.get(endpoint);
      setOrders(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      const apiMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'Failed to load orders';
      setMessage(apiMessage);
    } finally {
      setLoading(false);
    }
  };

  const canManageStatus = user?.role === 'VENDOR' || user?.role === 'ADMIN';

  const nextStatusOptions = (current) => {
    if (current === 'PLACED') return ['PROCESSING', 'CANCELLED'];
    if (current === 'PROCESSING') return ['SHIPPED', 'CANCELLED'];
    if (current === 'SHIPPED') return ['DELIVERED'];
    return [];
  };

  const updateStatus = async (orderId, status) => {
    setUpdatingOrderId(orderId);
    setMessage('');
    try {
      await API.put(`/api/orders/${orderId}/status`, { status });
      await fetchOrders();
    } catch (err) {
      const apiMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'Failed to update order status';
      setMessage(apiMessage);
    } finally {
      setUpdatingOrderId(null);
    }
  };

  if (loading) return <div className="loading">Loading orders...</div>;

  return (
    <div className="orders-page-container">
      <h1>{user?.role === 'ADMIN' ? 'All Orders' : user?.role === 'VENDOR' ? 'Vendor Orders' : 'My Orders'}</h1>
      {message && <div className="message error">{message}</div>}

      {orders.length === 0 ? (
        <p className="empty-state">No orders yet.</p>
      ) : (
        <div className="orders-list">
          {orders.map((order) => (
            <div key={order.id} className="order-card">
              <div className="order-card-header">
                <h3>Order #{order.id}</h3>
                <span className="status-badge status-approved">{order.status}</span>
              </div>
              <p className="dashboard-muted">Placed: {new Date(order.createdAt).toLocaleString()}</p>
              <p className="dashboard-muted">Total: Rs {order.totalAmount?.toFixed?.(2) ?? order.totalAmount}</p>

              {canManageStatus && (
                <div className="status-actions">
                  {nextStatusOptions(order.status).map((status) => (
                    <button
                      key={`${order.id}-${status}`}
                      className="btn-outline"
                      onClick={() => updateStatus(order.id, status)}
                      disabled={updatingOrderId === order.id}
                    >
                      {updatingOrderId === order.id ? 'Updating...' : `Mark ${status}`}
                    </button>
                  ))}
                </div>
              )}

              <div className="order-items">
                {order.items?.map((item, idx) => (
                  <div key={`${order.id}-${idx}`} className="order-item-row">
                    <span>{item.productName}</span>
                    <span>Qty {item.quantity}</span>
                    <span>Rs {item.lineTotal?.toFixed?.(2) ?? item.lineTotal}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Orders;
