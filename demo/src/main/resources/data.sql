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
-- Insert Maintenance Services
-- ================================
INSERT INTO maintenance_services (name, maintenance_service_type, description, est_time_minutes, current_price, status, created_at)
VALUES
    ('Battery Inspection', 'BATTERY_INSPECTION', 'Complete battery health check and diagnostics', 60.0, 150000, 'ACTIVE', GETDATE()),
    ('Tire Rotation', 'TIRE_ROTATION', 'Rotate all four tires for even wear', 30.0, 80000, 'ACTIVE', GETDATE()),
    ('Brake System Check', 'BRAKE_SYSTEM_CHECK', 'Inspect brake pads, rotors, and fluid levels', 90.0, 200000, 'ACTIVE', GETDATE()),
    ('Software Update', 'SOFTWARE_UPDATE', 'Update vehicle firmware and system software', 120.0, 300000, 'ACTIVE', GETDATE()),
    ('Air Filter Replacement', 'AIR_FILTER_REPLACEMENT', 'Replace cabin air filter for better air quality', 30.0, 100000, 'ACTIVE', GETDATE()),
    ('Coolant System Service', 'COOLANT_SYSTEM_SERVICE', 'Check and refill coolant for battery temperature management', 60.0, 180000, 'ACTIVE', GETDATE()),
    ('Suspension Inspection', 'SUSPENSION_INSPECTION', 'Inspect shocks, struts, and suspension components', 90.0, 250000, 'ACTIVE', GETDATE()),
    ('Wheel Alignment', 'WHEEL_ALIGNMENT', 'Adjust wheel alignment for optimal handling', 60.0, 220000, 'ACTIVE', GETDATE()),
    ('Full Vehicle Diagnostic', 'FULL_VEHICLE_DIAGNOSTIC', 'Comprehensive system diagnostic scan', 150.0, 400000, 'ACTIVE', GETDATE()),
    ('Emergency Charging Service', 'EMERGENCY_CHARGING_SERVICE', 'On-site battery charging service', 30.0, 120000, 'INACTIVE', GETDATE());
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
   ('VEHICLE_MODEL', 'read-by-status', 1, 'Read vehicle models'),
   ('VEHICLE_MODEL', 'create', 1, 'Create vehicle models'),
   ('VEHICLE_MODEL', 'update', 1, 'Update vehicle models'),
   ('VEHICLE_MODEL', 'delete', 1, 'Delete vehicle models'),
   ('BOOKING', 'create', 1, 'Create new booking'),
   ('BOOKING', 'read', 1, 'Read booking details'),
   ('BOOKING', 'update', 1, 'Update booking (staff/admin only)'),
   ('BOOKING', 'cancel', 1, 'Cancel booking');

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
-- Bookings for Customer 4
INSERT INTO bookings (customer_id, vin, schedule_date, status, total_price, payment_status, created_at, updated_at)
VALUES
    (4, '1HGCM82633A123456', DATEADD(DAY, 3, GETDATE()), 'PENDING', 350000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, 2, GETDATE()), 'CONFIRMED', 360000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, 1, GETDATE()), 'CHECKIN', 365000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', GETDATE(), 'INSPECTING', 370000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, -1, GETDATE()), 'WAITING_APPROVAL', 375000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, -2, GETDATE()), 'IN_PROGRESS', 380000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, -3, GETDATE()), 'COMPLETED', 385000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, -7, GETDATE()), 'DELIVERED', 385000, 'UNPAID', GETDATE(), GETDATE()),
    (4, '1HGCM82633A123456', DATEADD(DAY, -8, GETDATE()), 'CANCELLED', 200000, 'UNPAID', GETDATE(), GETDATE());

-- Bookings for Customer 5
INSERT INTO bookings (customer_id, vin, schedule_date, status, total_price, payment_status, created_at, updated_at)
VALUES
    (5, '5YJSA1E26HF123001', DATEADD(DAY, 3, GETDATE()), 'PENDING', 550000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, 2, GETDATE()), 'CONFIRMED', 560000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, 1, GETDATE()), 'CHECKIN', 565000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', GETDATE(), 'INSPECTING', 570000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, -1, GETDATE()), 'WAITING_APPROVAL', 575000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, -2, GETDATE()), 'IN_PROGRESS', 580000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, -3, GETDATE()), 'COMPLETED', 585000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, -7, GETDATE()), 'DELIVERED', 585000, 'UNPAID', GETDATE(), GETDATE()),
    (5, '5YJSA1E26HF123001', DATEADD(DAY, -8, GETDATE()), 'CANCELLED', 220000, 'UNPAID', GETDATE(), GETDATE());

-- Thêm cho Staff/Technician nếu staff/technician có quyền đặt booking thì bổ sung thêm user_id cho loại user đó ở customer_id.
-- Có thể thêm dòng tương tự cho Staff, Technician nếu muốn kiểm tra các trạng thái cho các vai trò khác.

-- Nếu muốn seed cho nhiều xe khác, lặp lại block phía trên với VIN, giá và thời gian khác nhau.

-- Nếu muốn booking nào test action từ trạng thái nào đến trạng thái nào, chỉ cần cập nhật field status, schedule_date và giá cho phù hợp.

-- ================================
-- Insert Booking Details (chuẩn cho SQL Server, không truyền id)
-- ================================

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (1, 1, 'Battery inspection needed', 150000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (1, 2, 'Tire rotation check', 80000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (2, 3, 'Brake pads replacement', 200000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (2, 4, 'Software system update', 300000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (3, 5, 'Air filter replacement', 100000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (3, 6, 'Coolant system refill', 180000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (4, 7, 'Suspension inspection', 250000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (4, 8, 'Wheel alignment', 220000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (5, 9, 'Full vehicle diagnostic', 400000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (5, 1, 'Battery health check', 150000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (6, 2, 'Tire rotation performed', 80000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (6, 3, 'Brake system checked', 200000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (7, 4, 'Software issue fixed', 300000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (7, 5, 'Cabin filter replaced', 100000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (8, 6, 'Coolant refill done', 180000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (8, 7, 'Suspension repaired', 250000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (9, 8, 'Wheel alignment corrected', 220000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (9, 9, 'Diagnostic scan performed', 400000);

-- Customer 5: Bookings 10-18
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (10, 1, 'Battery check done', 150000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (10, 2, 'Tire rotation scheduled', 80000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (11, 3, 'Brake fluid changed', 200000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (11, 4, 'Software feature upgraded', 300000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (12, 5, 'Air filter cleaned', 100000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (12, 6, 'Coolant leak fixed', 180000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (13, 7, 'Suspension tuning', 250000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (13, 8, 'Wheel balance check', 220000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (14, 9, 'Diagnostic before trip', 400000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (14, 1, 'Battery terminal cleaning', 150000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (15, 2, 'Summer tire change', 80000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (15, 3, 'Brake system updated', 200000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (16, 4, 'Software reinstallation', 300000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (16, 5, 'Cabin filter refreshed', 100000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (17, 6, 'Coolant replaced', 180000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (17, 7, 'Suspension component changed', 250000);

INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (18, 8, 'Wheel tracking checked', 220000);
INSERT INTO booking_details (booking_id, maintenance_service_id, description, service_price) VALUES (18, 9, 'Vehicle system scanned', 400000);

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

-- ================================
-- MAINTENANCE_SERVICE DOMAIN - Permissions & Role Mapping
-- ================================

-- Insert Maintenance Service Permissions
INSERT INTO permissions (resource, action, is_active, description) VALUES
    ('MAINTENANCE_SERVICE', 'read', 1, 'Read maintenance services'),
    ('MAINTENANCE_SERVICE', 'create', 1, 'Create maintenance services'),
    ('MAINTENANCE_SERVICE', 'update', 1, 'Update maintenance services'),
    ('MAINTENANCE_SERVICE', 'delete', 1, 'Delete maintenance services');

-- 1) ADMIN: MAINTENANCE_SERVICE all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, p.id
FROM permissions p
WHERE p.resource = 'MAINTENANCE_SERVICE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 1 AND rp.permission_id = p.id
);

-- 2) STAFF: MAINTENANCE_SERVICE all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p
WHERE p.resource = 'MAINTENANCE_SERVICE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 2 AND rp.permission_id = p.id
);

-- 3) TECHNICIAN: MAINTENANCE_SERVICE read only
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, p.id
FROM permissions p
WHERE p.resource = 'MAINTENANCE_SERVICE'
  AND p.action = 'read'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 3 AND rp.permission_id = p.id
);

-- 4) CUSTOMER: MAINTENANCE_SERVICE read only
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, p.id
FROM permissions p
WHERE p.resource = 'MAINTENANCE_SERVICE'
  AND p.action = 'read'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = 4 AND rp.permission_id = p.id
);
