import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';

function Cart() {
  const { items, updateQuantity, removeFromCart, clearCart, cartTotal } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const [placing, setPlacing] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const navigate = useNavigate();

  const placeOrder = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    if (items.length === 0) {
      setMessage({ type: 'error', text: 'Cart is empty.' });
      return;
    }

    setPlacing(true);
    setMessage({ type: '', text: '' });

    try {
      await API.post('/api/orders', { items });
      clearCart();
      setMessage({ type: 'success', text: 'Order placed successfully.' });
      setTimeout(() => navigate('/orders'), 900);
    } catch (err) {
      const apiMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        (err?.response?.status ? `Request failed (${err.response.status})` : '') ||
        'Failed to place order';
      setMessage({ type: 'error', text: apiMessage });
    } finally {
      setPlacing(false);
    }
  };

  return (
    <div className="orders-page-container">
      <h1>Cart</h1>
      {message.text && <div className={`message ${message.type}`}>{message.text}</div>}

      {items.length === 0 ? (
        <p className="empty-state">Your cart is empty.</p>
      ) : (
        <>
          <div className="orders-list">
            {items.map((item) => (
              <div className="order-card" key={item.productId}>
                <div className="order-card-header">
                  <h3>{item.productName}</h3>
                  <strong>Rs {item.lineTotal.toFixed(2)}</strong>
                </div>
                <p className="dashboard-muted">Unit Price: Rs {item.unitPrice}</p>
                <div className="cart-row">
                  <label htmlFor={`qty-${item.productId}`}>Qty</label>
                  <input
                    id={`qty-${item.productId}`}
                    type="number"
                    min="1"
                    value={item.quantity}
                    onChange={(e) => updateQuantity(item.productId, e.target.value)}
                  />
                  <button className="btn-danger" onClick={() => removeFromCart(item.productId)}>
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="checkout-bar">
            <strong>Total: Rs {cartTotal.toFixed(2)}</strong>
            <div className="product-actions">
              <button className="btn-outline" onClick={clearCart}>Clear Cart</button>
              <button className="btn-primary" onClick={placeOrder} disabled={placing}>
                {placing ? 'Placing...' : 'Checkout'}
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

export default Cart;
