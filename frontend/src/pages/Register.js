import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

function Register() {
  const [form, setForm] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    agreedToTerms: false
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const { register } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const getPasswordScore = () => {
    const password = form.password;
    let score = 0;

    if (password.length >= 8) score += 1;
    if (/[A-Z]/.test(password)) score += 1;
    if (/\d/.test(password)) score += 1;
    if (/[^A-Za-z0-9]/.test(password)) score += 1;

    return score;
  };

  const getPasswordStrength = () => {
    const score = getPasswordScore();
    if (score >= 4) return 'strong';
    if (score >= 2) return 'medium';
    return 'weak';
  };

  const getStrengthLabel = () => {
    const strength = getPasswordStrength();
    if (strength === 'strong') return 'Strong';
    if (strength === 'medium') return 'Medium';
    return 'Weak';
  };

  const passwordChecks = {
    length: form.password.length >= 8,
    letter: /[A-Za-z]/.test(form.password),
    number: /\d/.test(form.password),
    match: form.password.length > 0 && form.password === form.confirmPassword
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const normalizedEmail = form.email.trim().toLowerCase();
    if (!normalizedEmail) {
      setError('Business email is required');
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (form.password.length < 8) {
      setError('Password must be at least 8 characters');
      return;
    }

    const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d).+$/;
    if (!passwordPattern.test(form.password)) {
      setError('Password must include at least one letter and one number');
      return;
    }

    if (!form.agreedToTerms) {
      setError('Please accept the terms to create your account');
      return;
    }

    setLoading(true);
    const result = await register(normalizedEmail, form.password);
    setLoading(false);

    if (!result.success) {
      setError(result.error);
      return;
    }

    setSuccess(true);
    setTimeout(() => navigate('/login'), 1800);
  };

  const strength = getPasswordStrength();

  return (
    <div className="auth-container register-page">
      <div className="auth-card register-card">
        <div className="register-header">
          <p className="register-eyebrow">Industrial Marketplace</p>
          <h2>Create Your Business Account</h2>
          <p className="register-subtitle">
            Register to source equipment, manage suppliers, and track procurement securely.
          </p>
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && (
          <div className="success-message">
            Registration successful - redirecting to login...
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Business Email</label>
            <input
              id="email"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              disabled={loading || success}
              placeholder="name@company.com"
              autoComplete="email"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
              disabled={loading || success}
              autoComplete="new-password"
              placeholder="Minimum 8 characters"
            />

            {form.password && (
              <div className="password-strength">
                <div className="password-strength-header">
                  <span>Password strength</span>
                  <span className={`strength-badge ${strength}`}>{getStrengthLabel()}</span>
                </div>
                <div className="strength-bar-track">
                  <span className={`strength-bar-fill ${strength}`} />
                </div>
              </div>
            )}
            <ul className="password-rule-list">
              <li className={passwordChecks.length ? 'valid' : ''}>At least 8 characters</li>
              <li className={passwordChecks.letter ? 'valid' : ''}>Contains a letter</li>
              <li className={passwordChecks.number ? 'valid' : ''}>Contains a number</li>
              <li className={passwordChecks.match ? 'valid' : ''}>Passwords match</li>
            </ul>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              id="confirmPassword"
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              required
              disabled={loading || success}
              autoComplete="new-password"
            />
          </div>

          <label className="terms-row" htmlFor="agreedToTerms">
            <input
              id="agreedToTerms"
              type="checkbox"
              name="agreedToTerms"
              checked={form.agreedToTerms}
              onChange={handleChange}
              disabled={loading || success}
            />
            <span>I agree to the Marketplace Terms and Data Policy.</span>
          </label>

          <button type="submit" className="btn-primary btn-full" disabled={loading || success}>
            {loading ? 'Creating account...' : success ? 'Registered' : 'Register Account'}
          </button>
        </form>

        <p className="auth-link">
          Already registered? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
