-- ===================================================================
-- SCRIPT SEED DATA TỔNG HỢP
-- Version: 2.0 (Đã tổ chức lại và bổ sung đầy đủ permissions)
-- Database: SQL Server
-- ===================================================================

-- ===================================================================
-- BẢNG 1: ROLES - Định nghĩa các vai trò trong hệ thống
-- ===================================================================

INSERT INTO roles (name, display_name) VALUES
('ADMIN', 'Admin'),
('STAFF', 'Staff Employee'),
('TECHNICIAN', 'Technician Employee'),
('CUSTOMER', 'Customer');

-- ADMIN có tất cả các quyền chỉnh sửa/tạo (all)
INSERT INTO role_editable (role_id, editable_id) VALUES
(1, 1), -- admin chỉnh admin
(1, 2), -- admin chỉnh staff
(1, 3), -- admin chỉnh technician
(1, 4); -- admin chỉnh customer

-- STAFF chỉ chỉnh/tạo được TECHNICIAN và CUSTOMER
INSERT INTO role_editable (role_id, editable_id) VALUES
(2, 3), -- staff chỉnh technician
(2, 4); -- staff chỉnh customer


-- ===================================================================
-- BẢNG 2: VEHICLE_MODELS - Danh sách các mẫu xe
-- ===================================================================
IF NOT EXISTS (SELECT 1 FROM vehicle_models WHERE brand_name = 'Tesla' AND model_name = 'Model S')
INSERT INTO vehicle_models
(brand_name, model_name, dimensions, year_introduce, seats, battery_capacity_kwh, range_km, charging_time_hours, motor_power_kw, weight_kg, status, created_at)
VALUES
--VF 3, VF 5 Plus, VF 6, VF 7, VF 8, VF 9, và VF Wild
('VinFast', 'VF 3', '3190x1675x1600', '2024', 4, 18.6, 210.0, 0.5, 35.0, 1150.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF 5 Plus', '3965x1720x1575', '2023', 5, 37.2, 300.0, 1.0, 100.0, 1250.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF 6', '4240x1820x1620', '2023', 5, 59.6, 400.0, 1.2, 150.0, 1500.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF 7', '4545x1895x1640', '2024', 5, 75.3, 450.0, 1.5, 200.0, 1750.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF 8', '4750x1900x1660', '2023', 5, 90.0, 550.0, 2.0, 300.0, 2200.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF 9', '5118x2254x1696', '2023', 7, 123.0, 675.0, 2.5, 300.0, 2600.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF Wild', '5310x1995x1890', '2025', 5, 120.0, 600.0, 2.0, 300.0, 2400.0, 'INACTIVE', GETDATE());

-- ===================================================================
-- BẢNG 3: USERS - Danh sách người dùng (mật khẩu cho tất cả là: "string")
-- ===================================================================
IF NOT EXISTS (SELECT 1 FROM users WHERE email_address = 'admin@example.com')
INSERT INTO users (full_name, email_address, phone_number, hashed_password, role_id, status, created_at, update_at)
VALUES
('Nguyen Van A', 'admin@example.com', '0901234567', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 1, 'ACTIVE', GETDATE(), GETDATE()),
('Tran Thi B', 'staff@example.com', '0902345678', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', GETDATE(), GETDATE()),
('Le Van C', 'technician@example.com', '0903456789', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', GETDATE(), GETDATE()),
('Pham Thi D', 'customer@example.com', '0904567890', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE()),
('Nguyen Van E', 'user@example.com', '0905678901','$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE()),
('Dao Thi F', 'ban@example.com', '0905678902','$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ARCHIVED', GETDATE(), GETDATE()),
-- ==================================================================
-- Bổ sung account
-- Staff với các trạng thái khác nhau
('Tran Thi Ac', 'staffactive@example.com', '0902222221', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', GETDATE(), GETDATE()),
('Tran Thi In', 'staffinactive@example.com', '0902222222', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'INACTIVE', GETDATE(), GETDATE()),
('Tran Thi Ar', 'staffarchived@example.com', '0902222223', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ARCHIVED', GETDATE(), GETDATE()),
-- Technician với các trạng thái khác nhau
('Le Van Ac', 'techactive@example.com', '0903333331', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', GETDATE(), GETDATE()),
('Le Van In', 'techinactive@example.com', '0903333332', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'INACTIVE', GETDATE(), GETDATE()),
('Le Van Ar', 'techarchived@example.com', '0903333333', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ARCHIVED', GETDATE(), GETDATE()),
-- CUSTOMER với các trạng thái khác nhau
('Pham Thi Ac', 'customer_active@example.com', '0904444441', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE()),
('Pham Thi In', 'customer_inactive@example.com', '0904444442', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'INACTIVE', GETDATE(), GETDATE()),
('Pham Thi Ar', 'customer_archived@example.com', '0904444443', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ARCHIVED', GETDATE(), GETDATE());
-- ===================================================================
-- BẢNG 4: VEHICLES - Xe của từng khách hàng
-- ===================================================================
IF NOT EXISTS (SELECT 1 FROM vehicles WHERE plate_number = '51H-12345')
INSERT INTO vehicles
(plate_number, color, vin, customer_id, vehicle_model_id, entity_status, created_at, purchased_at)
VALUES
('51H-12345', 'Red', '1HGCM82633A123456', 4, 1, 'ACTIVE', GETDATE(), GETDATE()),
('51H-67890', 'Blue', '1HGCM82633A654321', 4, 2, 'ACTIVE', GETDATE(), GETDATE()),
('60A-22222', 'White', '1HGCM82633A888888', 4, 3, 'INACTIVE', GETDATE(), GETDATE()),
('51G-55555', 'Black', '5YJSA1E26HF123001', 5, 1, 'ACTIVE', GETDATE(), GETDATE()),
('59A-66666', 'Silver', '5YJSA1E26HF123002', 5, 2, 'ACTIVE', GETDATE(), GETDATE()),
('51C-77777', 'White', '5YJSA1E26HF123003', 5, 3, 'INACTIVE', GETDATE(), GETDATE()),
-- bổ sung thêm xe cho các khách hàng khác
('51H-22145', 'Red', '1HGCM82633A100001', 13, 1, 'ACTIVE', GETDATE(), GETDATE()),
('51H-99810', 'Blue', '1HGCM82633A100002', 13, 2, 'INACTIVE', GETDATE(), GETDATE()),
('60A-12222', 'White', '1HGCM82633A200001', 14, 3, 'ACTIVE', GETDATE(), GETDATE()),
('51G-55435', 'Black', '1HGCM82633A200002', 14, 1, 'INACTIVE', GETDATE(), GETDATE()),
('59A-62211', 'Silver', '1HGCM82633A300001', 15, 2, 'ACTIVE', GETDATE(), GETDATE()),
('51C-12477', 'White', '1HGCM82633A300002', 15, 3, 'INACTIVE', GETDATE(), GETDATE());

-- ===================================================================
-- BẢNG 5: MAINTENANCE_CATALOGS - Các dịch vụ bảo dưỡng có sẵn
-- ===================================================================
INSERT INTO maintenance_catalogs (name, maintenance_service_type, description, status, created_at)
VALUES
    ('High-Voltage Battery Check', 'BATTERY_INSPECTION', 'Complete battery health check and diagnostics', 'ACTIVE', GETDATE()),-- đã chỉnh sửa lại tên dịch vụ
    ('Tire Rotation', 'TIRE_ROTATION', 'Rotate all four tires for even wear', 'ACTIVE', GETDATE()),
    ('Brake System Check', 'BRAKE_SYSTEM_CHECK', 'Inspect brake pads, rotors, and fluid levels', 'ACTIVE', GETDATE()),
    ('Firmware & System Update', 'SOFTWARE_UPDATE', 'Update vehicle firmware and system software', 'ACTIVE', GETDATE()),-- đã chỉnh sửa lại tên dịch vụ
    ('Air Filter Replacement', 'AIR_FILTER_REPLACEMENT', 'Replace cabin air filter for better air quality', 'ACTIVE', GETDATE()),
    ('Battery Cooling System Service', 'COOLANT_SYSTEM_SERVICE', 'Check and refill coolant for battery temperature management', 'ACTIVE', GETDATE()),-- đã chỉnh sửa lại tên dịch vụ
    ('Suspension Inspection', 'SUSPENSION_INSPECTION', 'Inspect shocks, struts, and suspension components', 'ACTIVE', GETDATE()),
    ('Wheel Alignment', 'WHEEL_ALIGNMENT', 'Adjust wheel alignment for optimal handling', 'ACTIVE', GETDATE()),
    ('Comprehensive Vehicle Diagnostic', 'FULL_VEHICLE_DIAGNOSTIC', 'Comprehensive system diagnostic scan', 'ACTIVE', GETDATE()),-- đã chỉnh sửa lại tên dịch vụ
    ('Emergency Mobile Charging', 'EMERGENCY_CHARGING_SERVICE', 'On-site battery charging service', 'INACTIVE', GETDATE());-- dịch vụ tạm thời ngưng cung cấp
-- ===================================================================
-- ===================================================================
-- BẢNG 6: PERMISSIONS - Định nghĩa tất cả các quyền trong hệ thống
-- ===================================================================
INSERT INTO permissions (resource, action, is_active, description) VALUES
-- SYSTEM
('SYSTEM', 'bypass_ownership', 1, 'Bypass ownership checks for all resources'),
-- VEHICLE
('VEHICLE', 'read', 1, 'Read vehicles'),
('VEHICLE', 'create', 1, 'Create vehicles'),
('VEHICLE', 'update', 1, 'Update vehicles'),
('VEHICLE', 'delete', 1, 'Delete vehicles'),
-- VEHICLE_MODEL
('VEHICLE_MODEL', 'read', 1, 'Read vehicle models'),
('VEHICLE_MODEL', 'create', 1, 'Create vehicle models'),
('VEHICLE_MODEL', 'update', 1, 'Update vehicle models'),
('VEHICLE_MODEL', 'delete', 1, 'Delete vehicle models'),
-- MAINTENANCE_SERVICE
('MAINTENANCE_SERVICE', 'read', 1, 'Read maintenance services'),
('MAINTENANCE_SERVICE', 'create', 1, 'Create maintenance services'),
('MAINTENANCE_SERVICE', 'update', 1, 'Update maintenance services'),
('MAINTENANCE_SERVICE', 'delete', 1, 'Delete maintenance services'),
-- BOOKING
('BOOKING', 'create', 1, 'Create booking'),
('BOOKING', 'read', 1, 'Read booking'),
('BOOKING', 'update', 1, 'Update booking'),
('BOOKING', 'cancel', 1, 'Cancel booking'),
-- MAINTENANCE (✅ BỔ SUNG)
('MAINTENANCE', 'start-inspection', 1, 'Start inspection'),
('MAINTENANCE', 'request-approval', 1, 'Request approval'),
('MAINTENANCE', 'approve', 1, 'Approve maintenance'),
('MAINTENANCE', 'reject', 1, 'Reject maintenance'),
('MAINTENANCE', 'start-repair', 1, 'Start repair'),
('MAINTENANCE', 'complete', 1, 'Complete maintenance'),
('MAINTENANCE', 'abort', 1, 'Abort maintenance'),
-- PAYMENT (✅ BỔ SUNG)
('PAYMENT', 'authorize', 1, 'Authorize payment'),
('PAYMENT', 'pay', 1, 'Pay booking'),
('PAYMENT', 'refund', 1, 'Refund payment'),
('PAYMENT', 'void', 1, 'Void payment'),
('PAYMENT', 'cancel', 1, 'Cancel payment'),
-- PART (Quản lý kho linh kiện)
('PART', 'read', 1, 'Read parts inventory'),
('PART', 'create', 1, 'Create new parts'),
('PART', 'update', 1, 'Update part information'),
('PART', 'delete', 1, 'Delete parts from inventory'),
('PART', 'manage_stock', 1, 'Manage part stock levels (increase/decrease)'),
('PART', 'view_low_stock', 1, 'View low stock alerts'),
-- USER PROFILE MANAGEMENT
('USER', 'read', 1, 'Read user info'),
('USER', 'create', 1, 'Create user'),
('USER', 'update', 1, 'Update user'),
('USER', 'disable', 1, 'Disable user (set INACTIVE)'),
('USER', 'reactive', 1, 'Reactive user (set ACTIVE)'),
('USER', 'delete', 1, 'Delete user');

-- ===================================================================
-- BẢNG 7: ROLE_PERMISSIONS - Phân quyền cho từng vai trò
-- ===================================================================

-- 1) ADMIN: Có tất cả các quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, p.id FROM permissions p

-- 2) STAFF: Có hầu hết các quyền vận hành
INSERT INTO role_permissions (role_id, permission_id)

SELECT 2, p.id FROM permissions p
WHERE
    -- Quyền bỏ qua check ownership
     (p.resource = 'SYSTEM' AND p.action = 'bypass_ownership') OR
    -- Toàn quyền trên các miền nghiệp vụ chính
     (p.resource IN (
         'USER',
         'VEHICLE',
         'VEHICLE_MODEL',
         'BOOKING',
         'PAYMENT',
         'MAINTENANCE',
         'MAINTENANCE_SERVICE',
         'PART'
     ));

-- 3) TECHNICIAN: Quyền liên quan đến kỹ thuật
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, p.id FROM permissions p
WHERE
   -- Chỉ đọc thông tin chung
    (p.resource IN ('VEHICLE', 'VEHICLE_MODEL', 'MAINTENANCE_SERVICE', 'BOOKING') AND p.action = 'read') OR
   -- Các hành động kỹ thuật trên booking
    (p.resource = 'MAINTENANCE' AND p.action IN ('start-inspection', 'request-approval', 'start-repair', 'complete')) OR
   -- Chỉ đọc và xem cảnh báo hết phụ tùng
    (p.resource = 'PART' AND p.action IN ('read', 'view_low_stock')) OR
    (p.resource = 'USER' AND p.action IN ('read', 'update'));

-- 4) CUSTOMER: Quyền của khách hàng
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, p.id FROM permissions p
WHERE
   -- Quản lý xe của mình
    p.resource = 'VEHICLE' OR
   -- Đọc thông tin chung
    (p.resource IN ('VEHICLE_MODEL', 'MAINTENANCE_SERVICE') AND p.action = 'read') OR
   -- Các hành động trên booking
    (p.resource = 'BOOKING' AND p.action IN ('create', 'read', 'cancel')) OR
    (p.resource = 'MAINTENANCE' AND p.action = 'approve') OR -- Phê duyệt báo giá
    (p.resource = 'PAYMENT' AND p.action = 'pay') OR
    (p.resource = 'USER' AND p.action IN ('read', 'update', 'disable'));

-- ===================================================================
-- BẢNG 8: BOOKINGS - Lịch sử đặt hẹn
-- ===================================================================
-- Bookings cho Customer 4 (ID=4)
INSERT INTO bookings (customer_id, vin, schedule_date, booking_status, payment_status, total_price, created_at, updated_at)
VALUES
-- Các booking ở trạng thái schedule phase
(4, '1HGCM82633A123456', DATEADD(DAY, 3, GETDATE()), 'PENDING', 'UNPAID', 350000, GETDATE(), GETDATE()),
(4, '1HGCM82633A123456', DATEADD(DAY, 2, GETDATE()), 'CONFIRMED', 'UNPAID', 360000, GETDATE(), GETDATE()),
(4, '1HGCM82633A123456', DATEADD(DAY, 5, GETDATE()), 'RESCHEDULED', 'UNPAID', 365000, GETDATE(), GETDATE()),

-- Các booking ở trạng thái maintenance phase
(4, '1HGCM82633A123456', GETDATE(), 'IN_PROGRESS', 'UNPAID', 370000, GETDATE(), GETDATE()),
(4, '1HGCM82633A123456', DATEADD(DAY, -1, GETDATE()), 'IN_PROGRESS', 'PAID', 375000, GETDATE(), GETDATE()),
(4, '1HGCM82633A123456', DATEADD(DAY, -2, GETDATE()), 'MAINTENANCE_COMPLETE', 'PAID', 380000, GETDATE(), GETDATE()),

-- Các booking đã hoàn thành
(4, '1HGCM82633A123456', DATEADD(DAY, -3, GETDATE()), 'DELIVERED', 'PAID', 385000, GETDATE(), GETDATE()),
(4, '1HGCM82633A123456', DATEADD(DAY, -7, GETDATE()), 'DELIVERED', 'PAID', 390000, GETDATE(), GETDATE()),

-- Booking đã hủy
(4, '1HGCM82633A123456', DATEADD(DAY, -8, GETDATE()), 'CANCELLED', 'UNPAID', 200000, GETDATE(), GETDATE());

-- Bookings cho Customer 5 (ID=5)
INSERT INTO bookings (customer_id, vin, schedule_date, booking_status, payment_status, total_price, created_at, updated_at)
VALUES
-- Schedule phase
(5, '5YJSA1E26HF123001', DATEADD(DAY, 3, GETDATE()), 'PENDING', 'UNPAID', 550000, GETDATE(), GETDATE()),
(5, '5YJSA1E26HF123001', DATEADD(DAY, 2, GETDATE()), 'CONFIRMED', 'UNPAID', 560000, GETDATE(), GETDATE()),
(5, '5YJSA1E26HF123001', DATEADD(DAY, 4, GETDATE()), 'RESCHEDULED', 'UNPAID', 565000, GETDATE(), GETDATE()),

-- Maintenance phase
(5, '5YJSA1E26HF123001', GETDATE(), 'IN_PROGRESS', 'UNPAID', 570000, GETDATE(), GETDATE()),
(5, '5YJSA1E26HF123001', DATEADD(DAY, -1, GETDATE()), 'IN_PROGRESS', 'PAID', 575000, GETDATE(), GETDATE()),
(5, '5YJSA1E26HF123001', DATEADD(DAY, -2, GETDATE()), 'MAINTENANCE_COMPLETE', 'PAID', 580000, GETDATE(), GETDATE()),

-- Completed
(5, '5YJSA1E26HF123001', DATEADD(DAY, -3, GETDATE()), 'DELIVERED', 'PAID', 585000, GETDATE(), GETDATE()),
(5, '5YJSA1E26HF123001', DATEADD(DAY, -7, GETDATE()), 'DELIVERED', 'PAID', 590000, GETDATE(), GETDATE()),

-- Cancelled
(5, '5YJSA1E26HF123001', DATEADD(DAY, -8, GETDATE()), 'CANCELLED', 'UNPAID', 220000, GETDATE(), GETDATE());


-- ===================================================================
-- BẢNG 9: BOOKING_DETAILS - Chi tiết các dịch vụ trong mỗi booking
-- ===================================================================
-- Details cho Bookings 1-9 (Customer 4)
INSERT INTO booking_details (booking_id, maintenance_catalog_id, description, service_price) VALUES
(1, 1, 'Battery inspection needed', 150000),(1, 2, 'Tire rotation check', 80000),
(2, 3, 'Brake pads replacement', 200000),(2, 4, 'Software system update', 300000),
(3, 5, 'Air filter replacement', 100000),(3, 6, 'Coolant system refill', 180000),
(4, 7, 'Suspension inspection', 250000),(4, 8, 'Wheel alignment', 220000),
(5, 9, 'Full vehicle diagnostic', 400000),(5, 1, 'Battery health check', 150000),
(6, 2, 'Tire rotation performed', 80000),(6, 3, 'Brake system checked', 200000),
(7, 4, 'Software issue fixed', 300000),(7, 5, 'Cabin filter replaced', 100000),
(8, 6, 'Coolant refill done', 180000),(8, 7, 'Suspension repaired', 250000),
(9, 8, 'Wheel alignment corrected', 220000),(9, 9, 'Diagnostic scan performed', 400000);

-- Details cho Bookings 10-18 (Customer 5)
INSERT INTO booking_details (booking_id, maintenance_catalog_id, description, service_price) VALUES
(10, 1, 'Battery check done', 150000),(10, 2, 'Tire rotation scheduled', 80000),
(11, 3, 'Brake fluid changed', 200000),(11, 4, 'Software feature upgraded', 300000),
(12, 5, 'Air filter cleaned', 100000),(12, 6, 'Coolant leak fixed', 180000),
(13, 7, 'Suspension tuning', 250000),(13, 8, 'Wheel balance check', 220000),
(14, 9, 'Diagnostic before trip', 400000),(14, 1, 'Battery terminal cleaning', 150000),
(15, 2, 'Summer tire change', 80000),(15, 3, 'Brake system updated', 200000),
(16, 4, 'Software reinstallation', 300000),(16, 5, 'Cabin filter refreshed', 100000),
(17, 6, 'Coolant replaced', 180000),(17, 7, 'Suspension component changed', 250000),
(18, 8, 'Wheel tracking checked', 220000),(18, 9, 'Vehicle system scanned', 400000);

-- ===================================================================
-- BẢNG 10: PARTS - Kho linh kiện phụ tùng
-- ===================================================================
-- Parts liên quan đến Pin (Battery)
INSERT INTO parts (name, part_number, manufacturer, description, current_unit_price, quantity, status, created_at)
VALUES

    ('Battery Cell Module - Type A', 1001, 'Tesla', 'High-capacity lithium-ion battery cell module for Model S', 15000000, 25, 'ACTIVE', GETDATE()),
    ('Battery Cooling System', 1002, 'Tesla', 'Liquid cooling system for battery temperature management', 8500000, 15, 'ACTIVE', GETDATE()),
    ('Battery Management System (BMS)', 1003, 'Panasonic', 'Electronic battery monitoring and control unit', 12000000, 10, 'ACTIVE', GETDATE()),
    ('Battery Terminal Connector', 1004, 'Generic', 'High-voltage battery terminal connector kit', 450000, 50, 'ACTIVE', GETDATE()),

-- Parts liên quan đến Lốp (Tire)
    ('EV Performance Tire - 19 inch', 2001, 'Michelin', 'Low rolling resistance tire for electric vehicles', 3500000, 40, 'ACTIVE', GETDATE()),
    ('EV Performance Tire - 21 inch', 2002, 'Michelin', 'Premium performance tire for Tesla vehicles', 4200000, 30, 'ACTIVE', GETDATE()),
    ('Tire Pressure Sensor (TPMS)', 2003, 'Continental', 'Tire pressure monitoring sensor', 850000, 60, 'ACTIVE', GETDATE()),
    ('Wheel Balance Weight Set', 2004, 'Generic', 'Adhesive wheel balancing weights', 120000, 100, 'ACTIVE', GETDATE()),

-- Parts liên quan đến Phanh (Brake)
    ('Brake Pad Set - Front', 3001, 'Brembo', 'High-performance ceramic brake pads for front axle', 2800000, 35, 'ACTIVE', GETDATE()),
    ('Brake Pad Set - Rear', 3002, 'Brembo', 'High-performance ceramic brake pads for rear axle', 2500000, 35, 'ACTIVE', GETDATE()),
    ('Brake Rotor - Front', 3003, 'Brembo', 'Ventilated brake disc rotor for front wheel', 3200000, 20, 'ACTIVE', GETDATE()),
    ('Brake Fluid DOT 4', 3004, 'Bosch', 'Premium brake fluid for EV braking systems', 180000, 80, 'ACTIVE', GETDATE()),
    ('Brake Caliper Repair Kit', 3005, 'ATE', 'Complete caliper seal and piston repair kit', 950000, 25, 'ACTIVE', GETDATE()),

-- Parts liên quan đến Hệ thống làm mát (Coolant)
    ('Coolant Fluid - 5L', 4001, 'Motul', 'Specialized coolant for EV battery thermal management', 650000, 70, 'ACTIVE', GETDATE()),
    ('Coolant Pump', 4002, 'Bosch', 'Electric coolant circulation pump', 4500000, 12, 'ACTIVE', GETDATE()),
    ('Coolant Hose Kit', 4003, 'Gates', 'High-temperature resistant coolant hose set', 1200000, 30, 'ACTIVE', GETDATE()),
    ('Radiator - Battery Cooling', 4004, 'Denso', 'Aluminum radiator for battery cooling system', 6800000, 8, 'ACTIVE', GETDATE()),

-- Parts liên quan đến Lọc gió (Air Filter)
    ('Cabin Air Filter - HEPA', 5001, 'Mann Filter', 'High-efficiency particulate air filter for cabin', 550000, 60, 'ACTIVE', GETDATE()),
    ('Cabin Air Filter - Activated Carbon', 5002, 'Bosch', 'Carbon filter for odor and pollution removal', 680000, 45, 'ACTIVE', GETDATE()),

-- Parts liên quan đến Hệ thống treo (Suspension)
    ('Front Shock Absorber', 6001, 'Bilstein', 'Gas-filled shock absorber for front suspension', 5500000, 18, 'ACTIVE', GETDATE()),
    ('Rear Shock Absorber', 6002, 'Bilstein', 'Gas-filled shock absorber for rear suspension', 5200000, 18, 'ACTIVE', GETDATE()),
    ('Control Arm Bushing Kit', 6003, 'Lemforder', 'Polyurethane control arm bushing set', 1800000, 25, 'ACTIVE', GETDATE()),
    ('Stabilizer Bar Link', 6004, 'TRW', 'Heavy-duty stabilizer bar end link', 650000, 40, 'ACTIVE', GETDATE()),
    ('Coil Spring - Front', 6005, 'Eibach', 'Progressive rate coil spring for front suspension', 3200000, 15, 'ACTIVE', GETDATE()),

-- Parts liên quan đến Điện tử (Software/Electronics)
--     ('Diagnostic OBD-II Module', 7001, 'Bosch', 'On-board diagnostics module for system scanning', 2800000, 20, 'ACTIVE', GETDATE()),
--     ('Vehicle Control Unit (VCU)', 7002, 'Tesla', 'Main vehicle electronic control unit', 18000000, 5, 'ACTIVE', GETDATE()),
--     ('Touchscreen Display - 17 inch', 7003, 'LG', 'Replacement center console touchscreen', 22000000, 8, 'ACTIVE', GETDATE()),

-- Parts ít dùng / hết hàng
--     ('Emergency Brake Cable', 8001, 'Generic', 'Backup mechanical brake cable', 380000, 5, 'ACTIVE', GETDATE()),
--     ('Wiper Blade Set', 8002, 'Bosch', 'Front windshield wiper blade pair', 420000, 3, 'ACTIVE', GETDATE()),
--     ('Door Handle Assembly', 8003, 'Tesla', 'Electronic pop-out door handle mechanism', 6500000, 2, 'ACTIVE', GETDATE()),
--     ('Charge Port Cover', 8004, 'Tesla', 'Motorized charging port door assembly', 3800000, 1, 'INACTIVE', GETDATE()),
-- ===================================================================
-- Bổ sung dữ lieu Part
-- 1️ VF 3
    ('VF3 Battery Pack - LFP 37kWh', 3001, 'VinES', 'Lithium Iron Phosphate (LFP) battery pack for VF 3', 320000000, 8, 'ACTIVE', GETDATE()),
    ('VF3 Tire 15-inch EcoGrip', 3002, 'Bridgestone', '15-inch high-efficiency tire for VF 3', 2500000, 40, 'ACTIVE', GETDATE()),
    ('VF3 Front Brake Disc', 3003, 'VinFast', 'Front brake disc, OE standard', 1500000, 20, 'ACTIVE', GETDATE()),
    ('VF3 Coolant Pump Assembly', 3004, 'VinFast', 'Coolant pump for battery thermal system', 3800000, 10, 'ACTIVE', GETDATE()),
    ('VF3 Air Filter Cartridge', 3005, 'VinFast', 'Engine air intake filter cartridge', 600000, 25, 'ACTIVE', GETDATE()),
    ('VF3 Suspension Arm Set', 3006, 'VinFast', 'Front suspension arm set', 2700000, 15, 'ACTIVE', GETDATE()),
    ('VF3 Battery ECU Unit', 3007, 'VinFast', 'Battery electronic control unit (ECU) for VF 3', 9500000, 6, 'ACTIVE', GETDATE()),
    ('VF3 Door Handle Sensor (Old)', 3008, 'VinFast', 'Old version door handle sensor — no longer produced', 1200000, 0, 'INACTIVE', GETDATE()),

-- ===================================================================
-- 2 VF 5 Plus
    ('VF5 Battery Module - NMC 45kWh', 3101, 'VinES', 'Nickel Manganese Cobalt (NMC) 45kWh battery module for VF 5 Plus', 350000000, 6, 'ACTIVE', GETDATE()),
    ('VF5 Tire 16-inch AllSeason', 3102, 'Michelin', '16-inch all-season tire for VF 5 Plus', 3200000, 30, 'ACTIVE', GETDATE()),
    ('VF5 Rear Brake Caliper', 3103, 'VinFast', 'Rear brake caliper assembly', 2500000, 12, 'ACTIVE', GETDATE()),
    ('VF5 Coolant Radiator', 3104, 'VinFast', 'Cooling radiator for electric motor system', 4100000, 8, 'ACTIVE', GETDATE()),
    ('VF5 Air Intake Filter', 3105, 'VinFast', 'Cabin air intake filter', 850000, 18, 'ACTIVE', GETDATE()),
    ('VF5 Front Shock Absorber', 3106, 'VinFast', 'Front shock absorber set for VF 5 Plus', 3100000, 10, 'ACTIVE', GETDATE()),
    ('VF5 Infotainment Unit V-Connect', 3107, 'VinFast', 'Central infotainment and control unit (V-Connect)', 19000000, 4, 'ACTIVE', GETDATE()),
    ('VF5 Old Tire Sensor (Deprecated)', 3108, 'VinFast', 'Legacy tire pressure sensor — no longer sold', 950000, 0, 'INACTIVE', GETDATE()),

-- ===================================================================
-- 3️ VF 6
    ('VF6 Battery Pack - 59kWh', 3201, 'VinES', 'High-capacity 59kWh battery pack for VF 6', 410000000, 5, 'ACTIVE', GETDATE()),
    ('VF6 Tire 17-inch RoadMax', 3202, 'Pirelli', '17-inch performance tire for VF 6', 4200000, 24, 'ACTIVE', GETDATE()),
    ('VF6 Brake Pad Kit', 3203, 'VinFast', 'Front/rear brake pad kit', 1300000, 25, 'ACTIVE', GETDATE()),
    ('VF6 Battery Coolant Hose', 3204, 'VinFast', 'Battery coolant circulation hose', 950000, 14, 'ACTIVE', GETDATE()),
    ('VF6 Cabin Air Filter', 3205, 'VinFast', 'Cabin air filter for interior ventilation', 700000, 20, 'ACTIVE', GETDATE()),
    ('VF6 Rear Suspension Spring', 3206, 'VinFast', 'Rear suspension coil spring', 2800000, 8, 'ACTIVE', GETDATE()),
    ('VF6 ADAS Main Control Unit', 3207, 'VinFast', 'Main processing unit for ADAS driver-assist system', 31000000, 3, 'ACTIVE', GETDATE()),
    ('VF6 Mirror Adjustment Motor', 3208, 'VinFast', 'Mirror adjustment motor — old model', 650000, 1, 'INACTIVE', GETDATE()),

-- ===================================================================
-- 4️ VF 7
    ('VF7 Battery Pack - 75kWh', 3301, 'VinES', '75kWh high-performance battery pack for VF 7', 520000000, 4, 'ACTIVE', GETDATE()),
    ('VF7 Tire 18-inch UltraGrip', 3302, 'Goodyear', '18-inch UltraGrip SUV tire for VF 7', 4800000, 20, 'ACTIVE', GETDATE()),
    ('VF7 Front Brake Disc Plus', 3303, 'VinFast', 'Enhanced alloy front brake disc', 2200000, 10, 'ACTIVE', GETDATE()),
    ('VF7 Coolant Expansion Tank', 3304, 'VinFast', 'Coolant expansion reservoir tank', 1400000, 12, 'ACTIVE', GETDATE()),
    ('VF7 Engine Air Filter XL', 3305, 'VinFast', 'High-efficiency engine air filter', 950000, 20, 'ACTIVE', GETDATE()),
    ('VF7 Rear Suspension Arm', 3306, 'VinFast', 'Rear suspension arm assembly', 3300000, 6, 'ACTIVE', GETDATE()),
    ('VF7 Digital Dashboard Module', 3307, 'VinFast', '12-inch digital dashboard display module', 25000000, 5, 'ACTIVE', GETDATE()),
    ('VF7 Door Trim Set (Discontinued)', 3308, 'VinFast', 'Interior door trim set — old model', 1200000, 0, 'INACTIVE', GETDATE()),

-- ===================================================================
-- 5️ VF 8
    ('VF8 Battery Pack - 87.7kWh', 3401, 'VinES', 'Premium 87.7kWh battery pack for VF 8', 600000000, 3, 'ACTIVE', GETDATE()),
    ('VF8 Tire 19-inch TouringPro', 3402, 'Michelin', '19-inch premium TouringPro tire for VF 8', 5200000, 18, 'ACTIVE', GETDATE()),
    ('VF8 Performance Brake Set', 3403, 'VinFast', 'High-performance brake set', 3600000, 10, 'ACTIVE', GETDATE()),
    ('VF8 Coolant Radiator Assembly', 3404, 'VinFast', 'Complete cooling radiator assembly', 4200000, 8, 'ACTIVE', GETDATE()),
    ('VF8 Cabin Air Purifier Filter', 3405, 'VinFast', 'HEPA air and dust purifier filter', 1100000, 12, 'ACTIVE', GETDATE()),
    ('VF8 Active Suspension Control', 3406, 'VinFast', 'Active suspension control module', 43000000, 2, 'ACTIVE', GETDATE()),
    ('VF8 Infotainment OS Chipset', 3407, 'VinFast', 'Vehicle infotainment OS processing chipset', 38000000, 3, 'ACTIVE', GETDATE()),
    ('VF8 Door Motor Gen1', 3408, 'VinFast', 'First-gen door motor', 2100000, 0, 'INACTIVE', GETDATE()),

-- ===================================================================
-- 6️ VF 9
    ('VF9 Dual Battery Pack - 92kWh', 3501, 'VinES', 'Dual 92kWh high-performance battery pack for VF 9', 720000000, 2, 'ACTIVE', GETDATE()),
    ('VF9 Tire 20-inch PowerGrip', 3502, 'Goodyear', '20-inch PowerGrip premium SUV tire', 6100000, 12, 'ACTIVE', GETDATE()),
    ('VF9 Electronic Brake System', 3503, 'VinFast', 'Electronic brake system with force balancing', 5300000, 6, 'ACTIVE', GETDATE()),
    ('VF9 High-Capacity Coolant Pump', 3504, 'VinFast', 'High-capacity coolant circulation pump', 4700000, 5, 'ACTIVE', GETDATE()),
    ('VF9 Air Intake Filter HEPA+', 3505, 'VinFast', 'HEPA+ fine dust intake air filter', 1600000, 8, 'ACTIVE', GETDATE()),
    ('VF9 Adaptive Suspension Module', 3506, 'VinFast', 'Adaptive suspension control module', 56000000, 2, 'ACTIVE', GETDATE()),
    ('VF9 Central Computing Unit', 3507, 'VinFast', 'Central computing and processing unit for VF 9', 95000000, 1, 'ACTIVE', GETDATE()),
    ('VF9 Steering ECU (Old Model)', 3508, 'VinFast', 'Legacy steering ECU — old version', 13000000, 0, 'INACTIVE', GETDATE()),

-- ===================================================================
-- 7️ VF Wild
    ('VF Wild Battery Pack - 85kWh Offroad', 3601, 'VinES', '85kWh off-road battery pack for VF Wild electric pickup', 640000000, 2, 'ACTIVE', GETDATE()),
    ('VF Wild Tire 21-inch AllTerrain', 3602, 'Continental', '21-inch all-terrain off-road tire', 7200000, 8, 'ACTIVE', GETDATE()),
    ('VF Wild Brake Reinforced Kit', 3603, 'VinFast', 'Reinforced heavy-duty brake kit', 6800000, 5, 'ACTIVE', GETDATE()),
    ('VF Wild Coolant System Pro', 3604, 'VinFast', 'High-performance cooling system for off-road conditions', 5400000, 3, 'ACTIVE', GETDATE()),
    ('VF Wild Dust Filter Element', 3605, 'VinFast', 'Desert-grade dust air filter element', 1800000, 6, 'ACTIVE', GETDATE()),
    ('VF Wild Heavy-Duty Suspension', 3606, 'VinFast', 'Heavy-duty off-road suspension assembly', 62000000, 2, 'ACTIVE', GETDATE()),
    ('VF Wild Navigation & Sensor Suite', 3607, 'VinFast', 'Advanced navigation and terrain sensor suite', 45000000, 2, 'ACTIVE', GETDATE()),
    ('VF Wild Old Headlight Unit', 3608, 'VinFast', 'Legacy headlight unit — no longer in production', 3900000, 0, 'INACTIVE', GETDATE());
-- ===================================================================
-- ===================================================================
-- BỔ SUNG SEED DATA CHO MAINTENANCE CATALOG DOMAIN
-- Bao gồm: Catalog, CatalogModel, CatalogModelPart
-- ===================================================================

-- ===================================================================
-- BẢNG: PARTS (Đã có sẵn từ line 200+ trong data.sql)
-- Tổng: 31 parts đã được seed
-- ===================================================================

-- ===================================================================
-- BẢNG 11: MAINTENANCE_CATALOGS_MODELS
-- Mapping giữa Catalog và Vehicle Model (xe nào dùng được dịch vụ gì)
-- ===================================================================

-- Catalog 1: Battery Inspection (Kiểm tra pin)
-- Tesla Model S (id=1): Xe điện cao cấp, cần kiểm tra pin thường xuyên
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (1, 1, 60.0, 150000, 'Tesla Model S - Standard battery inspection', GETDATE()),
    (1, 2, 75.0, 180000, 'VinFast VF8 - Requires Vietnamese-spec battery check tools', GETDATE());

-- Catalog 2: Tire Rotation (Xoay vị trí lốp)
-- Áp dụng cho tất cả xe điện
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (2, 1, 30.0, 80000, 'Tesla Model S - 19 inch performance tires', GETDATE()),
    (2, 2, 35.0, 90000, 'VinFast VF8 - Larger 20 inch wheels', GETDATE()),
    (2, 3, 30.0, 75000, 'Toyota Corolla Cross EV - Standard 18 inch tires', GETDATE());

-- Catalog 3: Brake System Check (Kiểm tra hệ thống phanh)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (3, 1, 90.0, 200000, 'Tesla Model S - High-performance Brembo brake system', GETDATE()),
    (3, 2, 95.0, 220000, 'VinFast VF8 - Regenerative + mechanical brake check', GETDATE()),
    (3, 3, 85.0, 180000, 'Toyota Corolla Cross EV - Standard brake system', GETDATE());

-- Catalog 4: Software Update (Cập nhật phần mềm)
-- Chỉ Tesla và VinFast hỗ trợ OTA update
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (4, 1, 120.0, 300000, 'Tesla Model S - Full OTA firmware update capability', GETDATE()),
    (4, 2, 150.0, 350000, 'VinFast VF8 - Requires VinFast diagnostic tool', GETDATE());

-- Catalog 5: Air Filter Replacement (Thay lọc gió)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (5, 1, 30.0, 100000, 'Tesla Model S - HEPA filtration system', GETDATE()),
    (5, 2, 35.0, 110000, 'VinFast VF8 - Activated carbon filter', GETDATE()),
    (5, 3, 30.0, 95000, 'Toyota Corolla Cross EV - Standard cabin filter', GETDATE());

-- Catalog 6: Coolant System Service (Bảo dưỡng hệ thống làm mát)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (6, 1, 60.0, 180000, 'Tesla Model S - Battery thermal management system', GETDATE()),
    (6, 2, 70.0, 200000, 'VinFast VF8 - Dual cooling system (battery + motor)', GETDATE()),
    (6, 3, 55.0, 160000, 'Toyota Corolla Cross EV - Simplified cooling system', GETDATE());

-- Catalog 7: Suspension Inspection (Kiểm tra hệ thống treo)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (7, 1, 90.0, 250000, 'Tesla Model S - Air suspension system check', GETDATE()),
    (7, 2, 85.0, 240000, 'VinFast VF8 - Standard suspension with adaptive damping', GETDATE()),
    (7, 3, 80.0, 220000, 'Toyota Corolla Cross EV - MacPherson strut suspension', GETDATE());

-- Catalog 8: Wheel Alignment (Cân chỉnh góc đặt bánh xe)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (8, 1, 60.0, 220000, 'Tesla Model S - High-precision alignment required', GETDATE()),
    (8, 2, 65.0, 230000, 'VinFast VF8 - SUV alignment specifications', GETDATE()),
    (8, 3, 55.0, 200000, 'Toyota Corolla Cross EV - Standard alignment', GETDATE());

-- Catalog 9: Full Vehicle Diagnostic (Chẩn đoán toàn bộ xe)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (9, 1, 150.0, 400000, 'Tesla Model S - Comprehensive system scan including autopilot', GETDATE()),
    (9, 2, 180.0, 450000, 'VinFast VF8 - Full diagnostic with VinFast proprietary tools', GETDATE()),
    (9, 3, 140.0, 380000, 'Toyota Corolla Cross EV - Standard EV diagnostic', GETDATE());
-- ===================================================================
-- bổ sung seed data cho bảng maintenance_catalogs_models_parts
-- Battery Inspection (Kiểm tra pin)
INSERT INTO maintenance_catalogs_models (maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at)
VALUES
    (1, 1, 45.0, 120000, 'VF3 - Standard LFP battery health check', GETDATE()),
    (1, 2, 55.0, 140000, 'VF5 Plus - NMC battery diagnostic', GETDATE()),
    (1, 3, 60.0, 160000, 'VF6 - Medium capacity 59kWh pack inspection', GETDATE()),
    (1, 4, 70.0, 180000, 'VF7 - 75kWh high capacity pack maintenance', GETDATE()),
    (1, 5, 80.0, 200000, 'VF8 - Advanced thermal battery management check', GETDATE()),
    (1, 6, 90.0, 230000, 'VF9 - Dual pack 92kWh battery diagnostic', GETDATE()),
    (1, 7, 100.0, 250000, 'VF Wild - Offroad battery module inspection (Inactive model)', GETDATE()),
-- Tire Rotation (trí lốp)
    (2, 1, 25.0, 70000, 'VF3 - 15-inch eco tires', GETDATE()),
    (2, 2, 30.0, 80000, 'VF5 Plus - 16-inch all-season tires', GETDATE()),
    (2, 3, 35.0, 90000, 'VF6 - 17-inch road tires', GETDATE()),
    (2, 4, 40.0, 100000, 'VF7 - 18-inch SUV tires', GETDATE()),
    (2, 5, 45.0, 110000, 'VF8 - 19-inch touring tires', GETDATE()),
    (2, 6, 50.0, 120000, 'VF9 - 20-inch performance tires', GETDATE()),
    (2, 7, 55.0, 130000, 'VF Wild - 21-inch all-terrain tires (Inactive model)', GETDATE()),
-- Brake System Check (Kiểm tra phanh)
    (3, 1, 50.0, 150000, 'VF3 - Front disc brake inspection', GETDATE()),
    (3, 2, 60.0, 170000, 'VF5 Plus - Rear caliper + pad wear check', GETDATE()),
    (3, 3, 65.0, 190000, 'VF6 - Regenerative + mechanical brake system', GETDATE()),
    (3, 4, 70.0, 210000, 'VF7 - Enhanced brake disc set check', GETDATE()),
    (3, 5, 75.0, 230000, 'VF8 - Performance brake module inspection', GETDATE()),
    (3, 6, 80.0, 250000, 'VF9 - Electronic brake balancing system', GETDATE()),
    (3, 7, 90.0, 260000, 'VF Wild - Reinforced brake kit inspection (Inactive model)', GETDATE()),
--  Software Update (Cập nhật phần mềm xe)
    (4, 1, 60.0, 120000, 'VF3 - Firmware + infotainment patch update', GETDATE()),
    (4, 2, 70.0, 150000, 'VF5 Plus - OTA software update', GETDATE()),
    (4, 3, 80.0, 170000, 'VF6 - System diagnostics + software upgrade', GETDATE()),
    (4, 4, 90.0, 200000, 'VF7 - ADAS + OS update', GETDATE()),
    (4, 5, 100.0, 230000, 'VF8 - Full vehicle system update', GETDATE()),
    (4, 6, 120.0, 250000, 'VF9 - Central computing firmware update', GETDATE()),
    (4, 7, 110.0, 240000, 'VF Wild - Navigation + sensor software patch (Inactive model)', GETDATE()),

--  Air Filter Replacement (Thay lọc gió)
    (5, 1, 25.0, 80000, 'VF3 - Engine air filter cartridge', GETDATE()),
    (5, 2, 30.0, 90000, 'VF5 Plus - Cabin air filter replacement', GETDATE()),
    (5, 3, 35.0, 95000, 'VF6 - Cabin HEPA filter replacement', GETDATE()),
    (5, 4, 40.0, 100000, 'VF7 - Premium air filter', GETDATE()),
    (5, 5, 45.0, 110000, 'VF8 - Air purifier + carbon filter', GETDATE()),
    (5, 6, 50.0, 120000, 'VF9 - Advanced HEPA+ filter', GETDATE()),
    (5, 7, 55.0, 125000, 'VF Wild - Dust-resistant offroad filter (Inactive model)', GETDATE()),

--  Coolant System Service (Bảo dưỡng hệ thống làm mát)
    (6, 1, 40.0, 100000, 'VF3 - Battery coolant refill', GETDATE()),
    (6, 2, 45.0, 120000, 'VF5 Plus - Dual coolant circuit', GETDATE()),
    (6, 3, 50.0, 140000, 'VF6 - Thermal management maintenance', GETDATE()),
    (6, 4, 55.0, 160000, 'VF7 - High-efficiency cooling inspection', GETDATE()),
    (6, 5, 60.0, 180000, 'VF8 - Dual system (battery + motor) check', GETDATE()),
    (6, 6, 70.0, 200000, 'VF9 - High-capacity coolant pump service', GETDATE()),
    (6, 7, 75.0, 210000, 'VF Wild - Offroad cooling system check (Inactive model)', GETDATE()),

-- Suspension Inspection (Kiểm tra hệ thống treo)
    (7, 1, 50.0, 130000, 'VF3 - Basic suspension arm check', GETDATE()),
    (7, 2, 55.0, 150000, 'VF5 Plus - Shock absorber inspection', GETDATE()),
    (7, 3, 60.0, 170000, 'VF6 - Rear suspension spring check', GETDATE()),
    (7, 4, 70.0, 190000, 'VF7 - Adaptive damping system check', GETDATE()),
    (7, 5, 80.0, 210000, 'VF8 - Active suspension inspection', GETDATE()),
    (7, 6, 90.0, 230000, 'VF9 - Adaptive suspension module check', GETDATE()),
    (7, 7, 95.0, 250000, 'VF Wild - Heavy-duty offroad suspension (Inactive model)', GETDATE()),

--  Wheel Alignment (Cân chỉnh bánh xe)
    (8, 1, 30.0, 90000, 'VF3 - Compact alignment setup', GETDATE()),
    (8, 2, 35.0, 100000, 'VF5 Plus - SUV alignment', GETDATE()),
    (8, 3, 40.0, 110000, 'VF6 - Precision alignment', GETDATE()),
    (8, 4, 45.0, 130000, 'VF7 - Sport alignment calibration', GETDATE()),
    (8, 5, 50.0, 150000, 'VF8 - High-performance alignment', GETDATE()),
    (8, 6, 55.0, 170000, 'VF9 - Large-size chassis calibration', GETDATE()),
    (8, 7, 60.0, 180000, 'VF Wild - Offroad angle alignment (Inactive model)', GETDATE());
-- ===================================================================
-- BẢNG 12: MAINTENANCE_CATALOGS_MODELS_PARTS
-- Linh kiện cần thiết cho từng combo (Catalog + Model)
-- ===================================================================

-- ==================== CATALOG 1: BATTERY INSPECTION ====================

-- Tesla Model S (catalog=1, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
-- Parts required for Tesla Model S battery inspection
(1, 1, 3, 1, 0, 'BMS diagnostic scan required'),           -- Battery Management System
(1, 1, 4, 2, 0, 'Terminal cleaning and inspection'),       -- Battery Terminal Connector
(1, 1, 2, 1, 1, 'Check coolant level (optional refill)');  -- Battery Cooling System

-- VinFast VF8 (catalog=1, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (1, 2, 3, 1, 0, 'BMS check for VinFast battery packs'),
    (1, 2, 4, 2, 0, 'High-voltage terminal inspection'),
    (1, 2, 1, 1, 1, 'Battery module health check (if needed)'); -- Battery Cell Module

-- ==================== CATALOG 2: TIRE ROTATION ====================

-- Tesla Model S (catalog=2, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (2, 1, 6, 4, 0, 'Check all 4 tires for 21-inch Model S wheels'), -- EV Performance Tire 21"
    (2, 1, 7, 4, 0, 'Verify TPMS sensors during rotation'),          -- Tire Pressure Sensor
    (2, 1, 8, 1, 0, 'Rebalance if necessary');                       -- Wheel Balance Weight Set

-- VinFast VF8 (catalog=2, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (2, 2, 5, 4, 0, 'Standard 19-inch tire set for VF8'),
    (2, 2, 7, 4, 0, 'TPMS check required'),
    (2, 2, 8, 1, 0, 'Balance weights as needed');

-- Toyota Corolla Cross EV (catalog=2, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (2, 3, 5, 4, 0, 'Standard 19-inch tire for compact EV'),
    (2, 3, 7, 4, 0, 'TPMS sensor validation'),
    (2, 3, 8, 1, 0, 'Wheel balancing weights');

-- ==================== CATALOG 3: BRAKE SYSTEM CHECK ====================

-- Tesla Model S (catalog=3, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (3, 1, 9, 1, 1, 'Front brake pads (replace if <3mm)'),          -- Brake Pad Front
    (3, 1, 10, 1, 1, 'Rear brake pads (replace if <3mm)'),          -- Brake Pad Rear
    (3, 1, 12, 1, 0, 'Brake fluid check and top-up'),               -- Brake Fluid DOT 4
    (3, 1, 13, 1, 1, 'Caliper service kit (if seized)');            -- Brake Caliper Repair Kit

-- VinFast VF8 (catalog=3, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (3, 2, 9, 1, 1, 'Front pads inspection'),
    (3, 2, 10, 1, 1, 'Rear pads inspection'),
    (3, 2, 11, 2, 1, 'Rotor replacement if warped (front pair)'),   -- Brake Rotor Front
    (3, 2, 12, 1, 0, 'Brake fluid flush (DOT 4 required)');

-- Toyota Corolla Cross EV (catalog=3, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (3, 3, 9, 1, 1, 'Front brake pad set'),
    (3, 3, 10, 1, 1, 'Rear brake pad set'),
    (3, 3, 12, 1, 0, 'Brake fluid check'),
    (3, 3, 13, 1, 1, 'Caliper rebuild if necessary');

-- ==================== CATALOG 4: SOFTWARE UPDATE ====================

-- Tesla Model S (catalog=4, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (4, 1, 28, 1, 0, 'OBD-II diagnostic module for pre-update scan'),     -- Diagnostic OBD-II Module
    (4, 1, 29, 1, 1, 'VCU replacement if corrupted firmware detected'),    -- Vehicle Control Unit
    (4, 1, 30, 1, 1, 'Touchscreen replacement if display malfunction');    -- Touchscreen Display

-- VinFast VF8 (catalog=4, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (4, 2, 28, 1, 0, 'Diagnostic scan before OTA update'),
    (4, 2, 29, 1, 1, 'VCU backup available if update fails');

-- ==================== CATALOG 5: AIR FILTER REPLACEMENT ====================

-- Tesla Model S (catalog=5, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (5, 1, 21, 1, 0, 'HEPA cabin air filter for bioweapon defense mode'); -- Cabin Air Filter HEPA

-- VinFast VF8 (catalog=5, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (5, 2, 22, 1, 0, 'Activated carbon filter for odor removal');          -- Cabin Air Filter Carbon

-- Toyota Corolla Cross EV (catalog=5, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (5, 3, 21, 1, 0, 'Standard HEPA cabin filter');

-- ==================== CATALOG 6: COOLANT SYSTEM SERVICE ====================

-- Tesla Model S (catalog=6, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 1, 14, 2, 0, 'Coolant fluid 10L total (2x 5L bottles)'),          -- Coolant Fluid 5L
    (6, 1, 15, 1, 1, 'Coolant pump replacement if failing'),                -- Coolant Pump
    (6, 1, 16, 1, 1, 'Hose kit replacement if leaking'),                   -- Coolant Hose Kit
    (6, 1, 17, 1, 1, 'Battery radiator replacement if clogged');           -- Radiator Battery Cooling

-- VinFast VF8 (catalog=6, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 2, 14, 3, 0, 'Dual cooling system requires 15L coolant'),
    (6, 2, 15, 1, 1, 'Electric coolant pump check'),
    (6, 2, 16, 1, 1, 'Coolant hose inspection');

-- Toyota Corolla Cross EV (catalog=6, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 3, 14, 2, 0, 'Standard coolant refill 10L'),
    (6, 3, 16, 1, 1, 'Hose replacement if degraded');

-- ==================== CATALOG 7: SUSPENSION INSPECTION ====================

-- Tesla Model S (catalog=7, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 1, 23, 2, 1, 'Front shock absorber pair (replace if leaking)'),    -- Front Shock Absorber
    (7, 1, 24, 2, 1, 'Rear shock absorber pair (replace if worn)'),        -- Rear Shock Absorber
    (7, 1, 25, 1, 1, 'Control arm bushing kit (if torn)'),                 -- Control Arm Bushing
    (7, 1, 26, 2, 0, 'Stabilizer bar link (common wear item)');            -- Stabilizer Bar Link

-- VinFast VF8 (catalog=7, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 2, 23, 2, 1, 'Front shocks for SUV-spec suspension'),
    (7, 2, 24, 2, 1, 'Rear shocks for heavier EV weight'),
    (7, 2, 25, 1, 1, 'Bushing kit for control arms'),
    (7, 2, 26, 2, 0, 'Stabilizer links check');

-- Toyota Corolla Cross EV (catalog=7, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 3, 23, 2, 1, 'Front strut inspection'),
    (7, 3, 24, 2, 1, 'Rear strut inspection'),
    (7, 3, 27, 2, 1, 'Coil spring replacement if sagging');                -- Coil Spring Front

-- ==================== CATALOG 8: WHEEL ALIGNMENT ====================

-- Tesla Model S (catalog=8, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (8, 1, 26, 2, 1, 'Replace stabilizer links if alignment is off'),
    (8, 1, 25, 1, 1, 'Control arm bushing affects alignment');

-- VinFast VF8 (catalog=8, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (8, 2, 26, 2, 1, 'Stabilizer link replacement during alignment'),
    (8, 2, 25, 1, 1, 'Bushing check before alignment');

-- Toyota Corolla Cross EV (catalog=8, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (8, 3, 26, 2, 1, 'Stabilizer link inspection'),
    (8, 3, 25, 1, 1, 'Control arm bushing check');

-- ==================== CATALOG 9: FULL VEHICLE DIAGNOSTIC ====================

-- Tesla Model S (catalog=9, model=1)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (9, 1, 28, 1, 0, 'Comprehensive OBD-II diagnostic scan'),              -- Diagnostic OBD-II Module
    (9, 1, 3, 1, 0, 'BMS full diagnostic report'),                         -- BMS
    (9, 1, 29, 1, 1, 'VCU replacement if error codes detected');           -- VCU

-- VinFast VF8 (catalog=9, model=2)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (9, 2, 28, 1, 0, 'Full system diagnostic with VinFast tools'),
    (9, 2, 3, 1, 0, 'Battery management system check'),
    (9, 2, 29, 1, 1, 'VCU diagnostic and firmware check');

-- Toyota Corolla Cross EV (catalog=9, model=3)
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (9, 3, 28, 1, 0, 'Standard EV diagnostic protocol'),
    (9, 3, 3, 1, 0, 'Battery system health check');
-- ===================================================================
-- bổ sung seed data cho bảng maintenance_records_parts
-- VF3
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 1, 14, 2, 0, 'VF3 - Basic coolant system, ~10L fluid required'),
    (6, 1, 16, 1, 1, 'VF3 - Inspect hoses for small leaks');

-- VF5 Plus
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 2, 14, 2, 0, 'VF5 Plus - 10L coolant total'),
    (6, 2, 15, 1, 1, 'VF5 Plus - Electric coolant pump check'),
    (6, 2, 16, 1, 1, 'VF5 Plus - Hose inspection');

-- VF6
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 3, 14, 3, 0, 'VF6 - Dual cooling loop (battery + motor), 15L coolant'),
    (6, 3, 15, 1, 1, 'VF6 - Coolant pump check'),
    (6, 3, 16, 1, 1, 'VF6 - Replace hose if worn');

-- VF7
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 4, 14, 3, 0, 'VF7 - 15L coolant refill'),
    (6, 4, 15, 1, 1, 'VF7 - Pump diagnostic'),
    (6, 4, 16, 1, 1, 'VF7 - Hose kit inspection');

-- VF8
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 5, 14, 3, 0, 'VF8 - Dual cooling system requires 15L coolant'),
    (6, 5, 15, 1, 1, 'VF8 - Electric coolant pump check'),
    (6, 5, 16, 1, 1, 'VF8 - Coolant hose inspection');

-- VF9
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 6, 14, 4, 0, 'VF9 - Large battery pack, 20L coolant total'),
    (6, 6, 15, 1, 1, 'VF9 - Dual pump configuration check'),
    (6, 6, 16, 2, 1, 'VF9 - Hose and connector replacement if leaking');

-- VF Wild
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (6, 7, 14, 4, 0, 'VF Wild - Prototype model, estimated 18L coolant'),
    (6, 7, 15, 1, 1, 'VF Wild - Pump inspection pending spec confirmation'),
    (6, 7, 16, 1, 1, 'VF Wild - Early design, hose system check');
-- VF3
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 1, 23, 2, 1, 'VF3 - Front shocks basic set'),
    (7, 1, 24, 2, 1, 'VF3 - Rear shocks replacement optional'),
    (7, 1, 26, 2, 0, 'VF3 - Stabilizer links standard check');

-- VF5 Plus
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 2, 23, 2, 1, 'VF5 Plus - Compact EV shocks'),
    (7, 2, 24, 2, 1, 'VF5 Plus - Rear absorber inspection'),
    (7, 2, 25, 1, 1, 'VF5 Plus - Control arm bushing kit check');

-- VF6
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 3, 23, 2, 1, 'VF6 - SUV front shocks'),
    (7, 3, 24, 2, 1, 'VF6 - Rear shocks check'),
    (7, 3, 26, 2, 0, 'VF6 - Stabilizer link standard');

-- VF7
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 4, 23, 2, 1, 'VF7 - Front shock absorber pair'),
    (7, 4, 24, 2, 1, 'VF7 - Rear shocks optional replacement'),
    (7, 4, 25, 1, 1, 'VF7 - Control arm bushing kit check');

-- VF8
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 5, 23, 2, 1, 'VF8 - Front shocks for SUV-spec suspension'),
    (7, 5, 24, 2, 1, 'VF8 - Rear shocks for heavier EV weight'),
    (7, 5, 25, 1, 1, 'VF8 - Bushing kit for control arms'),
    (7, 5, 26, 2, 0, 'VF8 - Stabilizer links check');

-- VF9
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 6, 23, 2, 1, 'VF9 - Large SUV front shocks'),
    (7, 6, 24, 2, 1, 'VF9 - Heavy rear shock absorbers'),
    (7, 6, 25, 1, 1, 'VF9 - Reinforced bushing set'),
    (7, 6, 26, 2, 0, 'VF9 - Stabilizer bar inspection');

-- VF Wild
INSERT INTO maintenance_catalogs_models_parts (maintenance_catalog_id, model_id, part_id, quantity_required, is_optional, notes)
VALUES
    (7, 7, 23, 2, 1, 'VF Wild - Front struts prototype'),
    (7, 7, 24, 2, 1, 'VF Wild - Rear shocks under test'),
    (7, 7, 25, 1, 1, 'VF Wild - Control bushings experimental');

-- ===================================================================