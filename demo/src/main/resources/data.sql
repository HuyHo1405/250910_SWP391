-- ================================
-- Insert Roles
-- ================================
INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('STAFF'),
    ('TECHNICIAN'),
    ('CUSTOMER');

-- ================================
-- Insert Vehicle Models
-- ================================
INSERT INTO vehicle_models
(brand_name, model_name, dimensions, year_introduce, seats, battery_capacity_kwh, range_km, charging_time_hours, motor_power_kw, weight_kg, status, created_at)
VALUES
    ('Tesla', 'Model S', '4970x1964x1445', '2022', 5, 100.0, 650.0, 1.5, 500.0, 2100.0, 'ACTIVE', GETDATE()),
    ('VinFast', 'VF8', '4750x1900x1660', '2023', 5, 90.0, 550.0, 2.0, 400.0, 2200.0, 'ACTIVE', GETDATE()),
    ('Toyota', 'Corolla Cross EV', '4460x1825x1620', '2023', 5, 60.0, 400.0, 1.8, 150.0, 1600.0, 'INACTIVE', GETDATE());

-- ================================
-- Insert User (mk:123456)
-- ================================
INSERT INTO users (full_name, email_address, phone_number, hashed_password, role_id, status, created_at, update_at)
VALUES
    ('Nguyen Van A', 'admin@example.com', '0901234567', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 1, 'ACTIVE', GETDATE(), GETDATE()),
    ('Tran Thi B', 'staff@example.com', '0902345678', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', GETDATE(), GETDATE()),
    ('Le Van C', 'technician@example.com', '0903456789', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', GETDATE(), GETDATE()),
    ('Pham Thi D', 'customer@example.com', '0904567890', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE()),
    ('Nguyen Van E', 'user@example.com', '0905678901','$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE());

-- ================================
-- Insert Vehicles (thuộc user và model)
-- ================================
INSERT INTO vehicles
(plate_number, color, vin, customer_id, vehicle_model_id, entity_status, created_at, purchased_at)
VALUES
    ('51H-12345', 'Red', '1HGCM82633A123456', 4, 1, 'ACTIVE', GETDATE(), GETDATE()),
    ('51H-67890', 'Blue', '1HGCM82633A654321', 4, 2, 'ACTIVE', GETDATE(), GETDATE()),
    ('60A-22222', 'White', '1HGCM82633A888888', 4, 3, 'INACTIVE', GETDATE(), GETDATE()),
    ('51G-55555', 'Black', '5YJSA1E26HF123001', 5, 1, 'ACTIVE', GETDATE(), GETDATE()),  -- Tesla Model S
    ('59A-66666', 'Silver', '5YJSA1E26HF123002', 5, 2, 'ACTIVE', GETDATE(), GETDATE()), -- VinFast VF8
    ('51C-77777', 'White', '5YJSA1E26HF123003', 5, 3, 'INACTIVE', GETDATE(), GETDATE());

-- ================================
-- Insert Vehicle Permissions
-- ================================
INSERT INTO permissions (resource, action, is_active, description) VALUES
   ('SYSTEM', 'bypass_ownership', 1, 'Bypass ownership checks for all resources'),
   ('VEHICLE', 'read', 1, 'Read vehicles'),  -- ← THÊM description
   ('VEHICLE', 'create', 1, 'Create vehicles'),  -- ← THÊM description
   ('VEHICLE', 'update', 1, 'Update vehicles'),  -- ← THÊM description
   ('VEHICLE', 'delete', 1, 'Delete vehicles'),  -- ← THÊM description
   ('VEHICLE_MODEL', 'read', 1, 'Read vehicle models'),
   ('VEHICLE_MODEL', 'create', 1, 'Create vehicle models'),
   ('VEHICLE_MODEL', 'update', 1, 'Update vehicle models'),
   ('VEHICLE_MODEL', 'delete', 1, 'Delete vehicle models');

-- ================================
-- Role - Permission Mapping
-- ================================

-- 1) ADMIN: grant ALL permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, p.id
FROM permissions p
WHERE NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 1 AND rp.permission_id = p.id
);

-- 2) STAFF: Grant bypass_ownership FIRST! ← FIX
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p
WHERE p.resource = 'SYSTEM'
  AND p.action = 'bypass_ownership'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 2 AND rp.permission_id = p.id
);

-- 2a) STAFF: VEHICLE all
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p
WHERE p.resource = 'VEHICLE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 2 AND rp.permission_id = p.id
);

-- 2b) STAFF: VEHICLE_MODEL read/create/update (no delete)
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p
WHERE p.resource = 'VEHICLE_MODEL'
  AND p.action IN ('read','create','update')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 2 AND rp.permission_id = p.id
);

-- 3) TECHNICIAN: VEHICLE + VEHICLE_MODEL read only
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, p.id
FROM permissions p
WHERE p.resource IN ('VEHICLE','VEHICLE_MODEL')
  AND p.action = 'read'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 3 AND rp.permission_id = p.id
);

-- 4) CUSTOMER: VEHICLE all (NO bypass_ownership)
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, p.id
FROM permissions p
WHERE p.resource = 'VEHICLE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 4 AND rp.permission_id = p.id
);

-- 4b) CUSTOMER: VEHICLE_MODEL read only
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, p.id
FROM permissions p
WHERE p.resource = 'VEHICLE_MODEL'
  AND p.action = 'read'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 4 AND rp.permission_id = p.id
);
