
INSERT INTO role_app (name, description) VALUES
('ADMIN', 'Administrator with full access to all system features'),
('PURCHASING_MANAGER', 'Responsible for managing purchases and suppliers'),
('STOREKEEPER', 'Manages stock, inventory, and warehouse operations'),
('WORKSHOP_MANAGER', 'Manages workshop operations and goods issues')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- ============================================



-- STOCK Permissions
INSERT INTO permission (ressource, action, description) VALUES
('STOCK', 'CREATE', 'Create stock entries'),
('STOCK', 'READ', 'View stock information'),
('STOCK', 'UPDATE', 'Update stock entries'),
('STOCK', 'DELETE', 'Delete stock entries'),
('STOCK', 'RECEIVE', 'Receive stock items'),
('STOCK', 'VALIDATE', 'Validate stock operations'),
('STOCK', 'CANCEL', 'Cancel stock operations')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- SUPPLIER Permissions
INSERT INTO permission (ressource, action, description) VALUES
('SUPPLIER', 'CREATE', 'Create new suppliers'),
('SUPPLIER', 'READ', 'View supplier information'),
('SUPPLIER', 'UPDATE', 'Update supplier details'),
('SUPPLIER', 'DELETE', 'Delete suppliers'),
('SUPPLIER', 'RECEIVE', 'Receive from suppliers'),
('SUPPLIER', 'VALIDATE', 'Validate supplier operations'),
('SUPPLIER', 'CANCEL', 'Cancel supplier operations')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- PRODUCT Permissions
INSERT INTO permission (ressource, action, description) VALUES
('PRODUCT', 'CREATE', 'Create new products'),
('PRODUCT', 'READ', 'View product information'),
('PRODUCT', 'UPDATE', 'Update product details'),
('PRODUCT', 'DELETE', 'Delete products'),
('PRODUCT', 'RECEIVE', 'Receive products'),
('PRODUCT', 'VALIDATE', 'Validate product operations'),
('PRODUCT', 'CANCEL', 'Cancel product operations')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- STOCK_MOVEMENT Permissions
INSERT INTO permission (ressource, action, description) VALUES
('STOCK_MOVEMENT', 'CREATE', 'Create stock movements'),
('STOCK_MOVEMENT', 'READ', 'View stock movement history'),
('STOCK_MOVEMENT', 'UPDATE', 'Update stock movements'),
('STOCK_MOVEMENT', 'DELETE', 'Delete stock movements'),
('STOCK_MOVEMENT', 'RECEIVE', 'Receive stock movements'),
('STOCK_MOVEMENT', 'VALIDATE', 'Validate stock movements'),
('STOCK_MOVEMENT', 'CANCEL', 'Cancel stock movements')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- GOODS_ISSUE Permissions
INSERT INTO permission (ressource, action, description) VALUES
('GOODS_ISSUE', 'CREATE', 'Create goods issue documents'),
('GOODS_ISSUE', 'READ', 'View goods issue documents'),
('GOODS_ISSUE', 'UPDATE', 'Update goods issue documents'),
('GOODS_ISSUE', 'DELETE', 'Delete goods issue documents'),
('GOODS_ISSUE', 'RECEIVE', 'Receive goods issue documents'),
('GOODS_ISSUE', 'VALIDATE', 'Validate goods issue documents'),
('GOODS_ISSUE', 'CANCEL', 'Cancel goods issue documents')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- PUCHASE_ORDER Permissions
INSERT INTO permission (ressource, action, description) VALUES
('PUCHASE_ORDER', 'CREATE', 'Create purchase orders'),
('PUCHASE_ORDER', 'READ', 'View purchase orders'),
('PUCHASE_ORDER', 'UPDATE', 'Update purchase orders'),
('PUCHASE_ORDER', 'DELETE', 'Delete purchase orders'),
('PUCHASE_ORDER', 'RECEIVE', 'Receive purchase orders'),
('PUCHASE_ORDER', 'VALIDATE', 'Validate purchase orders'),
('PUCHASE_ORDER', 'CANCEL', 'Cancel purchase orders')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- USER Permissions
INSERT INTO permission (ressource, action, description) VALUES
('USER', 'CREATE', 'Create new users'),
('USER', 'READ', 'View user information'),
('USER', 'UPDATE', 'Update user details and assign roles'),
('USER', 'DELETE', 'Delete users'),
('USER', 'RECEIVE', 'Receive user operations'),
('USER', 'VALIDATE', 'Validate user operations'),
('USER', 'CANCEL', 'Cancel user operations')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- ============================================
-- 3. INSERT DEFAULT ADMIN USER
-- ============================================
-- IMPORTANT: The BCrypt hash below is for password 'admin123'
-- If authentication fails, use one of these methods:
--
-- METHOD 1 (Recommended): Use the Register API endpoint first, then assign ADMIN role:
--   1. POST /api/v1/auth/register with email='admin@gmail.com', password='admin123', fullName='Admin'
--   2. Then use PUT /api/v1/users/{userId}/assign-role with roleName='ADMIN'
--
-- METHOD 2: Generate a new hash using Spring's BCryptPasswordEncoder and update the password:
--   UPDATE user_app SET password = '$2a$10$YOUR_NEW_HASH' WHERE email = 'admin@gmail.com';

INSERT INTO user_app (email, password, full_name, is_active, role_id, created_at, updated_at)
SELECT 
    'admin@gmail.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'System Administrator',
    true,
    (SELECT id FROM role_app WHERE name = 'ADMIN' LIMIT 1),
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_app WHERE email = 'admin@gmail.com');

-- ============================================


