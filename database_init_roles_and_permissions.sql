-- ============================================
-- TRICOL V2 - COMPLETE DATABASE INITIALIZATION
-- ============================================
-- Run this script to populate the entire database
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
-- 2. PERMISSIONS (All resources x All actions)
-- ============================================
INSERT INTO permission (ressource, action, description)
VALUES -- STOCK
    ('STOCK', 'CREATE', 'Create stock entries'),
    ('STOCK', 'READ', 'View stock information'),
    ('STOCK', 'UPDATE', 'Update stock entries'),
    ('STOCK', 'DELETE', 'Delete stock entries'),
    ('STOCK', 'RECEIVE', 'Receive stock items'),
    ('STOCK', 'VALIDATE', 'Validate stock operations'),
    ('STOCK', 'CANCEL', 'Cancel stock operations'),
    -- SUPPLIER
    ('SUPPLIER', 'CREATE', 'Create new suppliers'),
    ('SUPPLIER', 'READ', 'View supplier information'),
    ('SUPPLIER', 'UPDATE', 'Update supplier details'),
    ('SUPPLIER', 'DELETE', 'Delete suppliers'),
    ('SUPPLIER', 'RECEIVE', 'Receive from suppliers'),
    (
        'SUPPLIER',
        'VALIDATE',
        'Validate supplier operations'
    ),
    (
        'SUPPLIER',
        'CANCEL',
        'Cancel supplier operations'
    ),
    -- PRODUCT
    ('PRODUCT', 'CREATE', 'Create new products'),
    ('PRODUCT', 'READ', 'View product information'),
    ('PRODUCT', 'UPDATE', 'Update product details'),
    ('PRODUCT', 'DELETE', 'Delete products'),
    ('PRODUCT', 'RECEIVE', 'Receive products'),
    (
        'PRODUCT',
        'VALIDATE',
        'Validate product operations'
    ),
    ('PRODUCT', 'CANCEL', 'Cancel product operations'),
    -- STOCK_MOVEMENT
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
    (
        'STOCK_MOVEMENT',
        'RECEIVE',
        'Receive stock movements'
    ),
    (
        'STOCK_MOVEMENT',
        'VALIDATE',
        'Validate stock movements'
    ),
    (
        'STOCK_MOVEMENT',
        'CANCEL',
        'Cancel stock movements'
    ),
    -- GOODS_ISSUE
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
        'RECEIVE',
        'Receive goods issue documents'
    ),
    (
        'GOODS_ISSUE',
        'VALIDATE',
        'Validate goods issue documents'
    ),
    (
        'GOODS_ISSUE',
        'CANCEL',
        'Cancel goods issue documents'
    ),
    -- PUCHASE_ORDER
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
        'Receive purchase orders'
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
    -- USER
    ('USER', 'CREATE', 'Create new users'),
    ('USER', 'READ', 'View user information'),
    (
        'USER',
        'UPDATE',
        'Update user details and assign roles'
    ),
    ('USER', 'DELETE', 'Delete users'),
    ('USER', 'RECEIVE', 'Receive user operations'),
    ('USER', 'VALIDATE', 'Validate user operations'),
    ('USER', 'CANCEL', 'Cancel user operations') ON DUPLICATE KEY
UPDATE description =
VALUES(description);
-- ============================================
-- 3. ROLE-PERMISSION MAPPINGS
-- ============================================
-- ADMIN gets ALL permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,
    p.id
FROM role_app r,
    permission p
WHERE r.name = 'ADMIN' ON DUPLICATE KEY
UPDATE role_id = role_id;
-- PURCHASING_MANAGER permissions
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
            AND p.action IN ('READ')
        )
        OR (p.ressource = 'PUCHASE_ORDER')
        OR (
            p.ressource = 'STOCK'
            AND p.action IN ('READ')
        )
    ) ON DUPLICATE KEY
UPDATE role_id = role_id;
-- STOREKEEPER permissions
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
            AND p.action IN ('READ')
        )
        OR (
            p.ressource = 'PUCHASE_ORDER'
            AND p.action IN ('READ', 'RECEIVE')
        )
    ) ON DUPLICATE KEY
UPDATE role_id = role_id;
-- WORKSHOP_MANAGER permissions
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
            AND p.action IN ('READ')
        )
        OR (
            p.ressource = 'STOCK_MOVEMENT'
            AND p.action IN ('READ')
        )
        OR (
            p.ressource = 'PRODUCT'
            AND p.action IN ('READ')
        )
    ) ON DUPLICATE KEY
UPDATE role_id = role_id;
-- ============================================
-- 4. USERS (Admin + 3 sample users)
-- ============================================
-- Admin user (password: admin)
INSERT INTO user_app (
        email,
        password,
        full_name,
        is_active,
        role_id,
        created_at,
        updated_at
    )
SELECT 'admin@gmail.com',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
    'System Administrator',
    true,
    (
        SELECT id
        FROM role_app
        WHERE name = 'ADMIN'
    ),
    NOW(),
    NOW()
WHERE NOT EXISTS (
        SELECT 1
        FROM user_app
        WHERE email = 'admin@gmail.com'
    );
-- Update admin if exists
UPDATE user_app
SET password = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
    role_id = (
        SELECT id
        FROM role_app
        WHERE name = 'ADMIN'
    )
WHERE email = 'admin@gmail.com';
-- Sample users (password for all: password123 -> hash)
INSERT INTO user_app (
        email,
        password,
        full_name,
        is_active,
        role_id,
        created_at,
        updated_at
    )
VALUES (
        'purchasing@tricol.com',
        '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
        'Ahmed Benali',
        true,
        (
            SELECT id
            FROM role_app
            WHERE name = 'PURCHASING_MANAGER'
        ),
        NOW(),
        NOW()
    ),
    (
        'storekeeper@tricol.com',
        '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
        'Youssef Alaoui',
        true,
        (
            SELECT id
            FROM role_app
            WHERE name = 'STOREKEEPER'
        ),
        NOW(),
        NOW()
    ),
    (
        'workshop@tricol.com',
        '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
        'Karim Fassi',
        true,
        (
            SELECT id
            FROM role_app
            WHERE name = 'WORKSHOP_MANAGER'
        ),
        NOW(),
        NOW()
    ) ON DUPLICATE KEY
UPDATE full_name =
VALUES(full_name);
-- ============================================
-- 5. SUPPLIERS (3 records)
-- ============================================
INSERT INTO suppliers (
        company_name,
        address,
        city,
        email,
        phone,
        ice,
        contact_person
    )
VALUES (
        'TechSupply Maroc',
        '123 Boulevard Mohammed V',
        'Casablanca',
        'contact@techsupply.ma',
        '0522-123456',
        'ICE001234567890',
        'Hassan El Mansouri'
    ),
    (
        'Industrial Parts SA',
        '45 Avenue Hassan II',
        'Rabat',
        'sales@indparts.ma',
        '0537-654321',
        'ICE001234567891',
        'Fatima Zahra Bennani'
    ),
    (
        'Global Equipment SARL',
        '78 Rue Ibn Sina',
        'Marrakech',
        'info@globalequip.ma',
        '0524-789012',
        'ICE001234567892',
        'Omar Tazi'
    ) ON DUPLICATE KEY
UPDATE company_name =
VALUES(company_name);
-- ============================================
-- 6. PRODUCTS (3 records)
-- ============================================
INSERT INTO products (
        reference,
        name,
        description,
        unit_price,
        category,
        reorder_point,
        unit_of_measure
    )
VALUES (
        'PROD-001',
        'Laptop HP ProBook 450',
        'HP ProBook 450 G8 - Intel Core i7, 16GB RAM, 512GB SSD',
        12500,
        'Electronics',
        5.0,
        'UNIT'
    ),
    (
        'PROD-002',
        'Office Chair Ergonomic',
        'Ergonomic office chair with lumbar support',
        2500,
        'Furniture',
        10.0,
        'UNIT'
    ),
    (
        'PROD-003',
        'A4 Paper Box',
        'Premium A4 paper, 500 sheets per ream, 5 reams per box',
        350,
        'Office Supplies',
        20.0,
        'BOX'
    ) ON DUPLICATE KEY
UPDATE name =
VALUES(name);
-- ============================================
-- 7. PURCHASE ORDERS (3 records - DELIVERED status for stock)
-- ============================================
INSERT INTO purchase_orders (order_date, status, total_amount, supplier_id)
VALUES (
        '2025-12-01',
        'DELIVERED',
        137500.0,
        (
            SELECT id
            FROM suppliers
            WHERE ice = 'ICE001234567890'
            LIMIT 1
        )
    ), (
        '2025-12-05', 'DELIVERED', 50000.0, (
            SELECT id
            FROM suppliers
            WHERE ice = 'ICE001234567891'
            LIMIT 1
        )
    ), (
        '2025-12-10', 'PENDING', 7000.0, (
            SELECT id
            FROM suppliers
            WHERE ice = 'ICE001234567892'
            LIMIT 1
        )
    );
-- ============================================
-- 8. PURCHASE ORDER LINES (linked to orders and products)
-- ============================================
-- Lines for Order 1 (TechSupply - Laptops)
INSERT INTO purchase_order_lines (
        quantity,
        unit_price,
        purchase_order_id,
        product_id
    )
VALUES (
        10.0,
        12500.0,
        (
            SELECT id
            FROM purchase_orders
            WHERE order_date = '2025-12-01'
                AND status = 'DELIVERED'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-001'
            LIMIT 1
        )
    ), (
        5.0, 2500.0, (
            SELECT id
            FROM purchase_orders
            WHERE order_date = '2025-12-01'
                AND status = 'DELIVERED'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        )
    );
-- Lines for Order 2 (Industrial Parts - Chairs)
INSERT INTO purchase_order_lines (
        quantity,
        unit_price,
        purchase_order_id,
        product_id
    )
VALUES (
        20.0,
        2500.0,
        (
            SELECT id
            FROM purchase_orders
            WHERE order_date = '2025-12-05'
                AND status = 'DELIVERED'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        )
    );
-- Lines for Order 3 (Global Equipment - Paper) - PENDING
INSERT INTO purchase_order_lines (
        quantity,
        unit_price,
        purchase_order_id,
        product_id
    )
VALUES (
        20.0,
        350.0,
        (
            SELECT id
            FROM purchase_orders
            WHERE order_date = '2025-12-10'
                AND status = 'PENDING'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-003'
            LIMIT 1
        )
    );
-- ============================================
-- 9. STOCK LOTS (from delivered orders)
-- ============================================
INSERT INTO stock_lots (
        lot_number,
        entry_date,
        purchase_price,
        remaining_quantity,
        initial_quantity,
        product_id,
        purchase_order_line_id
    )
VALUES -- Lot from Order 1 - Laptops
    (
        'LOT-PROD001-20251201-001',
        '2025-12-01',
        12500.0,
        10.0,
        10.0,
        (
            SELECT id
            FROM products
            WHERE reference = 'PROD-001'
            LIMIT 1
        ), (
            SELECT pol.id
            FROM purchase_order_lines pol
                JOIN purchase_orders po ON pol.purchase_order_id = po.id
            WHERE po.order_date = '2025-12-01'
                AND pol.product_id = (
                    SELECT id
                    FROM products
                    WHERE reference = 'PROD-001'
                )
            LIMIT 1
        )
    ), -- Lot from Order 1 - Chairs (first batch)
    (
        'LOT-PROD002-20251201-001', '2025-12-01', 2500.0, 5.0, 5.0, (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        ), (
            SELECT pol.id
            FROM purchase_order_lines pol
                JOIN purchase_orders po ON pol.purchase_order_id = po.id
            WHERE po.order_date = '2025-12-01'
                AND pol.product_id = (
                    SELECT id
                    FROM products
                    WHERE reference = 'PROD-002'
                )
            LIMIT 1
        )
    ), -- Lot from Order 2 - Chairs (second batch)
    (
        'LOT-PROD002-20251205-001', '2025-12-05', 2500.0, 20.0, 20.0, (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        ), (
            SELECT pol.id
            FROM purchase_order_lines pol
                JOIN purchase_orders po ON pol.purchase_order_id = po.id
            WHERE po.order_date = '2025-12-05'
            LIMIT 1
        )
    );
-- ============================================
-- 10. STOCK MOVEMENTS (IN movements from received orders)
-- ============================================
INSERT INTO stock_movements (
        movement_date,
        quantity,
        movement_type,
        product_id,
        stock_lot_id,
        purchase_order_line_id
    )
VALUES -- Movement for Laptops
    (
        '2025-12-01',
        10.0,
        'IN',
        (
            SELECT id
            FROM products
            WHERE reference = 'PROD-001'
            LIMIT 1
        ), (
            SELECT id
            FROM stock_lots
            WHERE lot_number = 'LOT-PROD001-20251201-001'
            LIMIT 1
        ), (
            SELECT pol.id
            FROM purchase_order_lines pol
                JOIN purchase_orders po ON pol.purchase_order_id = po.id
            WHERE po.order_date = '2025-12-01'
                AND pol.product_id = (
                    SELECT id
                    FROM products
                    WHERE reference = 'PROD-001'
                )
            LIMIT 1
        )
    ), -- Movement for Chairs (batch 1)
    (
        '2025-12-01', 5.0, 'IN', (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        ), (
            SELECT id
            FROM stock_lots
            WHERE lot_number = 'LOT-PROD002-20251201-001'
            LIMIT 1
        ), (
            SELECT pol.id
            FROM purchase_order_lines pol
                JOIN purchase_orders po ON pol.purchase_order_id = po.id
            WHERE po.order_date = '2025-12-01'
                AND pol.product_id = (
                    SELECT id
                    FROM products
                    WHERE reference = 'PROD-002'
                )
            LIMIT 1
        )
    ), -- Movement for Chairs (batch 2)
    (
        '2025-12-05', 20.0, 'IN', (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        ), (
            SELECT id
            FROM stock_lots
            WHERE lot_number = 'LOT-PROD002-20251205-001'
            LIMIT 1
        ), (
            SELECT pol.id
            FROM purchase_order_lines pol
                JOIN purchase_orders po ON pol.purchase_order_id = po.id
            WHERE po.order_date = '2025-12-05'
            LIMIT 1
        )
    );
-- ============================================
-- 11. GOODS ISSUES (3 records with different statuses)
-- ============================================
INSERT INTO goods_issues (
        issue_number,
        issue_date,
        destination,
        motif,
        status
    )
VALUES (
        'GI-20251210-001',
        '2025-12-10',
        'Workshop A - Production Line',
        'PRODUCTION',
        'VALIDATED'
    ),
    (
        'GI-20251215-001',
        '2025-12-15',
        'IT Department',
        'MAINTENANCE',
        'DRAFT'
    ),
    (
        'GI-20251218-001',
        '2025-12-18',
        'Admin Office',
        'OTHER',
        'DRAFT'
    );
-- ============================================
-- 12. GOODS ISSUE LINES
-- ============================================
-- Lines for validated issue (GI-20251210-001) - 2 chairs consumed
INSERT INTO goods_issue_lines (quantity, goods_issue_id, product_id)
VALUES (
        2.0,
        (
            SELECT id
            FROM goods_issues
            WHERE issue_number = 'GI-20251210-001'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        )
    );
-- Lines for draft issue (GI-20251215-001) - 1 laptop requested
INSERT INTO goods_issue_lines (quantity, goods_issue_id, product_id)
VALUES (
        1.0,
        (
            SELECT id
            FROM goods_issues
            WHERE issue_number = 'GI-20251215-001'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-001'
            LIMIT 1
        )
    );
-- Lines for draft issue (GI-20251218-001) - 3 chairs requested
INSERT INTO goods_issue_lines (quantity, goods_issue_id, product_id)
VALUES (
        3.0,
        (
            SELECT id
            FROM goods_issues
            WHERE issue_number = 'GI-20251218-001'
            LIMIT 1
        ), (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        )
    );
-- ============================================
-- 13. UPDATE STOCK FOR VALIDATED GOODS ISSUE
-- ============================================
-- Reduce stock for the validated goods issue (FIFO - oldest lot first)
UPDATE stock_lots
SET remaining_quantity = remaining_quantity - 2.0
WHERE lot_number = 'LOT-PROD002-20251201-001';
-- Create OUT movement for the validated goods issue
INSERT INTO stock_movements (
        movement_date,
        quantity,
        movement_type,
        product_id,
        stock_lot_id,
        goods_issue_line_id
    )
VALUES (
        '2025-12-10',
        2.0,
        'OUT',
        (
            SELECT id
            FROM products
            WHERE reference = 'PROD-002'
            LIMIT 1
        ), (
            SELECT id
            FROM stock_lots
            WHERE lot_number = 'LOT-PROD002-20251201-001'
            LIMIT 1
        ), (
            SELECT gil.id
            FROM goods_issue_lines gil
                JOIN goods_issues gi ON gil.goods_issue_id = gi.id
            WHERE gi.issue_number = 'GI-20251210-001'
            LIMIT 1
        )
    );
-- ============================================
-- SUMMARY
-- ============================================
-- Users: 4 (admin + 3 sample users)
-- Roles: 4 (ADMIN, PURCHASING_MANAGER, STOREKEEPER, WORKSHOP_MANAGER)
-- Permissions: 49 (7 resources x 7 actions)
-- Suppliers: 3
-- Products: 3
-- Purchase Orders: 3 (2 DELIVERED, 1 PENDING)
-- Purchase Order Lines: 4
-- Stock Lots: 3
-- Stock Movements: 4 (3 IN, 1 OUT)
-- Goods Issues: 3 (1 VALIDATED, 2 DRAFT)
-- Goods Issue Lines: 3
--
-- Login credentials:
-- admin@gmail.com / admin (ADMIN)
-- purchasing@tricol.com / admin (PURCHASING_MANAGER)
-- storekeeper@tricol.com / admin (STOREKEEPER)
-- workshop@tricol.com / admin (WORKSHOP_MANAGER)
-- ============================================