INSERT INTO role_app (name, description)
VALUES ('ADMIN', 'Full system access'),
    (
        'PURCHASING_MANAGER',
        'Manages purchases and suppliers'
    ),
    ('STOREKEEPER', 'Manages stock and warehouse'),
    (
        'WORKSHOP_MANAGER',
        'Manages workshop and goods issues'
    ) ON DUPLICATE KEY
UPDATE description =
VALUES(description);
INSERT INTO permission (ressource, action, description)
VALUES ('STOCK', 'CREATE', 'Create stock entries'),
    ('STOCK', 'READ', 'View stock'),
    ('STOCK', 'UPDATE', 'Update stock'),
    ('STOCK', 'DELETE', 'Delete stock'),
    ('SUPPLIER', 'CREATE', 'Create suppliers'),
    ('SUPPLIER', 'READ', 'View suppliers'),
    ('SUPPLIER', 'UPDATE', 'Update suppliers'),
    ('SUPPLIER', 'DELETE', 'Delete suppliers'),
    ('PRODUCT', 'CREATE', 'Create products'),
    ('PRODUCT', 'READ', 'View products'),
    ('PRODUCT', 'UPDATE', 'Update products'),
    ('PRODUCT', 'DELETE', 'Delete products'),
    (
        'STOCK_MOVEMENT',
        'CREATE',
        'Create stock movements'
    ),
    ('STOCK_MOVEMENT', 'READ', 'View stock movements'),
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
    ('GOODS_ISSUE', 'CREATE', 'Create goods issues'),
    ('GOODS_ISSUE', 'READ', 'View goods issues'),
    ('GOODS_ISSUE', 'UPDATE', 'Update goods issues'),
    ('GOODS_ISSUE', 'DELETE', 'Delete goods issues'),
    (
        'GOODS_ISSUE',
        'VALIDATE',
        'Validate goods issues'
    ),
    ('GOODS_ISSUE', 'CANCEL', 'Cancel goods issues'),
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
    ('USER', 'CREATE', 'Create users'),
    ('USER', 'READ', 'View users'),
    ('USER', 'UPDATE', 'Update users'),
    ('USER', 'DELETE', 'Delete users'),
    ('AUDIT_LOGS', 'READ', 'View audit logs') ON DUPLICATE KEY
UPDATE description =
VALUES(description);
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,
    p.id
FROM role_app r,
    permission p
WHERE r.name = 'ADMIN' ON DUPLICATE KEY
UPDATE role_id = role_id;
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
        'admin@tricol.ma',
        '$2a$10$d7lQYLq0KYyzuqYZ0CP7zOt3TpOPfrlluas7CXKKJRBJ/wGcT3toS',
        'Admin User',
        true,
        (
            SELECT id
            FROM role_app
            WHERE name = 'ADMIN'
        ),
        NOW(),
        NOW()
    ),
    (
        'achats@tricol.ma',
        '$2a$10$d7lQYLq0KYyzuqYZ0CP7zOt3TpOPfrlluas7CXKKJRBJ/wGcT3toS',
        'Responsable Achats',
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
        'magasin@tricol.ma',
        '$2a$10$d7lQYLq0KYyzuqYZ0CP7zOt3TpOPfrlluas7CXKKJRBJ/wGcT3toS',
        'Magasinier',
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
        'atelier@tricol.ma',
        '$2a$10$d7lQYLq0KYyzuqYZ0CP7zOt3TpOPfrlluas7CXKKJRBJ/wGcT3toS',
        'Chef Atelier',
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