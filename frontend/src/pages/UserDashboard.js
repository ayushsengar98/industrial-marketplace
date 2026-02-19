import React, { useContext, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import API from '../services/api';
import { AuthContext } from '../context/AuthContext';

function UserDashboard() {
  const { user, refreshSession } = useContext(AuthContext);
  const navigate = useNavigate();
  const [vendorStatus, setVendorStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [syncingRole, setSyncingRole] = useState(false);
  const [roleSyncAttempted, setRoleSyncAttempted] = useState(false);
  const [form, setForm] = useState({ companyName: '', gstNumber: '' });
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchVendorStatus();
  }, []);

  useEffect(() => {
    const syncApprovedVendorRole = async () => {
      if (!vendorStatus || vendorStatus.status !== 'APPROVED') return;

      if (user?.role === 'VENDOR') {
        navigate('/vendor/dashboard', { replace: true });
        return;
      }

      if (roleSyncAttempted || syncingRole) return;

      setSyncingRole(true);
      setRoleSyncAttempted(true);
      const result = await refreshSession();
      setSyncingRole(false);

      if (result.success && result.role === 'VENDOR') {
        navigate('/vendor/dashboard', { replace: true });
      } else {
        setMessage({
          type: 'info',
          text: 'Your vendor account is approved. Please login again to load vendor access.'
        });
      }
    };

    syncApprovedVendorRole();
  }, [vendorStatus, user, refreshSession, roleSyncAttempted, syncingRole, navigate]);

  const fetchVendorStatus = async () => {
    try {
      const response = await API.get('/api/vendor/status');
      setVendorStatus(response.data);
    } catch (error) {
      setVendorStatus(null);
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setMessage({ type: '', text: '' });

    try {
      const response = await API.post('/api/vendor/apply', form);
      setVendorStatus(response.data);
      setMessage({ type: 'success', text: 'Vendor request submitted. Our team will review it shortly.' });
    } catch (error) {
      const networkMessage =
        error?.code === 'ERR_NETWORK'
          ? 'Unable to reach backend service. Please check gateway/auth/vendor services and retry.'
          : '';
      const apiMessage =
        networkMessage ||
        error?.response?.data?.message ||
        error?.response?.data?.error ||
        (typeof error?.response?.data === 'string' ? error.response.data : '') ||
        (error?.response?.status ? `Request failed (${error.response.status}). Please login again and retry.` : '') ||
        'Unable to submit request right now.';
      setMessage({ type: 'error', text: apiMessage });
    } finally {
      setSubmitting(false);
    }
  };

  const renderVendorRequestSection = () => {
    if (loading) {
      return <p className="dashboard-muted">Loading vendor application status...</p>;
    }

    if (!vendorStatus) {
      return (
        <div className="dashboard-card">
          <h3>Become a Vendor</h3>
          <p className="dashboard-muted">
            Ready to sell on the marketplace? Submit your company details to request vendor access.
          </p>
          <form className="vendor-request-form" onSubmit={handleApply}>
            <div className="form-group">
              <label htmlFor="companyName">Company Name</label>
              <input
                id="companyName"
                type="text"
                value={form.companyName}
                onChange={(e) => setForm((prev) => ({ ...prev, companyName: e.target.value }))}
                required
                disabled={submitting}
              />
            </div>
            <div className="form-group">
              <label htmlFor="gstNumber">GST Number</label>
              <input
                id="gstNumber"
                type="text"
                value={form.gstNumber}
                onChange={(e) => setForm((prev) => ({ ...prev, gstNumber: e.target.value }))}
                required
                disabled={submitting}
              />
            </div>
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Submitting...' : 'Raise Vendor Request'}
            </button>
          </form>
        </div>
      );
    }

    if (vendorStatus.status === 'PENDING') {
      return (
        <div className="dashboard-card status-card pending">
          <h3>Vendor Request Pending</h3>
          <p className="dashboard-muted">
            Your application for <strong>{vendorStatus.companyName}</strong> is under review.
          </p>
        </div>
      );
    }

    if (vendorStatus.status === 'APPROVED') {
      return (
        <div className="dashboard-card status-card approved">
          <h3>Vendor Request Approved</h3>
          <p className="dashboard-muted">
            Your company <strong>{vendorStatus.companyName}</strong> is approved.
            {user?.role !== 'VENDOR' && ' Syncing your vendor access...'}
          </p>
          <Link className="btn-primary inline-action" to="/vendor/dashboard">
            Open Vendor Dashboard
          </Link>
        </div>
      );
    }

    return (
      <div className="dashboard-card status-card rejected">
        <h3>Vendor Request Rejected</h3>
        <p className="dashboard-muted">
          Your previous request was rejected. Update your details and submit a fresh request.
        </p>
      </div>
    );
  };

  return (
    <div className="dashboard-container">
      <h1>User Dashboard</h1>

      {message.text && <div className={`message ${message.type}`}>{message.text}</div>}

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h3>Welcome</h3>
          <p className="dashboard-muted">Signed in as {user?.email}</p>
          <p className="dashboard-muted">
            Role:
            <span className="role-pill static">{user?.role}</span>
          </p>
          <Link className="btn-primary inline-action" to="/products">
            Browse Products
          </Link>
        </div>

        {renderVendorRequestSection()}
      </div>
    </div>
  );
}

export default UserDashboard;
