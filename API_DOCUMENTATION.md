# Tricol V2 API Documentation

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Common Response Structure](#common-response-structure)
4. [Error Handling](#error-handling)
5. [Pagination](#pagination)
6. [API Endpoints](#api-endpoints)
   - [Authentication Endpoints](#authentication-endpoints)
   - [Product Endpoints](#product-endpoints)
   - [Supplier Endpoints](#supplier-endpoints)
   - [Purchase Order Endpoints](#purchase-order-endpoints)
   - [Goods Issue Endpoints](#goods-issue-endpoints)
   - [Stock Endpoints](#stock-endpoints)
   - [Stock Movement Endpoints](#stock-movement-endpoints)
   - [User Management Endpoints](#user-management-endpoints)
   - [Role Endpoints](#role-endpoints)
   - [Permission Endpoints](#permission-endpoints)
   - [Audit Log Endpoints](#audit-log-endpoints)
   - [Dashboard Endpoints](#dashboard-endpoints)

---

## Overview

**Base URL:** `http://localhost:8080/api/v1`

**Content-Type:** `application/json`

**CORS:** Configured for Angular frontend at `http://localhost:4200`

---

## Authentication

The API uses JWT (JSON Web Token) for authentication.

### JWT Token Structure

When you login, you receive a JWT token containing the following claims:

```json
{
  "sub": "user@example.com",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "ADMIN",
  "permissions": ["PRODUCT:READ", "PRODUCT:CREATE", "..."],
  "iat": 1736780400,
  "exp": 1736784000
}
```

### Using the Token

Include the JWT token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

---

## Common Response Structure

All API responses follow this structure:

### Success Response
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Operation successful",
  "body": { ... }
}
```

### Error Response
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 400,
  "message": "Error description",
  "body": null
}
```

---

## Error Handling

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid input data |
| 401 | Unauthorized - Authentication required or invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 422 | Unprocessable Entity - Business rule violation |
| 500 | Internal Server Error - Server error |

### Common Error Messages

| Error Type | Status | Message Example |
|------------|--------|-----------------|
| ResourceNotFoundException | 404 | "Product not found with ID: 123" |
| ResourceAlreadyExistsException | 409 | "Product with reference 'REF001' already exists" |
| BusinessViolationException | 422 | "Cannot delete product with existing stock" |
| AuthenticationException | 401 | "Invalid credentials" |
| AccessDeniedException | 403 | "Access denied" |

---

## Pagination

All `GET` endpoints that return lists support pagination with the following parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 10 | Number of items per page |
| `sortBy` | string | "id" | Field to sort by |
| `sortDir` | string | "asc" | Sort direction ("asc" or "desc") |

### Paginated Response Structure

```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Products fetched successfully",
  "body": {
    "content": [ ... ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": { ... }
    },
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false,
    "numberOfElements": 10
  }
}
```

---

## API Endpoints

---

### Authentication Endpoints

Base path: `/api/v1/auth`

---

#### POST `/login`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Login successful",
  "body": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "ADMIN"
  }
}
```

**JWT Token Claims:**
- `sub` - User email (subject)
- `userId` - User ID
- `email` - User email
- `fullName` - User's full name
- `role` - User's role (e.g., "ADMIN", "USER", "MANAGER")
- `permissions` - List of permission codes (e.g., ["PRODUCT:READ", "PRODUCT:CREATE"])
- `iat` - Issued at timestamp
- `exp` - Expiration timestamp

**Error Responses:**
- `401 Unauthorized` - Invalid credentials

---

#### POST `/register`

Register a new user account.

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "Jane Doe"
}
```

**Success Response (201):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 201,
  "message": "Registration successful",
  "body": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "email": "newuser@example.com",
    "fullName": "Jane Doe",
    "role": null
  }
}
```

**Error Responses:**
- `409 Conflict` - Email already exists

---

#### POST `/refresh`

Refresh the access token using the refresh token cookie.

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Token refreshed successfully",
  "body": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "ADMIN"
  }
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or expired refresh token

---

#### POST `/logout`

Logout user and invalidate refresh token.

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Logout successful",
  "body": null
}
```

---

#### GET `/current`

Get current authenticated user information.

**Required Permission:** Authenticated user

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Current user fetched successfully",
  "body": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "ADMIN",
    "isActive": true
  }
}
```

---

### Product Endpoints

Base path: `/api/v1/products`

---

#### GET `/`

Get all products with pagination.

**Required Permission:** `PRODUCT:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "asc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Products fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "reference": "PROD001",
        "name": "Product Name",
        "description": "Product description",
        "category": "Electronics",
        "unit": "PCS",
        "reorderPoint": 10.0
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

---

#### GET `/{id}`

Get product by ID.

**Required Permission:** `PRODUCT:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Product fetched successfully",
  "body": {
    "id": 1,
    "reference": "PROD001",
    "name": "Product Name",
    "description": "Product description",
    "category": "Electronics",
    "unit": "PCS",
    "reorderPoint": 10.0
  }
}
```

**Error Responses:**
- `404 Not Found` - Product not found with ID

---

#### POST `/`

Create a new product.

**Required Permission:** `PRODUCT:CREATE`

**Request Body:**
```json
{
  "reference": "PROD002",
  "name": "New Product",
  "description": "Product description",
  "category": "Electronics",
  "unit": "PCS",
  "reorderPoint": 10.0
}
```

**Success Response (201):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 201,
  "message": "Product created successfully",
  "body": {
    "id": 2,
    "reference": "PROD002",
    "name": "New Product",
    "description": "Product description",
    "category": "Electronics",
    "unit": "PCS",
    "reorderPoint": 10.0
  }
}
```

**Error Responses:**
- `409 Conflict` - Product with reference already exists
- `400 Bad Request` - Validation errors

---

#### PUT `/{id}`

Update an existing product.

**Required Permission:** `PRODUCT:UPDATE`

**Request Body:**
```json
{
  "name": "Updated Product Name",
  "description": "Updated description",
  "category": "Electronics",
  "unit": "PCS",
  "reorderPoint": 15.0
}
```

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Product updated successfully",
  "body": { ... }
}
```

**Error Responses:**
- `404 Not Found` - Product not found

---

#### DELETE `/{id}`

Delete a product.

**Required Permission:** `PRODUCT:DELETE`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Product deleted successfully",
  "body": null
}
```

**Error Responses:**
- `404 Not Found` - Product not found
- `422 Unprocessable Entity` - Cannot delete product with existing order lines or stock

---

#### GET `/{id}/stock`

Get product stock detail.

**Required Permission:** `STOCK:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Product stock detail fetched successfully",
  "body": {
    "productId": 1,
    "reference": "PROD001",
    "name": "Product Name",
    "totalStock": 150.0,
    "reorderPoint": 10.0,
    "fifoValuation": 1500.0,
    "stockLots": [
      {
        "id": 1,
        "lotNumber": "LOT001",
        "entryDate": "2026-01-01",
        "remainingQuantity": 100.0,
        "initialQuantity": 100.0,
        "purchasePrice": 10.0
      }
    ]
  }
}
```

---

### Supplier Endpoints

Base path: `/api/v1/suppliers`

---

#### GET `/`

Get all suppliers with pagination.

**Required Permission:** `SUPPLIER:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "asc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Suppliers fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "name": "Supplier Name",
        "ice": "ICE123456",
        "address": "123 Supplier Street",
        "phone": "+1234567890",
        "email": "supplier@example.com"
      }
    ],
    "totalElements": 20,
    "totalPages": 2
  }
}
```

---

#### GET `/{id}`

Get supplier by ID.

**Required Permission:** `SUPPLIER:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Supplier fetched successfully",
  "body": {
    "id": 1,
    "name": "Supplier Name",
    "ice": "ICE123456",
    "address": "123 Supplier Street",
    "phone": "+1234567890",
    "email": "supplier@example.com"
  }
}
```

**Error Responses:**
- `404 Not Found` - Supplier not found

---

#### POST `/`

Create a new supplier.

**Required Permission:** `SUPPLIER:CREATE`

**Request Body:**
```json
{
  "name": "New Supplier",
  "ice": "ICE789012",
  "address": "456 New Street",
  "phone": "+0987654321",
  "email": "newsupplier@example.com"
}
```

**Success Response (201):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 201,
  "message": "Supplier created successfully",
  "body": { ... }
}
```

**Error Responses:**
- `409 Conflict` - Supplier with ICE already exists

---

#### PUT `/{id}`

Update a supplier.

**Required Permission:** `SUPPLIER:UPDATE`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Supplier updated successfully",
  "body": { ... }
}
```

---

#### DELETE `/{id}`

Delete a supplier.

**Required Permission:** `SUPPLIER:DELETE`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Supplier deleted successfully",
  "body": null
}
```

**Error Responses:**
- `422 Unprocessable Entity` - Cannot delete supplier with existing purchase orders

---

### Purchase Order Endpoints

Base path: `/api/v1/orders`

---

#### GET `/`

Get all purchase orders with pagination.

**Required Permission:** `PUCHASE_ORDER:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "desc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Purchase orders fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "orderNumber": "PO-20260113-001",
        "orderDate": "2026-01-13",
        "status": "PENDING",
        "supplierId": 1,
        "supplierName": "Supplier Name",
        "totalAmount": 5000.00,
        "orderLines": [
          {
            "id": 1,
            "productId": 1,
            "productName": "Product Name",
            "quantity": 100,
            "unitPrice": 50.00
          }
        ]
      }
    ],
    "totalElements": 30,
    "totalPages": 3
  }
}
```

---

#### GET `/status/{status}`

Get purchase orders by status.

**Required Permission:** `PUCHASE_ORDER:READ`

**Path Parameters:**
- `status`: `PENDING`, `VALIDATED`, `RECEIVED`, `CANCELLED`

---

#### GET `/{id}`

Get purchase order by ID.

**Required Permission:** `PUCHASE_ORDER:READ`

---

#### GET `/supplier/{id}`

Get purchase orders by supplier.

**Required Permission:** `PUCHASE_ORDER:READ`

---

#### POST `/`

Create a new purchase order.

**Required Permission:** `PUCHASE_ORDER:CREATE`

**Request Body:**
```json
{
  "supplierId": 1,
  "orderLines": [
    {
      "productId": 1,
      "quantity": 100,
      "unitPrice": 50.00
    }
  ]
}
```

**Success Response (201):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 201,
  "message": "Purchase order created successfully",
  "body": { ... }
}
```

---

#### PUT `/{id}`

Update a purchase order.

**Required Permission:** `PUCHASE_ORDER:UPDATE`

---

#### DELETE `/{id}`

Delete a purchase order.

**Required Permission:** `PUCHASE_ORDER:DELETE`

---

#### PUT `/{id}/validate`

Validate a purchase order.

**Required Permission:** `PUCHASE_ORDER:VALIDATE`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Purchase order validated successfully",
  "body": null
}
```

---

#### PUT `/{id}/reception`

Receive a purchase order (creates stock lots).

**Required Permission:** `PUCHASE_ORDER:RECEIVE`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Purchase order received and stock lots created successfully",
  "body": null
}
```

---

#### PUT `/{id}/cancel`

Cancel a purchase order.

**Required Permission:** `PUCHASE_ORDER:CANCEL`

---

### Goods Issue Endpoints

Base path: `/api/v1/goods-issues`

---

#### GET `/`

Get all goods issues with pagination.

**Required Permission:** `GOODS_ISSUE:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "desc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Goods issues fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "issueNumber": "GI-20260113-001",
        "issueDate": "2026-01-13",
        "status": "DRAFT",
        "totalCost": 1000.00,
        "issueLines": [
          {
            "id": 1,
            "productId": 1,
            "productName": "Product Name",
            "quantity": 10,
            "unitCost": 50.00,
            "totalCost": 500.00
          }
        ]
      }
    ],
    "totalElements": 15,
    "totalPages": 2
  }
}
```

---

#### GET `/status/{status}`

Get goods issues by status.

**Required Permission:** `GOODS_ISSUE:READ`

**Path Parameters:**
- `status`: `DRAFT`, `VALIDATED`, `CANCELLED`

---

#### GET `/{id}`

Get goods issue by ID.

**Required Permission:** `GOODS_ISSUE:READ`

---

#### POST `/`

Create a new goods issue.

**Required Permission:** `GOODS_ISSUE:CREATE`

**Request Body:**
```json
{
  "issueLines": [
    {
      "productId": 1,
      "quantity": 10
    }
  ]
}
```

---

#### PUT `/{id}`

Update a goods issue.

**Required Permission:** `GOODS_ISSUE:UPDATE`

---

#### DELETE `/{id}`

Delete a goods issue.

**Required Permission:** `GOODS_ISSUE:DELETE`

---

#### PUT `/{id}/validate`

Validate a goods issue (deducts from stock using FIFO).

**Required Permission:** `GOODS_ISSUE:VALIDATE`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Goods issue validated successfully",
  "body": null
}
```

**Error Responses:**
- `422 Unprocessable Entity` - Insufficient stock for product

---

#### PUT `/{id}/cancel`

Cancel a goods issue.

**Required Permission:** `GOODS_ISSUE:CANCEL`

---

### Stock Endpoints

Base path: `/api/v1/stock`

---

#### GET `/`

Get global stock summary with pagination.

**Required Permission:** `STOCK:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "asc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Global stock summary fetched successfully",
  "body": {
    "content": [
      {
        "productId": 1,
        "reference": "PROD001",
        "name": "Product Name",
        "totalStock": 150.0,
        "reorderPoint": 10.0,
        "belowThreshold": false
      }
    ],
    "totalElements": 50,
    "totalPages": 5
  }
}
```

---

#### GET `/product/{id}`

Get detailed stock for a specific product.

**Required Permission:** `STOCK:READ`

---

#### GET `/valuation`

Get total stock valuation.

**Required Permission:** `STOCK:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Stock valuation fetched successfully",
  "body": {
    "totalValue": 125000.00,
    "totalLots": 45,
    "totalProducts": 25
  }
}
```

---

### Stock Movement Endpoints

Base path: `/api/v1/stock`

---

#### GET `/mouvements`

Search stock movements with filters and pagination.

**Required Permission:** `STOCK_MOVEMENT:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `dateDebut` - Start date (ISO format: YYYY-MM-DD)
- `dateFin` - End date (ISO format: YYYY-MM-DD)
- `produitId` - Product ID
- `reference` - Movement reference
- `type` - Movement type (`ENTRY`, `EXIT`)
- `numeroLot` - Lot number

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Stock movements searched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "reference": "MOV-001",
        "movementDate": "2026-01-13",
        "type": "ENTRY",
        "quantity": 100.0,
        "productId": 1,
        "productName": "Product Name",
        "lotNumber": "LOT001"
      }
    ],
    "totalElements": 200,
    "totalPages": 20
  }
}
```

---

#### GET `/mouvements/all`

Get all stock movements.

**Required Permission:** `STOCK_MOVEMENT:READ`

---

#### GET `/mouvements/{id}`

Get stock movement by ID.

**Required Permission:** `STOCK_MOVEMENT:READ`

---

#### GET `/mouvements/product/{productId}`

Get stock movements for a specific product.

**Required Permission:** `STOCK_MOVEMENT:READ`

---

### User Management Endpoints

Base path: `/api/v1/users`

---

#### GET `/`

Get all users with pagination.

**Required Permission:** `USER:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "asc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Users fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "email": "user@example.com",
        "fullName": "John Doe",
        "role": "ADMIN",
        "isActive": true
      }
    ],
    "totalElements": 10,
    "totalPages": 1
  }
}
```

---

#### GET `/{id}/all-permissions`

Get all permissions for a user (role + custom).

**Required Permission:** `USER:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "User permissions fetched successfully",
  "body": {
    "userId": 1,
    "roleName": "ADMIN",
    "rolePermissions": ["PRODUCT:READ", "PRODUCT:CREATE", "..."],
    "customPermissions": [
      {
        "permissionId": 5,
        "permissionCode": "AUDIT_LOGS:READ",
        "isGranted": true
      }
    ]
  }
}
```

---

#### PUT `/{id}/assign-role`

Assign a role to a user.

**Required Permission:** `USER:UPDATE`

**Request Body:**
```json
{
  "roleName": "MANAGER"
}
```

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Role assigned successfully",
  "body": null
}
```

---

### Custom Permission Endpoints

Base path: `/api/v1/users/{userId}/permissions`

---

#### GET `/`

Get custom permissions for a user.

**Required Permission:** `USER:READ`

---

#### POST `/`

Assign a custom permission to a user.

**Required Permission:** `USER:UPDATE`

**Request Body:**
```json
{
  "permissionId": 5,
  "isGranted": true
}
```

---

#### DELETE `/{permissionId}`

Revoke a custom permission from a user.

**Required Permission:** `USER:UPDATE`

---

### Role Endpoints

Base path: `/api/v1/roles`

---

#### GET `/`

Get all roles with their permissions (paginated).

**Required Permission:** `USER:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "asc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Roles with permissions fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "name": "ADMIN",
        "description": "Administrator with full access",
        "permissions": [
          {
            "id": 1,
            "ressource": "PRODUCT",
            "action": "READ",
            "description": "Read products",
            "authority": "PRODUCT:READ"
          }
        ]
      }
    ],
    "totalElements": 3,
    "totalPages": 1
  }
}
```

---

### Permission Endpoints

Base path: `/api/v1/permissions`

---

#### GET `/`

Get all available permissions (paginated).

**Required Permission:** `USER:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "asc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Permissions fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "ressource": "PRODUCT",
        "action": "READ",
        "description": "Read products",
        "authority": "PRODUCT:READ"
      }
    ],
    "totalElements": 25,
    "totalPages": 3
  }
}
```

---

### Audit Log Endpoints

Base path: `/api/v1/logs`

---

#### GET `/`

Get all audit logs (paginated).

**Required Permission:** `AUDIT_LOGS:READ`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "id")
- `sortDir` (default: "desc")

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Logs fetched successfully",
  "body": {
    "content": [
      {
        "id": 1,
        "action": "PRODUCT_CREATED",
        "userId": 1,
        "userEmail": "admin@example.com",
        "timestamp": "2026-01-13T10:30:00",
        "details": {
          "Product id": "5"
        }
      }
    ],
    "totalElements": 500,
    "totalPages": 50
  }
}
```

---

### Dashboard Endpoints

Base path: `/api/v1/dashboard`

---

#### GET `/`

Get dashboard statistics.

**Required Permission:** `STOCK:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Dashboard data fetched successfully",
  "body": {
    "totalProducts": 50,
    "totalSuppliers": 20,
    "pendingOrders": 5,
    "lowStockProducts": 3,
    "totalStockValue": 125000.00
  }
}
```

---

#### GET `/alerts`

Get stock alerts (products below reorder point).

**Required Permission:** `STOCK:READ`

**Success Response (200):**
```json
{
  "timestamp": "13-01-2026 10:30:00",
  "status": 200,
  "message": "Stock alerts fetched successfully",
  "body": [
    {
      "productId": 1,
      "productName": "Product Name",
      "reference": "PROD001",
      "currentStock": 5.0,
      "reorderPoint": 10.0,
      "deficit": 5.0
    }
  ]
}
```

---

## Permission Codes Reference

| Resource | Action | Permission Code |
|----------|--------|-----------------|
| PRODUCT | READ | PRODUCT:READ |
| PRODUCT | CREATE | PRODUCT:CREATE |
| PRODUCT | UPDATE | PRODUCT:UPDATE |
| PRODUCT | DELETE | PRODUCT:DELETE |
| SUPPLIER | READ | SUPPLIER:READ |
| SUPPLIER | CREATE | SUPPLIER:CREATE |
| SUPPLIER | UPDATE | SUPPLIER:UPDATE |
| SUPPLIER | DELETE | SUPPLIER:DELETE |
| PUCHASE_ORDER | READ | PUCHASE_ORDER:READ |
| PUCHASE_ORDER | CREATE | PUCHASE_ORDER:CREATE |
| PUCHASE_ORDER | UPDATE | PUCHASE_ORDER:UPDATE |
| PUCHASE_ORDER | DELETE | PUCHASE_ORDER:DELETE |
| PUCHASE_ORDER | VALIDATE | PUCHASE_ORDER:VALIDATE |
| PUCHASE_ORDER | RECEIVE | PUCHASE_ORDER:RECEIVE |
| PUCHASE_ORDER | CANCEL | PUCHASE_ORDER:CANCEL |
| GOODS_ISSUE | READ | GOODS_ISSUE:READ |
| GOODS_ISSUE | CREATE | GOODS_ISSUE:CREATE |
| GOODS_ISSUE | UPDATE | GOODS_ISSUE:UPDATE |
| GOODS_ISSUE | DELETE | GOODS_ISSUE:DELETE |
| GOODS_ISSUE | VALIDATE | GOODS_ISSUE:VALIDATE |
| GOODS_ISSUE | CANCEL | GOODS_ISSUE:CANCEL |
| STOCK | READ | STOCK:READ |
| STOCK_MOVEMENT | READ | STOCK_MOVEMENT:READ |
| USER | READ | USER:READ |
| USER | UPDATE | USER:UPDATE |
| AUDIT_LOGS | READ | AUDIT_LOGS:READ |

---

## Role Types

| Role | Description |
|------|-------------|
| ADMIN | Full system access |
| MANAGER | Management access without system administration |
| USER | Basic read and limited write access |
| INVENTORY_MANAGER | Stock and inventory management |
| PURCHASING_AGENT | Purchase order management |

---

## Swagger/OpenAPI Documentation

Interactive API documentation is available at:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## Notes for Frontend Developers

### Decoding JWT Token

The JWT token can be decoded on the frontend to extract user information:

```javascript
// JavaScript example
function parseJwt(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
  }).join(''));
  return JSON.parse(jsonPayload);
}

// Usage
const token = "eyJhbGciOiJIUzI1NiIs...";
const decoded = parseJwt(token);
console.log(decoded.role);        // "ADMIN"
console.log(decoded.permissions); // ["PRODUCT:READ", "PRODUCT:CREATE", ...]
```

### Angular AuthGuard Example

```typescript
// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredPermission = route.data['permission'];
    
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return false;
    }
    
    if (requiredPermission && !this.authService.hasPermission(requiredPermission)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    
    return true;
  }
}

// Route configuration
const routes: Routes = [
  {
    path: 'products',
    component: ProductListComponent,
    canActivate: [AuthGuard],
    data: { permission: 'PRODUCT:READ' }
  }
];
```

---

*Documentation generated on January 13, 2026*
