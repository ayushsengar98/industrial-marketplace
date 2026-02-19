import React from 'react';
import { Link } from 'react-router-dom';

function NotFound() {
  return (
    <div className="not-found-container">
      <div className="not-found-card">
        <p className="not-found-code">404</p>
        <h1>Page Not Found</h1>
        <p>
          The page you requested does not exist or may have been moved.
        </p>
        <Link to="/" className="btn-primary inline-action">
          Go To Home
        </Link>
      </div>
    </div>
  );
}

export default NotFound;
