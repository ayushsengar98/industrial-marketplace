import React from 'react';

function Terms() {
  return (
    <div className="static-page-container">
      <div className="static-page-card">
        <h1>Terms of Service</h1>
        <p>
          By using this platform, you agree to provide accurate account details and follow
          responsible procurement and listing practices.
        </p>
        <ul className="static-list">
          <li>Users are responsible for account credentials and activity.</li>
          <li>Vendors must submit valid business and product information.</li>
          <li>Admins may review and moderate vendor requests and listings.</li>
          <li>Platform misuse may result in account suspension.</li>
        </ul>
      </div>
    </div>
  );
}

export default Terms;
