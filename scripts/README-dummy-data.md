# Dummy Product Data

This project now supports two optional dummy-data paths without changing normal flow:

## 1) Frontend dummy product view (read-only fallback)

Set env var and run frontend:

```powershell
$env:REACT_APP_USE_DUMMY_PRODUCTS="true"
npm start
```

Behavior:
- If `/api/products` is empty or unavailable, UI shows dummy products.
- If API works, real products are used.

## 2) Backend product seeding via API (real create calls)

Run script:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\seed-dummy-products.ps1 -Email "your-vendor@email.com" -Password "yourPassword"
```

Notes:
- Requires a `VENDOR` account.
- Uses `POST /api/products` through gateway.
- Does not run automatically; only runs when you execute it.
