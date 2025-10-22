-- ===================================================================
-- SCRIPT SEED DATA TỔNG HỢP
-- Version: 2.0 (Đã tổ chức lại và bổ sung đầy đủ permissions)
-- Database: SQL Server
-- ===================================================================

-- ===================================================================
-- BẢNG 1: ROLES - Định nghĩa các vai trò trong hệ thống
-- ===================================================================
INSERT INTO roles (name) VALUES
('ADMIN'),
('STAFF'),
('TECHNICIAN'),
('CUSTOMER');

-- ===================================================================
-- BẢNG 2: VEHICLE_MODELS - Danh sách các mẫu xe
-- ===================================================================
INSERT INTO vehicle_models
(brand_name, model_name, dimensions, year_introduce, seats, battery_capacity_kwh, range_km, charging_time_hours, motor_power_kw, weight_kg, status, created_at)
VALUES
('Tesla', 'Model S', '4970x1964x1445', '2022', 5, 100.0, 650.0, 1.5, 500.0, 2100.0, 'ACTIVE', GETDATE()),
('VinFast', 'VF8', '4750x1900x1660', '2023', 5, 90.0, 550.0, 2.0, 400.0, 2200.0, 'ACTIVE', GETDATE()),
('Toyota', 'Corolla Cross EV', '4460x1825x1620', '2023', 5, 60.0, 400.0, 1.8, 150.0, 1600.0, 'INACTIVE', GETDATE());

-- ===================================================================
-- BẢNG 3: USERS - Danh sách người dùng (mật khẩu cho tất cả là: "string")
-- ===================================================================
INSERT INTO users (full_name, email_address, phone_number, hashed_password, role_id, status, created_at, update_at)
VALUES
('Nguyen Van A', 'admin@example.com', '0901234567', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 1, 'ACTIVE', GETDATE(), GETDATE()),
('Tran Thi B', 'staff@example.com', '0902345678', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', GETDATE(), GETDATE()),
('Le Van C', 'technician@example.com', '0903456789', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', GETDATE(), GETDATE()),
('Pham Thi D', 'customer@example.com', '0904567890', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE()),
('Nguyen Van E', 'user@example.com', '0905678901','$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE()),
('Dao Thi F', 'ban@example.com', '0905678902','$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ARCHIVED', GETDATE(), GETDATE());

-- ===================================================================
-- BẢNG 4: VEHICLES - Xe của từng khách hàng
-- ===================================================================
INSERT INTO vehicles
(plate_number, color, vin, customer_id, vehicle_model_id, entity_status, created_at, purchased_at)
VALUES
('51H-12345', 'Red', '1HGCM82633A123456', 4, 1, 'ACTIVE', GETDATE(), GETDATE()),
('51H-67890', 'Blue', '1HGCM82633A654321', 4, 2, 'ACTIVE', GETDATE(), GETDATE()),
('60A-22222', 'White', '1HGCM82633A888888', 4, 3, 'INACTIVE', GETDATE(), GETDATE()),
('51G-55555', 'Black', '5YJSA1E26HF123001', 5, 1, 'ACTIVE', GETDATE(), GETDATE()),
('59A-66666', 'Silver', '5YJSA1E26HF123002', 5, 2, 'ACTIVE', GETDATE(), GETDATE()),
('51C-77777', 'White', '5YJSA1E26HF123003', 5, 3, 'INACTIVE', GETDATE(), GETDATE());

-- ===================================================================
-- BẢNG 5: MAINTENANCE_CATALOGS - Các dịch vụ bảo dưỡng có sẵn
-- ===================================================================
INSERT INTO maintenance_catalogs (name, maintenance_service_type, description, est_time_minutes, current_price, status, created_at)
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
-- SCHEDULE (✅ BỔ SUNG)
('SCHEDULE', 'confirm', 1, 'Confirm schedule'),
('SCHEDULE', 'cancel', 1, 'Cancel schedule'),
('SCHEDULE', 'reschedule', 1, 'Reschedule booking'),
('SCHEDULE', 'checkin', 1, 'Check-in schedule'),
('SCHEDULE', 'no-show', 1, 'Mark no-show'),
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
('PART', 'view_low_stock', 1, 'View low stock alerts');

-- ===================================================================
-- BẢNG 7: ROLE_PERMISSIONS - Phân quyền cho từng vai trò
-- ===================================================================

-- 1) ADMIN: Có tất cả các quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, p.id FROM permissions p;

-- 2) STAFF: Có hầu hết các quyền vận hành
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, p.id FROM permissions p
WHERE
   -- Quyền hệ thống đặc biệt
    (p.resource = 'SYSTEM' AND p.action = 'bypass_ownership') OR
   -- Toàn quyền trên các miền nghiệp vụ chính
    p.resource IN ('VEHICLE', 'BOOKING', 'SCHEDULE', 'MAINTENANCE', 'PAYMENT', 'MAINTENANCE_SERVICE', 'PART') OR
   -- Quyền trên Vehicle Model (không được xóa)
    (p.resource = 'VEHICLE_MODEL' AND p.action IN ('read', 'create', 'update'));

-- 3) TECHNICIAN: Quyền liên quan đến kỹ thuật
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, p.id FROM permissions p
WHERE
   -- Chỉ đọc thông tin chung
    (p.resource IN ('VEHICLE', 'VEHICLE_MODEL', 'MAINTENANCE_SERVICE', 'BOOKING') AND p.action = 'read') OR
   -- Các hành động kỹ thuật trên booking
    (p.resource = 'MAINTENANCE' AND p.action IN ('start-inspection', 'request-approval', 'start-repair', 'complete')) OR
   -- Chỉ đọc và xem cảnh báo hết phụ tùng
    (p.resource = 'PART' AND p.action IN ('read', 'view_low_stock'));

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
    (p.resource = 'PAYMENT' AND p.action = 'pay'); -- Thanh toán

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
    ('Diagnostic OBD-II Module', 7001, 'Bosch', 'On-board diagnostics module for system scanning', 2800000, 20, 'ACTIVE', GETDATE()),
    ('Vehicle Control Unit (VCU)', 7002, 'Tesla', 'Main vehicle electronic control unit', 18000000, 5, 'ACTIVE', GETDATE()),
    ('Touchscreen Display - 17 inch', 7003, 'LG', 'Replacement center console touchscreen', 22000000, 8, 'ACTIVE', GETDATE()),

-- Parts ít dùng / hết hàng
    ('Emergency Brake Cable', 8001, 'Generic', 'Backup mechanical brake cable', 380000, 5, 'ACTIVE', GETDATE()),
    ('Wiper Blade Set', 8002, 'Bosch', 'Front windshield wiper blade pair', 420000, 3, 'ACTIVE', GETDATE()),
    ('Door Handle Assembly', 8003, 'Tesla', 'Electronic pop-out door handle mechanism', 6500000, 2, 'ACTIVE', GETDATE()),
    ('Charge Port Cover', 8004, 'Tesla', 'Motorized charging port door assembly', 3800000, 1, 'ACTIVE', GETDATE());

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