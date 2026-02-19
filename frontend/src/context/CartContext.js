import React, { createContext, useEffect, useMemo, useState } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [items, setItems] = useState([]);

  useEffect(() => {
    const stored = localStorage.getItem('cart_items');
    if (stored) {
      try {
        setItems(JSON.parse(stored));
      } catch (err) {
        setItems([]);
      }
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('cart_items', JSON.stringify(items));
  }, [items]);

  const addToCart = (product) => {
    setItems((prev) => {
      const existing = prev.find((item) => item.productId === product.id);
      if (existing) {
        return prev.map((item) =>
          item.productId === product.id
            ? { ...item, quantity: item.quantity + 1, lineTotal: (item.quantity + 1) * item.unitPrice }
            : item
        );
      }

      return [
        ...prev,
        {
          productId: product.id,
          productName: product.name,
          unitPrice: Number(product.price),
          quantity: 1,
          lineTotal: Number(product.price),
          imageUrl: product.imageUrl || null,
          vendorEmail: product.vendorEmail || null
        }
      ];
    });
  };

  const updateQuantity = (productId, quantity) => {
    const qty = Number(quantity);
    if (!Number.isInteger(qty) || qty < 1) return;
    setItems((prev) =>
      prev.map((item) =>
        item.productId === productId
          ? { ...item, quantity: qty, lineTotal: qty * item.unitPrice }
          : item
      )
    );
  };

  const removeFromCart = (productId) => {
    setItems((prev) => prev.filter((item) => item.productId !== productId));
  };

  const clearCart = () => setItems([]);

  const cartCount = useMemo(() => items.reduce((sum, item) => sum + item.quantity, 0), [items]);
  const cartTotal = useMemo(() => items.reduce((sum, item) => sum + item.lineTotal, 0), [items]);

  return (
    <CartContext.Provider
      value={{
        items,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
        cartCount,
        cartTotal
      }}
    >
      {children}
    </CartContext.Provider>
  );
};
