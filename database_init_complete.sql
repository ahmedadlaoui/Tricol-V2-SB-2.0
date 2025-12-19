-- ============================================
-- TRICOL V2 - COMPLETE DATABASE INITIALIZATION
-- ============================================
-- This script initializes roles, permissions, and role-permission mappings
-- Run this after creating the database schema (via Liquibase or manual DDL)
-- Admin credentials: admin@gmail.com / admin
-- ============================================
-- ============================================
-- 1. ROLES
-- ============================================
INSERT INTO role_app (name, description)
VALUES (
        'ADMIN',
        'Administrator with full access to all system features'
    ),
    (
        'PURCHASING_MANAGER',
        'Responsible for managing purchases and suppliers'
    ),
    (
        'STOREKEEPER',
        'Manages stock, inventory, and warehouse operations'
    ),
    (
        'WORKSHOP_MANAGER',
        'Manages workshop operations and goods issues'
    ) ON DUPLICATE KEY
UPDATE description =
VALUES(description);
-- ============================================
-- 2. PERMISSIONS
-- ============================================
-- VALIDATE, CANCEL, RECEIVE only apply to business documents:
--   - PUCHASE_ORDER: RECEIVE, VALIDATE, CANCEL
--   - GOODS_ISSUE: VALIDATE, CANCEL (no RECEIVE)
-- AUDIT_LOGS: Only READ permission
-- All other resources: CREATE, READ, UPDATE, DELETE only
-- ============================================
INSERT INTO permission (ressource, action, description)
VALUES -- STOCK (CRUD only)
    ('STOCK', 'CREATE', 'Create stock entries'),
    ('STOCK', 'READ', 'View stock information'),
    ('STOCK', 'UPDATE', 'Update stock entries'),
    ('STOCK', 'DELETE', 'Delete stock entries'),
    -- SUPPLIER (CRUD only)
    ('SUPPLIER', 'CREATE', 'Create new suppliers'),
    ('SUPPLIER', 'READ', 'View supplier information'),
    ('SUPPLIER', 'UPDATE', 'Update supplier details'),
    ('SUPPLIER', 'DELETE', 'Delete suppliers'),
    -- PRODUCT (CRUD only)
    ('PRODUCT', 'CREATE', 'Create new products'),
    ('PRODUCT', 'READ', 'View product information'),
    ('PRODUCT', 'UPDATE', 'Update product details'),
    ('PRODUCT', 'DELETE', 'Delete products'),
    -- STOCK_MOVEMENT (CRUD only)
    (
        'STOCK_MOVEMENT',
        'CREATE',
        'Create stock movements'
    ),
    (
        'STOCK_MOVEMENT',
        'READ',
        'View stock movement history'
    ),
    (
        'STOCK_MOVEMENT',
        'UPDATE',
        'Update stock movements'
    ),
    (
        'STOCK_MOVEMENT',
        'DELETE',
        'Delete stock movements'
    ),
    -- GOODS_ISSUE (CRUD + VALIDATE + CANCEL)
    (
        'GOODS_ISSUE',
        'CREATE',
        'Create goods issue documents'
    ),
    (
        'GOODS_ISSUE',
        'READ',
        'View goods issue documents'
    ),
    (
        'GOODS_ISSUE',
        'UPDATE',
        'Update goods issue documents'
    ),
    (
        'GOODS_ISSUE',
        'DELETE',
        'Delete goods issue documents'
    ),
    (
        'GOODS_ISSUE',
        'VALIDATE',
        'Validate goods issue documents (consumes stock FIFO)'
    ),
    (
        'GOODS_ISSUE',
        'CANCEL',
        'Cancel goods issue documents'
    ),
    -- PUCHASE_ORDER (CRUD + RECEIVE + VALIDATE + CANCEL)
    (
        'PUCHASE_ORDER',
        'CREATE',
        'Create purchase orders'
    ),
    ('PUCHASE_ORDER', 'READ', 'View purchase orders'),
    (
        'PUCHASE_ORDER',
        'UPDATE',
        'Update purchase orders'
    ),
    (
        'PUCHASE_ORDER',
        'DELETE',
        'Delete purchase orders'
    ),
    (
        'PUCHASE_ORDER',
        'RECEIVE',
        'Receive purchase orders (creates stock lots)'
    ),
    (
        'PUCHASE_ORDER',
        'VALIDATE',
        'Validate purchase orders'
    ),
    (
        'PUCHASE_ORDER',
        'CANCEL',
        'Cancel purchase orders'
    ),
    -- USER (CRUD only)
    ('USER', 'CREATE', 'Create new users'),
    ('USER', 'READ', 'View user information'),
    (
        'USER',
        'UPDATE',
        'Update user details and assign roles'
    ),
    ('USER', 'DELETE', 'Delete users'),
    -- AUDIT_LOGS (READ only)
    (
        'AUDIT_LOGS',
        'READ',
        'View audit logs and system activity history'
    ) ON DUPLICATE KEY
UPDATE description =
VALUES(description);
-- ============================================
-- 3. ROLE-PERMISSION MAPPINGS
-- ============================================
-- ADMIN: Gets ALL permissions (including AUDIT_LOGS:READ)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,
    p.id
FROM role_app r,
    permission p
WHERE r.name = 'ADMIN' ON DUPLICATE KEY
UPDATE role_id = role_id;
-- PURCHASING_MANAGER: Suppliers, Products (READ), Purchase Orders, Stock (READ)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,
    p.id
FROM role_app r,
    permission p
WHERE r.name = 'PURCHASING_MANAGER'
    AND (
        (p.ressource = 'SUPPLIER')
        OR (
            p.ressource = 'PRODUCT'
            AND p.action = 'READ'
        )
        OR (p.ressource = 'PUCHASE_ORDER')
        OR (
            p.ressource = 'STOCK'
            AND p.action = 'READ'
        )
    ) ON DUPLICATE KEY
UPDATE role_id = role_id;
-- STOREKEEPER: Stock, Stock Movements, Products (READ), Purchase Orders (READ, RECEIVE)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,
    p.id
FROM role_app r,
    permission p
WHERE r.name = 'STOREKEEPER'
    AND (
        (p.ressource = 'STOCK')
        OR (p.ressource = 'STOCK_MOVEMENT')
        OR (
            p.ressource = 'PRODUCT'
            AND p.action = 'READ'
        )
        OR (
            p.ressource = 'PUCHASE_ORDER'
            AND p.action IN ('READ', 'RECEIVE')
        )
    ) ON DUPLICATE KEY
UPDATE role_id = role_id;
-- WORKSHOP_MANAGER: Goods Issues, Stock (READ), Stock Movements (READ), Products (READ)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,
    p.id
FROM role_app r,
    permission p
WHERE r.name = 'WORKSHOP_MANAGER'
    AND (
        (p.ressource = 'GOODS_ISSUE')
        OR (
            p.ressource = 'STOCK'
            AND p.action = 'READ'
        )
        OR (
            p.ressource = 'STOCK_MOVEMENT'
            AND p.action = 'READ'
        )
        OR (
            p.ressource = 'PRODUCT'
            AND p.action = 'READ'
        )
    ) ON DUPLICATE KEY
UPDATE role_id = role_id;
-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Run these to verify the setup:
--
-- Total permissions: Should be 34
-- SELECT COUNT(*) as total_permissions FROM permission;
--
-- Permissions by resource:
-- SELECT ressource, COUNT(*) as count 
-- FROM permission 
-- GROUP BY ressource 
-- ORDER BY ressource;
--
-- ADMIN permissions count: Should be 34 (all permissions)
-- SELECT COUNT(*) as admin_permissions
-- FROM role_permissions rp
-- JOIN role_app r ON rp.role_id = r.id
-- WHERE r.name = 'ADMIN';
--
-- AUDIT_LOGS permissions: Should only be READ, and only assigned to ADMIN
-- SELECT p.ressource, p.action, r.name as role_name
-- FROM permission p
-- LEFT JOIN role_permissions rp ON p.id = rp.permission_id
-- LEFT JOIN role_app r ON rp.role_id = r.id
-- WHERE p.ressource = 'AUDIT_LOGS'
-- ORDER BY r.name;
--
-- ============================================
-- SUMMARY
-- ============================================
-- Roles: 4 (ADMIN, PURCHASING_MANAGER, STOREKEEPER, WORKSHOP_MANAGER)
-- Total Permissions: 34
--   - STOCK: 4 (CREATE, READ, UPDATE, DELETE)
--   - SUPPLIER: 4 (CREATE, READ, UPDATE, DELETE)
--   - PRODUCT: 4 (CREATE, READ, UPDATE, DELETE)
--   - STOCK_MOVEMENT: 4 (CREATE, READ, UPDATE, DELETE)
--   - GOODS_ISSUE: 6 (CREATE, READ, UPDATE, DELETE, VALIDATE, CANCEL)
--   - PUCHASE_ORDER: 7 (CREATE, READ, UPDATE, DELETE, RECEIVE, VALIDATE, CANCEL)
--   - USER: 4 (CREATE, READ, UPDATE, DELETE)
--   - AUDIT_LOGS: 1 (READ only)
--
-- Permission Distribution:
--   - ADMIN: All 34 permissions (including AUDIT_LOGS:READ)
--   - PURCHASING_MANAGER: ~15 permissions (Suppliers, Products READ, Purchase Orders, Stock READ)
--   - STOREKEEPER: ~14 permissions (Stock, Stock Movements, Products READ, Purchase Orders READ/RECEIVE)
--   - WORKSHOP_MANAGER: ~10 permissions (Goods Issues, Stock READ, Stock Movements READ, Products READ)
-- ============================================