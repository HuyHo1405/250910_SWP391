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
('Nguyen Van E', 'user@example.com', '0905678901','$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', GETDATE(), GETDATE());

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
-- BẢNG 5: MAINTENANCE_SERVICES - Các dịch vụ bảo dưỡng có sẵn
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
('PAYMENT', 'cancel', 1, 'Cancel payment');


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
    p.resource IN ('VEHICLE', 'BOOKING', 'SCHEDULE', 'MAINTENANCE', 'PAYMENT', 'MAINTENANCE_SERVICE') OR
   -- Quyền trên Vehicle Model (không được xóa)
    (p.resource = 'VEHICLE_MODEL' AND p.action IN ('read', 'create', 'update'));

-- 3) TECHNICIAN: Quyền liên quan đến kỹ thuật
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, p.id FROM permissions p
WHERE
   -- Chỉ đọc thông tin chung
    (p.resource IN ('VEHICLE', 'VEHICLE_MODEL', 'MAINTENANCE_SERVICE', 'BOOKING') AND p.action = 'read') OR
   -- Các hành động kỹ thuật trên booking
    (p.resource = 'MAINTENANCE' AND p.action IN ('start-inspection', 'request-approval', 'start-repair', 'complete'));

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
