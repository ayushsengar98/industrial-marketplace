import React, { useContext, useEffect, useState } from 'react';
import API from '../services/api';
import { AuthContext } from '../context/AuthContext';

const EMPTY_PRODUCT_FORM = {
  name: '',
  description: '',
  price: '',
  stock: '',
  category: '',
  imageUrl: ''
};

function VendorDashboard() {
  const { user } = useContext(AuthContext);
  const [products, setProducts] = useState([]);
  const [vendorStatus, setVendorStatus] = useState(null);
  const [showProductForm, setShowProductForm] = useState(false);
  const [editingProductId, setEditingProductId] = useState(null);
  const [submittingProduct, setSubmittingProduct] = useState(false);
  const [applying, setApplying] = useState(false);
  const [applyForm, setApplyForm] = useState({ companyName: '', gstNumber: '' });
  const [productForm, setProductForm] = useState(EMPTY_PRODUCT_FORM);
  const [productErrors, setProductErrors] = useState({});
  const [failedImageIds, setFailedImageIds] = useState({});
  const [previewImageFailed, setPreviewImageFailed] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    checkVendorStatus();
  }, []);

  useEffect(() => {
    if (vendorStatus?.status === 'APPROVED') {
      fetchMyProducts();
    }
  }, [vendorStatus]);

  const checkVendorStatus = async () => {
    try {
      const response = await API.get('/api/vendor/status');
      setVendorStatus(response.data);
    } catch (err) {
      const notFoundMessage =
        err?.response?.data?.message === 'Vendor application not found' ||
        err?.response?.status === 404 ||
        err?.response?.status === 400;

      if (user?.role === 'VENDOR' && notFoundMessage) {
        const fallbackCompanyName = user?.email ? user.email.split('@')[0] : 'Vendor';
        setVendorStatus({
          status: 'APPROVED',
          companyName: fallbackCompanyName
        });
        setMessage({
          type: 'info',
          text: 'Vendor account active. Profile record not found, using role-based access.'
        });
        return;
      }

      setVendorStatus(null);
    }
  };

  const fetchMyProducts = async () => {
    try {
      const response = await API.get('/api/products/mine');
      setProducts(response.data);
    } catch (err) {
      console.error('Failed to fetch products', err);
    }
  };

  const applyForVendor = async (event) => {
    event.preventDefault();
    if (user?.role === 'VENDOR') {
      setMessage({ type: 'info', text: 'Your account already has vendor access.' });
      return;
    }

    setApplying(true);
    try {
      const response = await API.post('/api/vendor/apply', applyForm);
      setVendorStatus(response.data);
      setApplyForm({ companyName: '', gstNumber: '' });
      setMessage({ type: 'success', text: 'Application submitted successfully!' });
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (err) {
      const apiMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        (err?.response?.status ? `Request failed (${err.response.status})` : '') ||
        'Failed to apply';
      setMessage({ type: 'error', text: apiMessage });
    } finally {
      setApplying(false);
    }
  };

  const validateProduct = (product) => {
    const errors = {};
    const name = (product.name || '').trim();
    const description = (product.description || '').trim();
    const category = (product.category || '').trim();
    const imageUrl = (product.imageUrl || '').trim();
    const price = Number(product.price);
    const stock = Number(product.stock);

    if (name.length < 3 || name.length > 100) {
      errors.name = 'Name must be 3 to 100 characters.';
    }

    if (description.length < 10 || description.length > 1000) {
      errors.description = 'Description must be 10 to 1000 characters.';
    }

    if (Number.isNaN(price) || price < 0) {
      errors.price = 'Price must be a non-negative number.';
    }

    if (!Number.isInteger(stock) || stock < 0) {
      errors.stock = 'Stock must be a whole number (0 or more).';
    }

    if (category.length < 2 || category.length > 50) {
      errors.category = 'Category must be 2 to 50 characters.';
    }

    if (imageUrl && !/^https?:\/\/\S+$/i.test(imageUrl)) {
      errors.imageUrl = 'Image URL must start with http:// or https://';
    }

    return errors;
  };

  const handleProductFieldChange = (field, value) => {
    const next = { ...productForm, [field]: value };
    setProductForm(next);
    setProductErrors(validateProduct(next));
    if (field === 'imageUrl') {
      setPreviewImageFailed(false);
    }
  };

  const openCreateProductForm = () => {
    setEditingProductId(null);
    setProductForm(EMPTY_PRODUCT_FORM);
    setProductErrors({});
    setPreviewImageFailed(false);
    setShowProductForm(true);
  };

  const openEditProductForm = (product) => {
    setEditingProductId(product.id);
    setProductForm({
      name: product.name || '',
      description: product.description || '',
      price: product.price ?? '',
      stock: product.stock ?? '',
      category: product.category || '',
      imageUrl: product.imageUrl || ''
    });
    setProductErrors({});
    setPreviewImageFailed(false);
    setShowProductForm(true);
  };

  const closeProductForm = () => {
    setShowProductForm(false);
    setEditingProductId(null);
    setProductForm(EMPTY_PRODUCT_FORM);
    setProductErrors({});
    setPreviewImageFailed(false);
  };

  const submitProduct = async (event) => {
    event.preventDefault();
    const validationErrors = validateProduct(productForm);
    setProductErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      setMessage({ type: 'error', text: 'Please fix highlighted product fields.' });
      return;
    }

    setSubmittingProduct(true);
    const payload = {
      name: productForm.name.trim(),
      description: productForm.description.trim(),
      category: productForm.category.trim(),
      imageUrl: productForm.imageUrl.trim() || null,
      price: Number(productForm.price),
      stock: Number(productForm.stock)
    };

    try {
      if (editingProductId) {
        await API.put(`/api/products/${editingProductId}`, payload);
        setMessage({ type: 'success', text: 'Product updated successfully!' });
      } else {
        await API.post('/api/products', payload);
        setMessage({ type: 'success', text: 'Product added successfully!' });
      }
      closeProductForm();
      fetchMyProducts();
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    } catch (err) {
      const apiMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        (err?.response?.status ? `Request failed (${err.response.status})` : '') ||
        'Failed to save product';
      setMessage({ type: 'error', text: apiMessage });
    } finally {
      setSubmittingProduct(false);
    }
  };

  const deleteProduct = async (id) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      try {
        await API.delete(`/api/products/${id}`);
        fetchMyProducts();
        setMessage({ type: 'success', text: 'Product deleted successfully!' });
        setTimeout(() => setMessage({ type: '', text: '' }), 3000);
      } catch (err) {
        setMessage({ type: 'error', text: err?.response?.data?.message || 'Failed to delete product' });
      }
    }
  };

  if (!vendorStatus) {
    return (
      <div className="dashboard-container">
        <h1>Vendor Dashboard</h1>
        {message.text && <div className={`message ${message.type}`}>{message.text}</div>}
        <div className="vendor-apply-card">
          <h2>Become a Vendor</h2>
          <p>Apply to start selling your products on our marketplace</p>
          <form className="vendor-request-form compact" onSubmit={applyForVendor}>
            <div className="form-group">
              <label htmlFor="applyCompanyName">Company Name</label>
              <input
                id="applyCompanyName"
                type="text"
                value={applyForm.companyName}
                onChange={(e) => setApplyForm((prev) => ({ ...prev, companyName: e.target.value }))}
                required
                disabled={applying}
              />
            </div>
            <div className="form-group">
              <label htmlFor="applyGstNumber">GST Number</label>
              <input
                id="applyGstNumber"
                type="text"
                value={applyForm.gstNumber}
                onChange={(e) => setApplyForm((prev) => ({ ...prev, gstNumber: e.target.value }))}
                required
                disabled={applying}
              />
            </div>
            <button type="submit" className="btn-primary" disabled={applying}>
              {applying ? 'Submitting...' : 'Apply Now'}
            </button>
          </form>
        </div>
      </div>
    );
  }

  if (vendorStatus.status === 'PENDING') {
    return (
      <div className="dashboard-container">
        <h1>Vendor Dashboard</h1>
        <div className="vendor-status-card pending">
          <h2>Application Pending</h2>
          <p>Your vendor application is under review. You will be notified once approved.</p>
        </div>
      </div>
    );
  }

  if (vendorStatus.status === 'APPROVED') {
    return (
      <div className="dashboard-container">
        <h1>Vendor Dashboard</h1>
        {message.text && <div className={`message ${message.type}`}>{message.text}</div>}

        <div className="vendor-header">
          <div>
            <h2>Welcome, {vendorStatus.companyName}</h2>
            <p className="dashboard-muted">Manage your product catalog and inventory in one place.</p>
          </div>
          <button onClick={openCreateProductForm} className="btn-primary">
            + Add New Product
          </button>
        </div>

        {showProductForm && (
          <div className="add-product-form">
            <h3>{editingProductId ? 'Edit Product' : 'Add New Product'}</h3>
            <form onSubmit={submitProduct}>
              <input
                type="text"
                placeholder="Product Name"
                value={productForm.name}
                onChange={(e) => handleProductFieldChange('name', e.target.value)}
                required
              />
              {productErrors.name && <p className="field-error">{productErrors.name}</p>}

              <textarea
                placeholder="Description"
                value={productForm.description}
                onChange={(e) => handleProductFieldChange('description', e.target.value)}
                required
              />
              {productErrors.description && <p className="field-error">{productErrors.description}</p>}

              <input
                type="number"
                placeholder="Price"
                value={productForm.price}
                onChange={(e) => handleProductFieldChange('price', e.target.value)}
                required
                min="0"
                step="0.01"
              />
              {productErrors.price && <p className="field-error">{productErrors.price}</p>}

              <input
                type="number"
                placeholder="Stock"
                value={productForm.stock}
                onChange={(e) => handleProductFieldChange('stock', e.target.value)}
                required
                min="0"
                step="1"
              />
              {productErrors.stock && <p className="field-error">{productErrors.stock}</p>}

              <input
                type="text"
                placeholder="Category"
                value={productForm.category}
                onChange={(e) => handleProductFieldChange('category', e.target.value)}
                required
              />
              {productErrors.category && <p className="field-error">{productErrors.category}</p>}

              <input
                type="url"
                placeholder="Image URL (optional)"
                value={productForm.imageUrl}
                onChange={(e) => handleProductFieldChange('imageUrl', e.target.value)}
              />
              {productErrors.imageUrl && <p className="field-error">{productErrors.imageUrl}</p>}

              {productForm.imageUrl && !productErrors.imageUrl && (
                previewImageFailed ? (
                  <div className="product-image-fallback preview" aria-label="Invalid preview image">
                    Invalid Image URL
                  </div>
                ) : (
                  <img
                    className="product-preview"
                    src={productForm.imageUrl}
                    alt="Product preview"
                    onError={() => setPreviewImageFailed(true)}
                  />
                )
              )}

              <div className="form-buttons">
                <button type="submit" className="btn-primary" disabled={submittingProduct}>
                  {submittingProduct ? 'Saving...' : editingProductId ? 'Update Product' : 'Save Product'}
                </button>
                <button type="button" onClick={closeProductForm} className="btn-secondary">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        <div className="my-products">
          <h3>My Products ({products.length})</h3>
          <div className="products-grid">
            {products.length > 0 ? (
              products.map((product) => (
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
                  <h4>{product.name}</h4>
                  <p className="product-description">{product.description}</p>
                  <p className="product-price">Rs {product.price}</p>
                  <p className="product-stock">Stock: {product.stock}</p>
                  <p className="product-category">{product.category}</p>
                  <div className="product-actions">
                    <button onClick={() => openEditProductForm(product)} className="btn-outline">
                      Edit
                    </button>
                    <button onClick={() => deleteProduct(product.id)} className="btn-danger">
                      Delete
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p className="no-products">No products yet. Click "Add New Product" to start selling.</p>
            )}
          </div>
        </div>
      </div>
    );
  }

  return null;
}

export default VendorDashboard;
