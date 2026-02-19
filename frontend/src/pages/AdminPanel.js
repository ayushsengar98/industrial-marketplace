import React, { useState, useEffect } from 'react';
import API from '../services/api';

function AdminPanel() {
  const [pendingVendors, setPendingVendors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchPendingVendors();
  }, []);

  const fetchPendingVendors = async () => {
    try {
      const response = await API.get('/api/vendor/pending');
      setPendingVendors(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Failed to fetch vendors', err);
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    try {
      await API.post(`/api/vendor/approve/${id}`);
      setMessage({ type: 'success', text: 'Vendor approved successfully!' });
      fetchPendingVendors();
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to approve vendor' });
    }
  };

  const handleReject = async (id) => {
    if (window.confirm('Are you sure you want to reject this vendor?')) {
      try {
        await API.post(`/api/vendor/reject/${id}`);
        setMessage({ type: 'success', text: 'Vendor rejected successfully!' });
        fetchPendingVendors();
        setTimeout(() => setMessage({ type: '', text: '' }), 3000);
      } catch (err) {
        setMessage({ type: 'error', text: 'Failed to reject vendor' });
      }
    }
  };

  if (loading) return <div className="loading">Loading Admin Panel...</div>;

  return (
    <div className="admin-container">
      <h1>Admin Dashboard</h1>
      {message.text && <div className={`message ${message.type}`}>{message.text}</div>}
      
      <div className="stats-grid">
        <div className="stat-card" style={{ background: '#1a237e' }}>
          <h3>{pendingVendors.length}</h3>
          <p>Pending Vendors</p>
        </div>
      </div>

      <div className="pending-vendors">
        <h2>Pending Vendor Applications ({pendingVendors.length})</h2>
        {pendingVendors.length === 0 ? (
          <p className="empty-state">No pending applications</p>
        ) : (
          pendingVendors.map(vendor => (
            <div key={vendor.id} className="vendor-row">
              <div className="vendor-info">
                <h3>{vendor.companyName}</h3>
                <p><strong>Email:</strong> {vendor.email}</p>
                <p><strong>GST:</strong> {vendor.gstNumber}</p>
                <p><strong>Applied:</strong> {new Date().toLocaleDateString()}</p>
                <span className="status-badge status-pending">PENDING</span>
              </div>
              <div className="action-buttons">
                <button onClick={() => handleApprove(vendor.id)} className="approve-btn">
                  Approve
                </button>
                <button onClick={() => handleReject(vendor.id)} className="reject-btn">
                  Reject
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default AdminPanel;
