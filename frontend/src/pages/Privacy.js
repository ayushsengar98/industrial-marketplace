import React from 'react';

function Privacy() {
  return (
    <div className="static-page-container">
      <div className="static-page-card">
        <h1>Privacy Policy</h1>
        <p>
          We use your account and business information only for authentication, platform
          operations, and service communication.
        </p>
        <ul className="static-list">
          <li>Data is processed for login, authorization, and dashboard features.</li>
          <li>Sensitive credentials are never displayed in UI responses.</li>
          <li>Operational logs may be used for reliability and security monitoring.</li>
          <li>You can contact support for account-related privacy requests.</li>
        </ul>
      </div>
    </div>
  );
}

export default Privacy;
