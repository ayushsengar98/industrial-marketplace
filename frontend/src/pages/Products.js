import React, { useContext, useEffect, useState } from 'react';
import API from '../services/api';
import dummyProducts from '../data/dummyProducts';
import { CartContext } from '../context/CartContext';

function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [failedImageIds, setFailedImageIds] = useState({});
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const { addToCart } = useContext(CartContext);

  const useDummyProducts = process.env.REACT_APP_USE_DUMMY_PRODUCTS === 'true';

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await API.get('/api/products');
      const apiProducts = Array.isArray(response.data) ? response.data : [];
      if (apiProducts.length === 0 && useDummyProducts) {
        setProducts(dummyProducts);
        setInfo('Showing dummy products (API returned no products).');
      } else {
        setProducts(apiProducts);
      }
    } catch (err) {
      if (useDummyProducts) {
        setProducts(dummyProducts);
        setInfo('Showing dummy products (API unavailable).');
      } else {
        setError('Failed to load products');
      }
    } finally {
      setLoading(false);
    }
  };

  const filteredProducts = products.filter((product) => {
    const name = product?.name || '';
    const description = product?.description || '';
    const category = product?.category || '';
    const matchesSearch =
      name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory ? category === selectedCategory : true;
    return matchesSearch && matchesCategory;
  });

  const categories = [...new Set(products.map((p) => p?.category).filter(Boolean))];

  if (loading) return <div className="loading">Loading products...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="products-container">
      <h1>Products</h1>
      {info && <div className="message info">{info}</div>}

      <div className="filters">
        <input
          type="text"
          placeholder="Search products..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <select
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          className="category-select"
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>
      </div>

      <div className="products-grid">
        {filteredProducts.length > 0 ? (
          filteredProducts.map((product) => (
            <div key={product.id} className="product-card">
              {product.imageUrl && !failedImageIds[product.id] ? (
                <img
                  className="product-thumbnail"
                  src={product.imageUrl}
                  alt={product.name}
                  onError={() =>
                    setFailedImageIds((prev) => ({
                      ...prev,
                      [product.id]: true
                    }))
                  }
                />
              ) : (
                <div className="product-image-fallback" aria-label="No product image">
                  No Image
                </div>
              )}
              <h3>{product.name}</h3>
              <p className="product-description">{product.description}</p>
              <p className="product-price">Rs {product.price}</p>
              <p className="product-stock">Stock: {product.stock}</p>
              <p className="product-category">{product.category}</p>
              <p className="product-vendor">Vendor: {product.vendorEmail || 'N/A'}</p>
              <button className="btn-primary" onClick={() => addToCart(product)}>
                Add To Cart
              </button>
            </div>
          ))
        ) : (
          <p className="no-products">No products found</p>
        )}
      </div>
    </div>
  );
}

export default Products;
