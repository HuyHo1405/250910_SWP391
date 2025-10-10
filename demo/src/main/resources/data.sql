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
-- Insert User (mk:string)
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
-- Insert Services
-- ================================
INSERT INTO services (name, description, est_time_hours, current_price, status, created_at)
VALUES
    ('Battery Inspection', 'Complete battery health check and diagnostics', 1.0, 150000, 'ACTIVE', GETDATE()),
    ('Tire Rotation', 'Rotate all four tires for even wear', 0.5, 80000, 'ACTIVE', GETDATE()),
    ('Brake System Check', 'Inspect brake pads, rotors, and fluid levels', 1.5, 200000, 'ACTIVE', GETDATE()),
    ('Software Update', 'Update vehicle firmware and system software', 2.0, 300000, 'ACTIVE', GETDATE()),
    ('Air Filter Replacement', 'Replace cabin air filter for better air quality', 0.5, 100000, 'ACTIVE', GETDATE()),
    ('Coolant System Service', 'Check and refill coolant for battery temperature management', 1.0, 180000, 'ACTIVE', GETDATE()),
    ('Suspension Inspection', 'Inspect shocks, struts, and suspension components', 1.5, 250000, 'ACTIVE', GETDATE()),
    ('Wheel Alignment', 'Adjust wheel alignment for optimal handling', 1.0, 220000, 'ACTIVE', GETDATE()),
    ('Full Vehicle Diagnostic', 'Comprehensive system diagnostic scan', 2.5, 400000, 'ACTIVE', GETDATE()),
    ('Emergency Charging Service', 'On-site battery charging service', 0.5, 120000, 'INACTIVE', GETDATE());

-- ================================
-- Insert Vehicle Permissions
-- ================================
INSERT INTO permissions (resource, action, is_active, description) VALUES
   ('SYSTEM', 'bypass_ownership', 1, 'Bypass ownership checks for all resources'),
   ('VEHICLE', 'read', 1, 'Read vehicles'),
   ('VEHICLE', 'create', 1, 'Create vehicles'),
   ('VEHICLE', 'update', 1, 'Update vehicles'),
   ('VEHICLE', 'delete', 1, 'Delete vehicles'),
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

-- ================================
-- Insert Bookings
-- ================================
INSERT INTO bookings (customer_id, vin, schedule_date, status, total_price, created_at, updated_at)
VALUES
    -- Customer 4 (Pham Thi D) bookings
    (4, '1HGCM82633A123456', DATEADD(DAY, 3, GETDATE()), 'PENDING', 350000, GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, -7, GETDATE()), 'COMPLETED', 500000, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -7, GETDATE())),
    (4, '1HGCM82633A654321', DATEADD(DAY, 5, GETDATE()), 'CONFIRMED', 280000, GETDATE(), GETDATE()),
    (4, '1HGCM82633A654321', DATEADD(DAY, -14, GETDATE()), 'CANCELLED', 200000, DATEADD(DAY, -20, GETDATE()), DATEADD(DAY, -14, GETDATE())),

    -- Customer 5 (Nguyen Van E) bookings
    (5, '5YJSA1E26HF123001', DATEADD(DAY, 1, GETDATE()), 'CONFIRMED', 600000, GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, -3, GETDATE()), 'IN_PROGRESS', 450000, DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -3, GETDATE())),
    (5, '5YJSA1E26HF123002', DATEADD(DAY, 7, GETDATE()), 'PENDING', 180000, GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123002', DATEADD(DAY, -21, GETDATE()), 'COMPLETED', 380000, DATEADD(DAY, -25, GETDATE()), DATEADD(DAY, -21, GETDATE()));

-- ================================
-- Insert Booking Details
-- ================================
INSERT INTO booking_details (booking_id, service_id, description, service_price)
VALUES
    -- Booking 1 details (customer 4, booking 1)
    (1, 1, 'Battery showing signs of degradation', 150000),
    (1, 3, 'Front brake pads need replacement', 200000),

    -- Booking 2 details (customer 4, booking 2 - completed)
    (2, 4, 'Major software update applied', 300000),
    (2, 3, 'Brake system fully serviced', 200000),

    -- Booking 3 details (customer 4, booking 3)
    (3, 2, 'Standard tire rotation', 80000),
    (3, 5, 'Replaced old cabin filter', 100000),
    (3, 6, 'Coolant topped up', 100000),

    -- Booking 4 details (customer 4, booking 4 - cancelled)
    (4, 3, 'Cancelled before service', 200000),

    -- Booking 5 details (customer 5, booking 1)
    (5, 9, 'Full diagnostic before long trip', 400000),
    (5, 3, 'Brake inspection included', 200000),

    -- Booking 6 details (customer 5, booking 2 - in progress)
    (6, 1, 'Battery health check ongoing', 150000),
    (6, 4, 'Software update in progress', 300000),

    -- Booking 7 details (customer 5, booking 3)
    (7, 6, 'Scheduled coolant service', 180000),

    -- Booking 8 details (customer 5, booking 4 - completed)
    (8, 7, 'Suspension repaired', 250000),
    (8, 2, 'Tire rotation completed', 80000),
    (8, 5, 'Air filter replaced', 50000);

-- ================================
-- Insert Booking Permissions
-- ================================
INSERT INTO permissions (resource, action, is_active, description) VALUES
    ('BOOKING', 'create', 1, 'Create new booking'),
    ('BOOKING', 'read', 1, 'Read booking details'),
    ('BOOKING', 'update', 1, 'Update booking (staff/admin only)'),
    ('BOOKING', 'cancel', 1, 'Cancel booking');

-- ================================
-- Role - Permission Mapping (CRUD booking)
-- ================================

-- 1) ADMIN: grant BOOKING ALL permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, p.id
FROM permissions p
WHERE p.resource = 'BOOKING'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 1 AND rp.permission_id = p.id
);

-- 2) STAFF: grant BOOKING ALL permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p
WHERE p.resource = 'BOOKING'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 2 AND rp.permission_id = p.id
);

-- 3) TECHNICIAN: BOOKING read only
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, p.id
FROM permissions p
WHERE p.resource = 'BOOKING'
  AND p.action = 'read'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 3 AND rp.permission_id = p.id
);

-- 4) CUSTOMER: BOOKING create/read/cancel only
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, p.id
FROM permissions p
WHERE p.resource = 'BOOKING'
  AND p.action IN ('create','read','cancel')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 4 AND rp.permission_id = p.id
);