import React from 'react';
import { useNavigate } from 'react-router-dom';

function Home() {
  const navigate = useNavigate();

  return (
    <>
      <header className="hero">
        <div className="hero-content">
          <p className="home-eyebrow">Industrial procurement made reliable</p>
          <h1>B2B Industrial Parts Marketplace</h1>
          <p>Connect with verified manufacturers and suppliers across core industrial categories.</p>
          <div className="hero-buttons">
            <button className="btn-primary" onClick={() => navigate('/products')}>
              Start Buying
            </button>
            <button className="btn-secondary" onClick={() => navigate('/register')}>
              Start Selling
            </button>
          </div>
        </div>
      </header>

      <section className="features">
        <h2>Why Choose Our Marketplace?</h2>
        <div className="feature-grid">
          <div className="feature-card">
            <div className="feature-icon-badge">SEC</div>
            <h3>Secure Platform</h3>
            <p>Enterprise-grade security with JWT authentication.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-badge">OPS</div>
            <h3>Real-time Updates</h3>
            <p>Instant notifications for requests, approvals, and inventory actions.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-badge">NET</div>
            <h3>Global Network</h3>
            <p>Connect with verified vendors and procurement teams worldwide.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-badge">INV</div>
            <h3>Easy Management</h3>
            <p>Simple product listing and inventory controls for growing businesses.</p>
          </div>
        </div>
      </section>

      <section className="stats">
        <div className="stat-container">
          <div className="stat-item">
            <h3>1000+</h3>
            <p>Products</p>
          </div>
          <div className="stat-item">
            <h3>500+</h3>
            <p>Vendors</p>
          </div>
          <div className="stat-item">
            <h3>50+</h3>
            <p>Categories</p>
          </div>
          <div className="stat-item">
            <h3>10K+</h3>
            <p>Happy Customers</p>
          </div>
        </div>
      </section>

      <section className="how-it-works">
        <h2>How It Works</h2>
        <div className="steps">
          <div className="step">
            <div className="step-number">1</div>
            <h3>Register</h3>
            <p>Create your account as buyer or vendor.</p>
          </div>
          <div className="step">
            <div className="step-number">2</div>
            <h3>Get Verified</h3>
            <p>Vendor requests are reviewed and approved by admins.</p>
          </div>
          <div className="step">
            <div className="step-number">3</div>
            <h3>Start Trading</h3>
            <p>List products, discover suppliers, and manage orders.</p>
          </div>
        </div>
      </section>

      <section className="cta">
        <div className="cta-content">
          <h2>Ready to Start Your Journey?</h2>
          <p>Join businesses already using this platform for industrial sourcing and sales.</p>
          <button className="btn-primary btn-large" onClick={() => navigate('/register')}>
            Get Started Now
          </button>
        </div>
      </section>
    </>
  );
}

export default Home;
