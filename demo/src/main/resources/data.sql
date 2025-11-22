-- ================================================================================================================== --
-- BẢNG MAINTENANCE CATALOG - Định nghĩa các loại dịch vụ bảo dưỡng
-- ================================================================================================================== --
IF
NOT EXISTS (SELECT 1 FROM maintenance_catalogs)
INSERT INTO maintenance_catalogs
(name, maintenance_service_category, description, status, created_at)
VALUES
(N'Kiểm tra pin điện áp cao', 'BATTERY_INSPECTION', N'Kiểm tra tổng thể tình trạng và chẩn đoán sức khỏe pin (SoH)', 'ACTIVE', GETDATE()),
(N'Đảo lốp', 'TIRE_ROTATION', N'Đảo vị trí 4 lốp để mòn đều', 'ACTIVE', GETDATE()),
(N'Kiểm tra hệ thống phanh', 'BRAKE_SYSTEM_CHECK', N'Kiểm tra má phanh, đĩa phanh, và mức dầu phanh', 'ACTIVE', GETDATE()),
(N'Cập nhật hệ thống & phần mềm', 'SOFTWARE_UPDATE', N'Cập nhật phần mềm điều khiển, hệ điều hành trên xe', 'ACTIVE', GETDATE()),
(N'Thay lọc gió cabin', 'AIR_FILTER_REPLACEMENT', N'Thay lọc gió cabin giúp chất lượng không khí tốt hơn', 'ACTIVE', GETDATE()),
(N'Dịch vụ hệ thống làm mát pin', 'COOLANT_SYSTEM_SERVICE', N'Kiểm tra & châm bổ sung dung dịch làm mát hệ thống pin', 'ACTIVE', GETDATE()),
(N'Kiểm tra hệ thống treo', 'SUSPENSION_INSPECTION', N'Kiểm tra giảm xóc, thanh cân bằng và các bộ phận treo', 'ACTIVE', GETDATE()),
(N'Cân chỉnh góc đặt bánh xe', 'WHEEL_ALIGNMENT', N'Cân chỉnh góc đặt bánh xe (độ chụm) giúp xe vận hành tối ưu', 'ACTIVE', GETDATE()),
(N'Chẩn đoán tổng thể xe', 'FULL_VEHICLE_DIAGNOSTIC', N'Chẩn đoán tổng thể các hệ thống điện, điện tử trên xe', 'ACTIVE', GETDATE()),
(N'Sạc pin lưu động khẩn cấp', 'EMERGENCY_CHARGING_SERVICE', N'Dịch vụ sạc pin tại chỗ khi xe hết điện bất ngờ', 'INACTIVE', GETDATE()),
(N'Thay dầu phanh', 'BRAKE_FLUID_REPLACEMENT', N'Xả và thay mới dầu phanh hệ thống định kỳ (2 năm)', 'ACTIVE', GETDATE()),
(N'Kiểm tra ắc quy 12V', 'AUX_BATTERY_CHECK', N'Kiểm tra & chẩn đoán sức khỏe ắc quy phụ 12V', 'ACTIVE', GETDATE()),
(N'Thay thế ắc quy 12V', 'AUX_BATTERY_REPLACEMENT', N'Thay thế ắc quy phụ 12V cấp nguồn cho hệ thống điều khiển', 'ACTIVE', GETDATE()),
(N'Bảo dưỡng hệ thống điều hòa', 'AC_SYSTEM_SERVICE', N'Kiểm tra, làm sạch và bổ sung ga/dầu cho hệ thống điều hòa', 'ACTIVE', GETDATE()),
(N'Thay gạt mưa', 'WIPER_BLADE_REPLACEMENT', N'Thay thế cặp gạt mưa trước/sau', 'ACTIVE', GETDATE()),
(N'Bổ sung nước rửa kính', 'WASHER_FLUID_TOPUP', N'Bổ sung nước rửa kính (miễn phí khi làm dịch vụ)', 'ACTIVE', GETDATE());

-- ================================================================================================================== --
-- BẢNG VEHICLE MODELS - Định nghĩa các mẫu xe hệ thống hỗ trợ bảo dưỡng
-- ================================================================================================================== --
IF
NOT EXISTS (SELECT 1 FROM vehicle_models)
INSERT INTO vehicle_models
(brand_name, model_name, dimensions, seats, battery_capacity_kwh, range_km, charging_time_hours, motor_power_kw, weight_kg, status, image_url, created_at)
VALUES
    ('VinFast', 'VF 3', '3190x1675x1600', 4, 18.64, 210, 0.5, 32, 1090, 'ACTIVE', 'https://vinfast-cars.vn/wp-content/uploads/2024/10/vinfast-vf3-do.png', GETDATE()),
    ('VinFast', 'VF 5 Plus', '3967x1723x1578', 5, 37.23, 326, 0.5, 100, 1360, 'ACTIVE', 'https://vinfastbinhthanh.com/wp-content/uploads/2024/01/vinfast_vf5_trang-768x768.webp', GETDATE()),
    ('VinFast', 'VF 6', '4238x1820x1594', 5, 59.6, 399, 0.5, 150, 1550, 'ACTIVE', 'https://i.pinimg.com/736x/1c/d6/c8/1cd6c8d23d8815f29ebd852f158e3119.jpg', GETDATE()),
    ('VinFast', 'VF 7', '4545x1890x1636', 5, 75.3, 431, 0.42, 260, 2025, 'ACTIVE', 'https://i.pinimg.com/736x/e3/c9/ae/e3c9aeed275f2b3efcf0f4e008a9992b.jpg', GETDATE()),
    ('VinFast', 'VF 8', '4750x1934x1667', 5, 87.7, 471, 0.43, 300, 2605, 'ACTIVE', 'https://i.pinimg.com/736x/8d/1d/43/8d1d4386aa53db78fa935b4ff4b67161.jpg', GETDATE()),
    ('VinFast', 'VF 9', '5118x2004x1696', 7, 92, 438, 0.43, 300, 2830, 'ACTIVE', 'https://i.pinimg.com/736x/43/e9/17/43e917f48fe53c38185bd39cf750d6d6.jpg', GETDATE()),
    ('VinFast', 'VF e34', '4300x1768x1613', 5, 41.9, 318, 0.3, 110, 1490, 'ACTIVE', 'https://i.pinimg.com/1200x/15/25/28/1525281d921639b33c0bd6308fa7e935.jpg', GETDATE()),
    ('VinFast', 'VF Wild', '5324x1997xN/A', 5, 120, 600, 2.0, 300, 2400, 'INACTIVE', 'https://autopro8.mediacdn.vn/134505113543774208/2024/3/25/nlh4913-17113739622841194169314.jpg', GETDATE());

-- ================================================================================================================== --
-- BẢNG PARTS - Định nghĩa các loại linh kiện bảo dưỡng được quản lý trong kho
-- ================================================================================================================== --
IF NOT EXISTS (SELECT 1 FROM parts)
INSERT INTO parts
(part_number, name, manufacturer, category, current_unit_price, quantity, reserved, used, status, created_at, image_url)
VALUES
-- === 1. Lọc (Filters) ===
-- ('PART-FIL-CAB-S') Hóa đơn 12 (Reserved), 17 (Reserved)
('PART-FIL-CAB-S', N'Lọc gió cabin (Loại S)', 'VinFast', 'FILTER', 150000, 200, 2, 0, 'ACTIVE', GETDATE(), 'https://down-vn.img.susercontent.com/file/vn-11134207-820l4-mejerouay3gk45.webp'),
-- ('PART-FIL-CAB-M') Hóa đơn 7 (Used)
('PART-FIL-CAB-M', N'Lọc gió cabin (Loại M)', 'VinFast', 'FILTER', 220000, 150, 0, 1, 'ACTIVE', GETDATE(), 'https://down-vn.img.susercontent.com/file/vn-11134207-820l4-mejerouay3gk45.webp'),
-- ('PART-FIL-CAB-L') Hóa đơn 1 (Used), 9 (Used), 14 (Reserved)
('PART-FIL-CAB-L', N'Lọc gió cabin (Loại L)', 'VinFast', 'FILTER', 300000, 100, 1, 2, 'ACTIVE', GETDATE(), 'https://down-vn.img.susercontent.com/file/vn-11134207-820l4-mejerouay3gk45.webp'),

-- === 2. Dung dịch & Hóa chất (Fluids) ===
-- ('PART-FLD-BRK-01') Hóa đơn 3 (Used), 9 (Used x2), 18 (Reserved)
('PART-FLD-BRK-01', N'Dầu phanh DOT 4 (1L)', 'Castrol', 'FLUID', 180000, 300, 1, 3, 'ACTIVE', GETDATE(), 'https://bizweb.dktcdn.net/100/360/787/products/3093.jpg?v=1663317054257'),
-- ('PART-FLD-COL-01') Hóa đơn 3 (Used)
('PART-FLD-COL-01', N'Nước làm mát pin (EV Coolant) (5L)', 'Prestone', 'FLUID', 750000, 150, 0, 1, 'ACTIVE', GETDATE(), 'https://daunhotpenrite.com/wp-content/uploads/2023/03/RUN005-2-e1677732726630.png'),
-- ('PART-FLD-WSH-01') Hóa đơn 1, 6, 10 (Used) | 13, 16 (Reserved)
('PART-FLD-WSH-01', N'Nước rửa kính (2L)', 'VinFast', 'FLUID', 50000, 500, 2, 3, 'ACTIVE', GETDATE(), 'https://toyotahcm.vn/wp-content/uploads/2024/06/nuoc-rua-kinh-o-to-toyota-3.jpg'),
-- ('PART-FLD-ACG-01')
('PART-FLD-ACG-01', N'Gas điều hòa R-1234yf (1kg)', 'Honeywell', 'FLUID', 2500000, 50, 0, 0, 'ACTIVE', GETDATE(), 'https://news.oto-hui.com/wp-content/uploads/2022/06/r-1234yf-moi-chat-lam-lanh-thay-the-r-134a-cu-trong-he-thong-dieu-hoa-o-to-5.jpg'),
-- ('PART-FLD-ACO-01')
('PART-FLD-ACO-01', N'Dầu máy nén A/C (POE) (100ml)', 'Sanden', 'FLUID', 450000, 80, 0, 0, 'ACTIVE', GETDATE(), 'https://media.witglobal.net/source/eshop/stmedia/0100/images/std.lang.all/resolutions/category/576px/130432.jpg'),

-- === 3. Ắc quy 12V (Auxiliary Batteries) ===
-- ('PART-BAT-12V-45') Hóa đơn 8 (Used)
('PART-BAT-12V-45', N'Ắc quy 12V 45Ah (AGM)', 'GS Battery', 'BATTERY', 1800000, 70, 0, 1, 'ACTIVE', GETDATE(), 'https://giaphatbattery.com/wp-content/uploads/2023/10/long-12v-45ah-wp45-12.png'),
-- ('PART-BAT-12V-60')
('PART-BAT-12V-60', N'Ắc quy 12V 60Ah (AGM)', 'Varta', 'BATTERY', 2500000, 50, 0, 0, 'ACTIVE', GETDATE(), 'https://product.hstatic.net/1000296508/product/rocket_agm_997ec35bab674934b0db9406cb7630c8_master.jpg'),

-- === 4. Gạt mưa (Wipers) ===
-- ('PART-WPR-16')
('PART-WPR-16', N'Gạt mưa Bosch 16"', 'Bosch', 'WIPER', 120000, 100, 0, 0, 'ACTIVE', GETDATE(), 'https://bizweb.dktcdn.net/100/366/403/products/bosch-ba-20-9492fec8-1571-46c7-b940-e8a770d1f642.jpg?v=1679043168857'),
-- ('PART-WPR-24') Hóa đơn 5 (Used) | 11, 13 (Reserved)
('PART-WPR-24', N'Gạt mưa Bosch 24"', 'Bosch', 'WIPER', 180000, 100, 2, 1, 'ACTIVE', GETDATE(), 'https://bizweb.dktcdn.net/100/366/403/products/bosch-ba-20-9492fec8-1571-46c7-b940-e8a770d1f642.jpg?v=1679043168857'),
   -- ('PART-WPR-26') Hóa đơn 5 (Used) | 11, 13 (Reserved)
('PART-WPR-26', N'Gạt mưa Bosch 26"', 'Bosch', 'WIPER', 200000, 100, 2, 1, 'ACTIVE', GETDATE(), 'https://bizweb.dktcdn.net/100/366/403/products/bosch-ba-20-9492fec8-1571-46c7-b940-e8a770d1f642.jpg?v=1679043168857'),

-- === 5. Lốp (Tires) ===
('PART-TIRE-175-65R16', N'Lốp 175/65R16', 'Bridgestone', 'TIRE', 1500000, 40, 0, 0, 'ACTIVE', GETDATE(), 'https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2208486842803/O1CN016qioii1WZozb27d9q_!!2208486842803.jpg_400x400.jpg_.webp'),
('PART-TIRE-215-60R17', N'Lốp 215/60R17', 'Goodyear', 'TIRE', 2100000, 40, 0, 0, 'ACTIVE', GETDATE(), 'https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2208486842803/O1CN016qioii1WZozb27d9q_!!2208486842803.jpg_400x400.jpg_.webp'),
('PART-TIRE-215-55R18', N'Lốp 215/55R18', 'Michelin', 'TIRE', 2800000, 40, 0, 0, 'ACTIVE', GETDATE(), 'https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2208486842803/O1CN016qioii1WZozb27d9q_!!2208486842803.jpg_400x400.jpg_.webp'),
('PART-TIRE-245-45R20', N'Lốp 245/45R20', 'Pirelli', 'TIRE', 4500000, 30, 0, 0, 'ACTIVE', GETDATE(), 'https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2208486842803/O1CN016qioii1WZozb27d9q_!!2208486842803.jpg_400x400.jpg_.webp'),
('PART-TIRE-275-40R22', N'Lốp 275/40R22', 'Pirelli', 'TIRE', 6000000, 20, 0, 0, 'ACTIVE', GETDATE(), 'https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2208486842803/O1CN016qioii1WZozb27d9q_!!2208486842803.jpg_400x400.jpg_.webp'),

-- === 6. Phanh (Brakes) ===
('PART-BRAKE-PAD-F-S', N'Má phanh trước (Loại S)', 'Brembo', 'BRAKE', 800000, 60, 0, 0, 'ACTIVE', GETDATE(), 'https://bizweb.dktcdn.net/100/366/403/products/10032598a-jpg-v-1695960203640-537afc15-1a5c-4d82-beac-c05b74c64127.jpg?v=1697308710877'),
('PART-BRAKE-PAD-F-L', N'Má phanh trước (Loại L)', 'Brembo', 'BRAKE', 1500000, 40, 0, 0, 'ACTIVE', GETDATE(), 'https://bizweb.dktcdn.net/100/366/403/products/10032598a-jpg-v-1695960203640-537afc15-1a5c-4d82-beac-c05b74c64127.jpg?v=1697308710877');
-- ================================================================================================================== --
-- BẢNG MAINTENANCE CATALOG MODELS - Mapping các mẫu xe với các dịch vụ
-- BẢNG MAINTENANCE CATALOG MODEL PARTS - Mapping các linh kiện cho các mẫu xe và các dịch vụ
-- ================================================================================================================== --
IF
NOT EXISTS (SELECT 1 FROM maintenance_catalogs_models)
INSERT INTO maintenance_catalogs_models
(maintenance_catalog_id, model_id, est_time_minutes, maintenance_price, notes, created_at, status) -- Thêm 'status'
VALUES
-- === ID 1: BATTERY_INSPECTION (Kiểm tra pin) ===
(1, 1, 45, 250000, NULL, GETDATE(), 'ACTIVE'), -- Thêm 'ACTIVE'
(1, 2, 60, 300000, NULL, GETDATE(), 'ACTIVE'),
(1, 3, 60, 350000, NULL, GETDATE(), 'ACTIVE'),
(1, 4, 60, 400000, NULL, GETDATE(), 'ACTIVE'),
(1, 5, 60, 450000, NULL, GETDATE(), 'ACTIVE'),
(1, 6, 75, 500000, NULL, GETDATE(), 'ACTIVE'),
(1, 7, 60, 300000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 2: TIRE_ROTATION (Đảo lốp) ===
(2, 1, 30, 150000, NULL, GETDATE(), 'ACTIVE'),
(2, 2, 30, 150000, NULL, GETDATE(), 'ACTIVE'),
(2, 3, 30, 180000, NULL, GETDATE(), 'ACTIVE'),
(2, 4, 30, 180000, NULL, GETDATE(), 'ACTIVE'),
(2, 5, 45, 200000, NULL, GETDATE(), 'ACTIVE'),
(2, 6, 45, 250000, NULL, GETDATE(), 'ACTIVE'),
(2, 7, 30, 150000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 3: BRAKE_SYSTEM_CHECK (Kiểm tra phanh) ===
(3, 1, 30, 150000, NULL, GETDATE(), 'ACTIVE'),
(3, 2, 30, 150000, NULL, GETDATE(), 'ACTIVE'),
(3, 3, 30, 180000, NULL, GETDATE(), 'ACTIVE'),
(3, 4, 30, 180000, NULL, GETDATE(), 'ACTIVE'),
(3, 5, 45, 200000, NULL, GETDATE(), 'ACTIVE'),
(3, 6, 45, 250000, NULL, GETDATE(), 'ACTIVE'),
(3, 7, 30, 150000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 4: SOFTWARE_UPDATE (Cập nhật phần mềm) ===
(4, 1, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),
(4, 2, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),
(4, 3, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),
(4, 4, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),
(4, 5, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),
(4, 6, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),
(4, 7, 30, 0, N'Miễn phí (nếu có bản cập nhật)', GETDATE(), 'ACTIVE'),

-- === ID 5: AIR_FILTER_REPLACEMENT (Thay lọc gió cabin) ===
(5, 1, 15, 50000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),
(5, 2, 15, 50000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),
(5, 3, 15, 50000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),
(5, 4, 15, 70000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),
(5, 5, 20, 80000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),
(5, 6, 20, 80000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),
(5, 7, 15, 50000, N'Giá công, chưa bao gồm linh kiện', GETDATE(), 'ACTIVE'),

-- === ID 6: COOLANT_SYSTEM_SERVICE (Dịch vụ hệ thống làm mát pin) ===
(6, 1, 60, 300000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),
(6, 2, 60, 350000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),
(6, 3, 60, 400000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),
(6, 4, 75, 450000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),
(6, 5, 90, 500000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),
(6, 6, 90, 550000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),
(6, 7, 60, 350000, N'Giá công, chưa bao gồm dung dịch', GETDATE(), 'ACTIVE'),

-- === ID 7: SUSPENSION_INSPECTION (Kiểm tra hệ thống treo) ===
(7, 1, 45, 200000, NULL, GETDATE(), 'ACTIVE'),
(7, 2, 45, 200000, NULL, GETDATE(), 'ACTIVE'),
(7, 3, 45, 250000, NULL, GETDATE(), 'ACTIVE'),
(7, 4, 45, 250000, NULL, GETDATE(), 'ACTIVE'),
(7, 5, 60, 300000, NULL, GETDATE(), 'ACTIVE'),
(7, 6, 60, 350000, NULL, GETDATE(), 'ACTIVE'),
(7, 7, 45, 200000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 8: WHEEL_ALIGNMENT (Cân chỉnh góc đặt bánh xe) ===
(8, 1, 60, 350000, NULL, GETDATE(), 'ACTIVE'),
(8, 2, 60, 400000, NULL, GETDATE(), 'ACTIVE'),
(8, 3, 75, 450000, NULL, GETDATE(), 'ACTIVE'),
(8, 4, 75, 500000, NULL, GETDATE(), 'ACTIVE'),
(8, 5, 90, 600000, NULL, GETDATE(), 'ACTIVE'),
(8, 6, 90, 700000, NULL, GETDATE(), 'ACTIVE'),
(8, 7, 60, 400000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 9: FULL_VEHICLE_DIAGNOSTIC (Chẩn đoán tổng thể xe) ===
(9, 1, 60, 300000, NULL, GETDATE(), 'ACTIVE'),
(9, 2, 60, 300000, NULL, GETDATE(), 'ACTIVE'),
(9, 3, 60, 400000, NULL, GETDATE(), 'ACTIVE'),
(9, 4, 60, 400000, NULL, GETDATE(), 'ACTIVE'),
(9, 5, 75, 500000, NULL, GETDATE(), 'ACTIVE'),
(9, 6, 75, 500000, NULL, GETDATE(), 'ACTIVE'),
(9, 7, 60, 300000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 11: BRAKE_FLUID_REPLACEMENT (Thay dầu phanh) ===
(11, 1, 45, 200000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),
(11, 2, 45, 200000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),
(11, 3, 60, 250000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),
(11, 4, 60, 250000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),
(11, 5, 60, 300000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),
(11, 6, 60, 300000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),
(11, 7, 45, 200000, N'Giá công, chưa bao gồm dầu phanh', GETDATE(), 'ACTIVE'),

-- === ID 12: AUX_BATTERY_CHECK (Kiểm tra ắc quy 12V) ===
(12, 1, 15, 100000, NULL, GETDATE(), 'ACTIVE'),
(12, 2, 15, 100000, NULL, GETDATE(), 'ACTIVE'),
(12, 3, 15, 100000, NULL, GETDATE(), 'ACTIVE'),
(12, 4, 15, 100000, NULL, GETDATE(), 'ACTIVE'),
(12, 5, 15, 100000, NULL, GETDATE(), 'ACTIVE'),
(12, 6, 15, 100000, NULL, GETDATE(), 'ACTIVE'),
(12, 7, 15, 100000, NULL, GETDATE(), 'ACTIVE'),

-- === ID 13: AUX_BATTERY_REPLACEMENT (Thay thế ắc quy 12V) ===
(13, 1, 30, 100000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),
(13, 2, 30, 100000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),
(13, 3, 30, 100000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),
(13, 4, 30, 120000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),
(13, 5, 45, 150000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),
(13, 6, 45, 150000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),
(13, 7, 30, 100000, N'Giá công, chưa bao gồm ắc quy', GETDATE(), 'ACTIVE'),

-- === ID 14: AC_SYSTEM_SERVICE (Bảo dưỡng hệ thống điều hòa) ===
(14, 1, 60, 400000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),
(14, 2, 60, 400000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),
(14, 3, 60, 450000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),
(14, 4, 60, 450000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),
(14, 5, 75, 500000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),
(14, 6, 75, 550000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),
(14, 7, 60, 400000, N'Giá công, chưa bao gồm vật tư (ga, dầu)', GETDATE(), 'ACTIVE'),

-- === ID 15: WIPER_BLADE_REPLACEMENT (Thay gạt mưa) ===
(15, 1, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),
(15, 2, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),
(15, 3, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),
(15, 4, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),
(15, 5, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),
(15, 6, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),
(15, 7, 10, 50000, N'Giá công, chưa bao gồm gạt mưa', GETDATE(), 'ACTIVE'),

-- === ID 16: WASHER_FLUID_TOPUP (Bổ sung nước rửa kính) ===
(16, 1, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE'),
(16, 2, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE'),
(16, 3, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE'),
(16, 4, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE'),
(16, 5, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE'),
(16, 6, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE'),
(16, 7, 5, 0, N'Miễn phí (tối đa 1L)', GETDATE(), 'ACTIVE');

IF
NOT EXISTS (SELECT 1 FROM maintenance_catalogs_models_parts)
INSERT INTO maintenance_catalogs_models_parts
(maintenance_catalog_model_id, part_id, quantity_required, is_optional, notes)
VALUES
-- === ID 5: AIR_FILTER_REPLACEMENT (Thay lọc gió cabin) ===
-- (Catalog 5, Model 1) -> ID 29
(29, 1, 1, 0, N'Dùng Lọc gió S'),
-- (Catalog 5, Model 2) -> ID 30
(30, 1, 1, 0, N'Dùng Lọc gió S'),
-- (Catalog 5, Model 7) -> ID 35
(35, 1, 1, 0, N'Dùng Lọc gió S'),
-- (Catalog 5, Model 3) -> ID 31
(31, 2, 1, 0, N'Dùng Lọc gió M'),
-- (Catalog 5, Model 4) -> ID 32
(32, 2, 1, 0, N'Dùng Lọc gió M'),
-- (Catalog 5, Model 5) -> ID 33
(33, 3, 1, 0, N'Dùng Lọc gió L'),
-- (Catalog 5, Model 6) -> ID 34
(34, 3, 1, 0, N'Dùng Lọc gió L'),

-- === ID 6: COOLANT_SYSTEM_SERVICE (Dịch vụ hệ thống làm mát pin) ===
-- (Catalog 6, Model 1) -> ID 36
(36, 5, 1, 0, N'Cần 1 bình 5L'),
-- (Catalog 6, Model 2) -> ID 37
(37, 5, 1, 0, N'Cần 1 bình 5L'),
-- (Catalog 6, Model 3) -> ID 38
(38, 5, 2, 0, N'Cần 2 bình 5L (hệ thống lớn)'),
-- (Catalog 6, Model 4) -> ID 39
(39, 5, 2, 0, N'Cần 2 bình 5L'),
-- (Catalog 6, Model 5) -> ID 40
(40, 5, 2, 0, N'Cần 2 bình 5L'),
-- (Catalog 6, Model 6) -> ID 41
(41, 5, 3, 0, N'Cần 3 bình 5L (hệ thống rất lớn)'),
-- (Catalog 6, Model 7) -> ID 42
(42, 5, 1, 0, N'Cần 1 bình 5L'),

-- === ID 11: BRAKE_FLUID_REPLACEMENT (Thay dầu phanh) ===
-- (Catalog 11, Model 1) -> ID 64
(64, 4, 1, 0, N'Cần 1-2L'),
-- (Catalog 11, Model 2) -> ID 65
(65, 4, 1, 0, N'Cần 1-2L'),
-- (Catalog 11, Model 3) -> ID 66
(66, 4, 1, 0, N'Cần 1-2L'),
-- (Catalog 11, Model 4) -> ID 67
(67, 4, 2, 0, N'Cần 2L'),
-- (Catalog 11, Model 5) -> ID 68
(68, 4, 2, 0, N'Cần 2L'),
-- (Catalog 11, Model 6) -> ID 69
(69, 4, 2, 0, N'Cần 2L'),
-- (Catalog 11, Model 7) -> ID 70
(70, 4, 1, 0, N'Cần 1-2L'),

-- === ID 13: AUX_BATTERY_REPLACEMENT (Thay thế ắc quy 12V) ===
-- (Catalog 13, Model 1) -> ID 78
(78, 9, 1, 0, N'Dùng Ắc quy 45Ah'),
-- (Catalog 13, Model 2) -> ID 79
(79, 9, 1, 0, N'Dùng Ắc quy 45Ah'),
-- (Catalog 13, Model 7) -> ID 84
(84, 9, 1, 0, N'Dùng Ắc quy 45Ah'),
-- (Catalog 13, Model 3) -> ID 80
(80, 9, 1, 0, N'Dùng Ắc quy 45Ah'),
-- (Catalog 13, Model 4) -> ID 81
(81, 10, 1, 0, N'Dùng Ắc quy 60Ah'),
-- (Catalog 13, Model 5) -> ID 82
(82, 10, 1, 0, N'Dùng Ắc quy 60Ah'),
-- (Catalog 13, Model 6) -> ID 83
(83, 10, 1, 0, N'Dùng Ắc quy 60Ah'),

-- === ID 14: AC_SYSTEM_SERVICE (Bảo dưỡng hệ thống điều hòa) ===
-- (Catalog 14, Model 1) -> ID 85
(85, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 1) -> ID 85
(85, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),
-- (Catalog 14, Model 2) -> ID 86
(86, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 2) -> ID 86
(86, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),
-- (Catalog 14, Model 3) -> ID 87
(87, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 3) -> ID 87
(87, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),
-- (Catalog 14, Model 4) -> ID 88
(88, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 4) -> ID 88
(88, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),
-- (Catalog 14, Model 5) -> ID 89
(89, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 5) -> ID 89
(89, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),
-- (Catalog 14, Model 6) -> ID 90
(90, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 6) -> ID 90
(90, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),
-- (Catalog 14, Model 7) -> ID 91
(91, 7, 1, 1, N'Tùy chọn nếu nạp bổ sung gas'),
-- (Catalog 14, Model 7) -> ID 91
(91, 8, 1, 1, N'Tùy chọn nếu nạp bổ sung dầu'),

-- === ID 15: WIPER_BLADE_REPLACEMENT (Thay gạt mưa) ===
-- (Catalog 15, Model 1) -> ID 92
(92, 11, 1, 0, N'Gạt mưa bên phụ 16"'),
-- (Catalog 15, Model 1) -> ID 92
(92, 12, 1, 0, N'Gạt mưa bên lái 24"'),
-- (Catalog 15, Model 2) -> ID 93
(93, 11, 1, 0, N'Gạt mưa bên phụ 16"'),
-- (Catalog 15, Model 2) -> ID 93
(93, 12, 1, 0, N'Gạt mưa bên lái 24"'),
-- (Catalog 15, Model 7) -> ID 98
(98, 11, 1, 0, N'Gạt mưa bên phụ 16"'),
-- (Catalog 15, Model 7) -> ID 98
(98, 12, 1, 0, N'Gạt mưa bên lái 24"'),
-- (Catalog 15, Model 3) -> ID 94
(94, 12, 1, 0, N'Gạt mưa bên phụ 24"'),
-- (Catalog 15, Model 3) -> ID 94
(94, 13, 1, 0, N'Gạt mưa bên lái 26"'),
-- (Catalog 15, Model 4) -> ID 95
(95, 12, 1, 0, N'Gạt mưa bên phụ 24"'),
-- (Catalog 15, Model 4) -> ID 95
(95, 13, 1, 0, N'Gạt mưa bên lái 26"'),
-- (Catalog 15, Model 5) -> ID 96
(96, 12, 1, 0, N'Gạt mưa bên phụ 24"'),
-- (Catalog 15, Model 5) -> ID 96
(96, 13, 1, 0, N'Gạt mưa bên lái 26"'),
-- (Catalog 15, Model 6) -> ID 97
(97, 12, 1, 0, N'Gạt mưa bên phụ 24"'),
-- (Catalog 15, Model 6) -> ID 97
(97, 13, 1, 0, N'Gạt mưa bên lái 26"'),

-- === ID 16: WASHER_FLUID_TOPUP (Bổ sung nước rửa kính) ===
-- (Catalog 16, Model 1) -> ID 99
(99, 6, 1, 0, NULL),
-- (Catalog 16, Model 2) -> ID 100
(100, 6, 1, 0, NULL),
-- (Catalog 16, Model 3) -> ID 101
(101, 6, 1, 0, NULL),
-- (Catalog 16, Model 4) -> ID 102
(102, 6, 1, 0, NULL),
-- (Catalog 16, Model 5) -> ID 103
(103, 6, 1, 0, NULL),
-- (Catalog 16, Model 6) -> ID 104
(104, 6, 1, 0, NULL),
-- (Catalog 16, Model 7) -> ID 105
(105, 6, 1, 0, NULL),

-- === ID 3: BRAKE_SYSTEM_CHECK (Kiểm tra phanh) ===
-- (Catalog 3, Model 2) -> ID 16
(16, 19, 2, 1, N'Tùy chọn nếu má phanh mòn (Loại S)'),
-- (Catalog 3, Model 7) -> ID 21
(21, 19, 2, 1, N'Tùy chọn nếu má phanh mòn (Loại S)'),
-- (Catalog 3, Model 3) -> ID 17
(17, 19, 2, 1, N'Tùy chọn nếu má phanh mòn (Loại S)'),
-- (Catalog 3, Model 5) -> ID 19
(19, 20, 2, 1, N'Tùy chọn nếu má phanh mòn (Loại L)'),
-- (Catalog 3, Model 6) -> ID 20
(20, 20, 2, 1, N'Tùy chọn nếu má phanh mòn (Loại L)');

-- ================================================================================================================== --
-- BẢNG ROLES - Định nghĩa các vai trò trong hệ thống
-- BẢNG ROLES EDITABLE - Định nghĩa các quyền được chỉnh các vai trò khác
-- ================================================================================================================== --
IF
NOT EXISTS (SELECT 1 FROM roles)
INSERT INTO roles (name, display_name)
VALUES
('ADMIN', N'Quản trị viên'),
('STAFF', N'Nhân viên'),
('TECHNICIAN', N'Kỹ thuật viên'),
('CUSTOMER', N'Khách hàng');

   IF
NOT EXISTS (SELECT 1 FROM role_editable)
INSERT INTO role_editable (role_id, editable_id)
VALUES
-- Admin được chỉnh tất cả
(1, 1),
(1, 2),
(1, 3),
(1, 4),

-- Staff được chỉnh Technician và Customer
(2, 3),
(2, 4);

-- ================================================================================================================== --
-- BẢNG PERMISSIONS - Định nghĩa các phân quyền truy cập và chỉnh sửa với tài nguyên
-- BẢNG ROLE PERMISSION - Mapping các role với các phân quyền
-- ================================================================================================================== --
IF
NOT EXISTS (SELECT 1 FROM permissions)
INSERT INTO permissions
(resource, action, is_active, description)
VALUES
-- SYSTEM
('SYSTEM', 'bypass_ownership', 1, 'Bypass ownership checks for all resources'),
-- DASHBOARD
('DASHBOARD', 'read', 1, 'Read dashboard metrics and summaries'),
-- USER
('USER', 'create', 1, 'Create user'),
('USER', 'delete', 1, 'Delete user'),
('USER', 'disable', 1, 'Disable user (set INACTIVE)'),
('USER', 'read', 1, 'Read user info'),
('USER', 'reactive', 1, 'Reactive user (set ACTIVE)'),
('USER', 'update', 1, 'Update user'),
-- VEHICLE
('VEHICLE', 'create', 1, 'Create vehicles'),
('VEHICLE', 'delete', 1, 'Delete vehicles'),
('VEHICLE', 'read', 1, 'Read vehicles'),
('VEHICLE', 'update', 1, 'Update vehicles'),
('VEHICLE', 'bypass_ownership', 1, 'Bypass ownership check specifically for vehicles'),
-- VEHICLE_MODEL
('VEHICLE_MODEL', 'create', 1, 'Create vehicle models'),
('VEHICLE_MODEL', 'delete', 1, 'Delete vehicle models'),
('VEHICLE_MODEL', 'read', 1, 'Read vehicle models'),
('VEHICLE_MODEL', 'read-by-status', 1, 'Read vehicle models by status'),
('VEHICLE_MODEL', 'update', 1, 'Update vehicle models'),
-- MAINTENANCE_SERVICE
('MAINTENANCE_SERVICE', 'create', 1, 'Create maintenance services'),
('MAINTENANCE_SERVICE', 'delete', 1, 'Delete maintenance services'),
('MAINTENANCE_SERVICE', 'read', 1, 'Read maintenance services'),
('MAINTENANCE_SERVICE', 'update', 1, 'Update maintenance services'),
-- PART
('PART', 'create', 1, 'Create new parts'),
('PART', 'delete', 1, 'Delete parts from inventory'),
('PART', 'manage_reserved', 1, 'Manage reserved parts stock'),
('PART', 'manage_stock', 1, 'Manage part stock levels (increase/decrease)'),
('PART', 'read', 1, 'Read parts inventory'),
('PART', 'update', 1, 'Update part information'),
('PART', 'view_low_stock', 1, 'View low stock alerts'),
-- BOOKING
('BOOKING', 'cancel', 1, 'Cancel booking'),
('BOOKING', 'reject', 1, 'Reject booking'),
('BOOKING', 'complete', 1, 'Complete booking'),
('BOOKING', 'confirm', 1, 'Confirm booking'),
('BOOKING', 'create', 1, 'Create booking'),
('BOOKING', 'delete', 1, 'Delete booking'),
('BOOKING', 'read', 1, 'Read booking'),
('BOOKING', 'start-maintenance', 1, 'Start maintenance for a booking'),
('BOOKING', 'update', 1, 'Update booking'),
-- INVOICE
('INVOICE', 'create', 1, 'Create invoice'),
('INVOICE', 'update', 1, 'Update invoice'),
('INVOICE', 'read', 1, 'Read invoice'),
('INVOICE', 'delete', 1, 'Delete invoice'),
-- PAYMENT
('PAYMENT', 'authorize', 1, 'Authorize payment'),
('PAYMENT', 'cancel', 1, 'Cancel payment'),
('PAYMENT', 'pay', 1, 'Pay booking'),
('PAYMENT', 'refund', 1, 'Refund payment'),
('PAYMENT', 'view', 1, 'View payment'),
-- JOB
('JOB', 'READ', 1, 'Read job'),
('JOB', 'CREATE', 1, 'Assign job for technician'),
('JOB', 'UPDATE', 1, 'Update job'),
('JOB', 'DELETE', 1, 'Delete job'),
('JOB', 'START', 1, 'Start doing job'),
('JOB', 'COMPLETE', 1, 'Complete jpb'),
-- FEEDBACK
('FEEDBACK', 'CREATE', 1, 'Create feedback'),
('FEEDBACK', 'READ', 1, 'Read feedback'),
('FEEDBACK', 'UPDATE', 1, 'Update feedback'),
('FEEDBACK', 'DELETE', 1, 'Delete feedback'),
-- ROLE
('ROLE', 'read', 1, 'Read role');

-- 1) ADMIN: Có tất cả các quyền
IF
NOT EXISTS (SELECT 1 FROM role_permissions rp WHERE rp.role_id = 1)
INSERT INTO role_permissions
(role_id, permission_id)
SELECT 1, p.id
FROM permissions p;

-- 2) STAFF:
IF
NOT EXISTS (SELECT 1 FROM role_permissions rp WHERE rp.role_id = 2)
INSERT INTO role_permissions
(role_id, permission_id)
SELECT 2, p.id
FROM permissions p
WHERE
-- Quyền hệ thống (bỏ qua check ownership)
    (p.resource = 'SYSTEM' AND p.action = 'bypass_ownership')
   OR
-- Quyền được chỉnh sửa các tài nguyên
    (p.resource IN (
                    'USER',
                    'VEHICLE',
                    'VEHICLE_MODEL',
                    'BOOKING',
                    'INVOICE',
                    'PAYMENT',
                    'MAINTENANCE_SERVICE',
                    'PART',
                    'JOB',
                    'ROLE',
                   'FEEDBACK'
        ));

-- 3) TECHNICIAN:
IF
NOT EXISTS (SELECT 1 FROM role_permissions rp WHERE rp.role_id = 3)
INSERT INTO role_permissions
(role_id, permission_id)
SELECT 3, p.id
FROM permissions p
WHERE
-- Quyền đọc thông tin chung (xe, mẫu xe, dịch vụ, booking)
    (p.resource IN ('VEHICLE', 'VEHICLE_MODEL', 'MAINTENANCE_SERVICE', 'BOOKING', 'FEEDBACK') AND p.action = 'read')
   OR
-- Quyền kỹ thuật trên quy trình (Ánh xạ sang BOOKING)
    (p.resource = 'BOOKING' AND p.action IN ('start-maintenance', 'complete'))
   OR
-- Quyền thao tác kho (chỉ đọc và xem cảnh báo)
    (p.resource = 'PART' AND p.action IN ('read'))
   OR
-- Quyền tự xem và cập nhật thông tin cá nhân
    (p.resource = 'USER' AND p.action IN ('read', 'update'))
   OR
-- Quyền tự xem và làm những công việc được phân công
    (p.resource = 'JOB' AND p.action IN ('read', 'start', 'complete'))
   OR
-- Quyền tự xem xe của customer được phân công làm việc
    (p.resource = 'VEHICLE' AND p.action = 'bypass_ownership');

-- 4) CUSTOMER:
IF
NOT EXISTS (SELECT 1 FROM role_permissions rp WHERE rp.role_id = 4)
INSERT INTO role_permissions
(role_id, permission_id)
SELECT 4, p.id
FROM permissions p
WHERE
-- Quyền quản lý xe của mình
    (p.resource = 'VEHICLE' AND p.action IN ('create', 'read', 'update', 'delete'))
   OR
-- Quyền xem thông tin chung (mẫu xe, dịch vụ)
    (p.resource IN ('VEHICLE_MODEL', 'MAINTENANCE_SERVICE') AND p.action = 'read')
   OR
-- Quyền thao tác booking
    (p.resource = 'BOOKING' AND p.action IN ('create', 'read', 'cancel'))
   OR
-- Quyền coi hóa đơn
    (p.resource = 'INVOICE' AND p.action IN ('read', 'create'))
   OR
-- Quyền thanh toán
    (p.resource = 'PAYMENT' AND p.action IN ('pay', 'view'))
   OR
    (p.resource = 'FEEDBACK' AND p.action IN ('create', 'read', 'update', 'delete'))
   OR
-- Quyền quản lý tài khoản cá nhân
    (p.resource = 'USER' AND p.action IN ('read', 'update', 'disable'));

-- ================================================================================================================== --
-- BẢNG USERS - Định nghĩa các người dùng trong hệ thống
-- ================================================================================================================== --
IF
NOT EXISTS (SELECT 1 FROM users)
INSERT INTO users
(full_name, email_address, phone_number, hashed_password, role_id, status, created_at, update_at)
VALUES
-- 1 Admin (role_id = 1)
(N'Nguyễn An Bình', 'admin@autocare.vn', '0901234567', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 1, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),

-- 7 Staff (role_id = 2)
(N'Trần Minh Tuấn', 'tuan.tm@autocare.vn', '0988123456', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Lê Thị Thu Trang', 'trang.ltt@autocare.vn', '0356789123', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Phạm Văn Hùng', 'hung.pv@autocare.vn', '0912345678', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Hoàng Bích Thủy', 'thuy.hb@autocare.vn', '0902222333', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Vũ Đức Anh', 'anh.vd@autocare.vn', '0905111222', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Đỗ Ngọc Linh', 'linh.dn.inactive@autocare.vn', '0902000006', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Nguyễn Bảo An', 'an.nb.archived@autocare.vn', '0902000007', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 2, 'ARCHIVED', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),

-- 10 Technician (role_id = 3)
(N'Lê Minh Khôi', 'khoi.lm@autocare.vn', '0933111222', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Phan Thanh Tùng', 'tung.pt@autocare.vn', '0933111333', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Võ Quốc Trung', 'trung.vq@autocare.vn', '0933111444', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Trịnh Quang Vinh', 'vinh.tq@autocare.vn', '0933111555', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Đặng Hữu Phước', 'phuoc.dh@autocare.vn', '0933111666', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Ngô Gia Huy', 'huy.ng@autocare.vn', '0933111777', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Bùi Tuấn Kiệt', 'kiet.bt@autocare.vn', '0933111888', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Hồ Minh Đăng', 'dang.hm.inactive@autocare.vn', '0933000008', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Lý Cẩm Ly', 'ly.lc.inactive@autocare.vn', '0933000009', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Mai Văn Sơn', 'son.mv.archived@autocare.vn', '0933000010', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 3, 'ARCHIVED', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),

-- 20 Customer (role_id = 4)
(N'Nguyễn Thu Thảo', 'thao.nguyen@gmail.com', '0868111222', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Trần Văn Nam', 'nam.tran@gmail.com', '0868111333', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Lê Thị Mai', 'mai.le@gmail.com', '0868111444', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Phạm Hùng Dũng', 'dung.pham@gmail.com', '0868111555', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Hoàng Thị Lan Anh', 'lananh.hoang@gmail.com', '0868111666', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Vũ Minh Hiếu', 'hieu.vu@gmail.com', '0868111777', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Đỗ Gia Hân', 'han.do@gmail.com', '0868111888', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Bùi Thanh Tâm', 'tam.bui@gmail.com', '0868111999', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Đặng Yến Nhi', 'nhi.dang@gmail.com', '0868111000', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Hồ Bảo Long', 'long.ho@gmail.com', '0868222111', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Lý Anh Kiệt', 'kiet.ly@gmail.com', '0868222333', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Mai Phương Thúy', 'thuy.mai@gmail.com', '0868222444', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Phan Hoàng Nam', 'nam.phan@gmail.com', '0868222555', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Trịnh Hoài An', 'an.trinh@gmail.com', '0868222666', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Vương Gia Bảo', 'bao.vuong@gmail.com', '0868222777', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Khách Hàng Vãng Lai 1', 'guest.inactive1@gmail.com', '0868000016', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'INACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Khách Hàng Vãng Lai 2', 'guest.inactive2@gmail.com', '0868000017', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'INACTIVE', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Tài Khoản Cũ 1', 'old.archived1@gmail.com', '0868000018', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ARCHIVED', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Tài Khoản Cũ 2', 'old.archived2@gmail.com', '0868000019', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ARCHIVED', '2025-10-01 08:00:00', '2025-10-01 08:00:00'),
(N'Tài Khoản Cũ 3', 'old.archived3@gmail.com', '0868000020', '$2a$10$t1Vgn3jF1I.iIoa.iBIGLe5KVG1mVrIl0zTfs.t6.fVqs32/e3Ute', 4, 'ARCHIVED', '2025-10-01 08:00:00', '2025-10-01 08:00:00');

-- ================================================================================================================== --
-- BẢNG VEHICLES - Định nghĩa các dữ liệu xe hệ của khách hàng
-- ================================================================================================================== --
IF NOT EXISTS (SELECT 1 FROM vehicles)
INSERT INTO vehicles
(name, plate_number, color, vin, customer_id, vehicle_model_id, entity_status, purchased_at, distance_traveled_km, battery_degradation, created_at)
VALUES
-- Nguyễn Thu Thảo (user_id=19) - 2 xe
(N'VF 8', '51K-111.22', N'Đỏ', 'VFVF82AD3PA000101', 19, 5, 'ACTIVE', '2023-01-10', 15000, 98.5, '2023-01-10 08:00:00'),
(N'VF 5 Plus', '51K-111.33', N'Trắng', 'VFVF51BC3PA000102', 19, 2, 'ACTIVE', '2024-02-15', 5000, 99.1, '2024-02-15 08:00:00'),

-- Trần Văn Nam (user_id=20) - 1 xe
(N'VF e34', '51H-222.44', N'Xanh', 'VFVFE34D3PA000201', 20, 7, 'ACTIVE', '2022-05-20', 35000, 95.0, '2022-05-20 08:00:00'),

-- Lê Thị Mai (user_id=21) - 3 xe
(N'VF 9', '51F-333.55', N'Đen', 'VFVF93EF3PA000301', 21, 6, 'ACTIVE', '2023-07-01', 8000, 99.0, '2023-07-01 08:00:00'),
(N'VF 7', '51F-333.66', N'Bạc', 'VFVF71GH3PA000302', 21, 4, 'ACTIVE', '2024-05-01', 2000, 99.8, '2024-05-01 08:00:00'),
(N'VF 3', '51F-333.77', N'Cam', 'VFVF31IJ3PA000303', 21, 1, 'ACTIVE', '2024-09-10', 150, 100.0, '2024-09-10 08:00:00'),

-- Phạm Hùng Dũng (user_id=22) - 1 xe
(N'VF 6', '51G-444.88', N'Xám', 'VFVF62KL3PA000401', 22, 3, 'ACTIVE', '2023-11-11', 12000, 98.0, '2023-11-11 08:00:00'),

-- Hoàng Thị Lan Anh (user_id=23) - 1 xe
(N'VF e34', '51K-555.11', N'Trắng', 'VFVFE34D3PA000501', 23, 7, 'ACTIVE', '2022-10-30', 22000, 97.2, '2022-10-30 08:00:00'),

-- Vũ Minh Hiếu (user_id=24) - 1 xe
(N'VF 8', '51K-555.22', N'Đen', 'VFVF82AD3PA000601', 24, 5, 'ACTIVE', '2023-03-12', 19000, 97.5, '2023-03-12 08:00:00'),

-- Đỗ Gia Hân (user_id=25) - 2 xe
(N'VF 5 Plus', '51H-666.11', N'Bạc', 'VFVF51BC3PA000701', 25, 2, 'ACTIVE', '2023-12-25', 7500, 99.0, '2023-12-25 08:00:00'),
(N'VF 9', '51H-666.22', N'Đỏ', 'VFVF93EF3PA000702', 25, 6, 'INACTIVE', '2024-01-20', 4200, 99.3, '2024-01-20 08:00:00'),

-- Bùi Thanh Tâm (user_id=26) - 1 xe
(N'VF 6', '51F-777.11', N'Xanh', 'VFVF62KL3PA000801', 26, 3, 'ACTIVE', '2023-09-05', 9100, 98.7, '2023-09-05 08:00:00'),

-- Khách Hàng Vãng Lai 1 (user_id=36)
(N'VF 5 Plus', '51K-123.01', N'Trắng', 'VFVF51BC3PA001001', 36, 2, 'ACTIVE', '2023-06-10', 18000, 97.0, '2023-06-10 08:00:00'),

-- Tài Khoản Cũ 1 (user_id=38) - 1 xe (ARCHIVED)
(N'VF e34', '51A-999.00', N'Đen', 'VFVFE34D3PA001101', 38, 7, 'INACTIVE', '2022-01-15', 75000, 88.0, '2022-01-15 08:00:00');

-- ================================================================================================================== --
-- BẢNG BOOKINGS - Định nghĩa các đơn bảo dưỡng xe của khách hàng
-- BẢNG BOOKING DETAILS - Định nghĩa dịch vụ được khách hàng đặt trong đơn
-- BẢNG INVOICES - Định nghĩa các hoá đơn của đơn bảo dưỡng
-- BẢNG INVOICE LINES - Định nghĩa chi tiết các dịch vụ + linh kiện
-- BẢNG JOBS - Định nghĩa các công việc phân công cho kỹ thuật viên
-- BẢNG PAYMENTS - Định nghĩa các giao dịch thanh toán
-- BẢNG BOOKING COMMENTS - Định nghĩa các ghi chú/bình luận của booking
-- ================================================================================================================== --

-- ================================================================
-- Bảng BOOKINGS (25 records)
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM bookings)
INSERT INTO bookings
(customer_id, vin, schedule_date, booking_status, created_at, updated_at)
VALUES
-- === 1. Đã hoàn thành (MAINTENANCE_COMPLETE) - 10 đơn (Quá khứ) ===
(19, 'VFVF82AD3PA000101', DATEADD(HOUR, 9, DATEADD(DAY, -30, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -35, GETDATE()), DATEADD(DAY, -30, GETDATE())),
(19, 'VFVF51BC3PA000102', DATEADD(HOUR, 9, DATEADD(DAY, -25, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -26, GETDATE()), DATEADD(DAY, -25, GETDATE())),
(20, 'VFVFE34D3PA000201', DATEADD(HOUR, 9, DATEADD(DAY, -20, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -22, GETDATE()), DATEADD(DAY, -20, GETDATE())),
(21, 'VFVF93EF3PA000301', DATEADD(HOUR, 9, DATEADD(DAY, -15, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -16, GETDATE()), DATEADD(DAY, -15, GETDATE())),
(21, 'VFVF71GH3PA000302', DATEADD(HOUR, 9, DATEADD(DAY, -10, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -11, GETDATE()), DATEADD(DAY, -10, GETDATE())),
(21, 'VFVF31IJ3PA000303', DATEADD(HOUR, 9, DATEADD(DAY, -7, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -8, GETDATE()), DATEADD(DAY, -7, GETDATE())),
(22, 'VFVF62KL3PA000401', DATEADD(HOUR, 9, DATEADD(DAY, -5, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -5, GETDATE())),
(23, 'VFVFE34D3PA000501', DATEADD(HOUR, 9, DATEADD(DAY, -4, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -4, GETDATE())),
(24, 'VFVF82AD3PA000601', DATEADD(HOUR, 9, DATEADD(DAY, -3, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -3, GETDATE())),
(25, 'VFVF51BC3PA000701', DATEADD(HOUR, 9, DATEADD(DAY, -2, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -2, GETDATE())),

-- === 2. Đang thực hiện (IN_PROGRESS) - 3 đơn (Hôm nay) ===
(19, 'VFVF82AD3PA000101', DATEADD(HOUR, 8, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'IN_PROGRESS', DATEADD(DAY, -2, GETDATE()), GETDATE()),
(20, 'VFVFE34D3PA000201', DATEADD(HOUR, 9, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'IN_PROGRESS', DATEADD(DAY, -3, GETDATE()), GETDATE()),
(21, 'VFVF71GH3PA000302', DATEADD(HOUR, 10, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'IN_PROGRESS', DATEADD(DAY, -2, GETDATE()), GETDATE()),

-- === (MỚI) Đã thanh toán - Chờ đến giờ hẹn (Hôm nay) ===
(26, 'VFVF62KL3PA000801', DATEADD(HOUR, DATEPART(hour, GETDATE()), CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'PAID', DATEADD(DAY, -1, GETDATE()), GETDATE()),

-- === 3. Đã xác nhận (CONFIRMED) - 5 đơn (Tương lai gần) ===
(21, 'VFVF93EF3PA000301', DATEADD(HOUR, 9, DATEADD(DAY, 1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CONFIRMED', DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE())),
(19, 'VFVF51BC3PA000102', DATEADD(HOUR, 14, DATEADD(DAY, 1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CONFIRMED', DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE())),
(24, 'VFVF82AD3PA000601', DATEADD(HOUR, 9, DATEADD(DAY, 2, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CONFIRMED', DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE())),
(25, 'VFVF51BC3PA000701', DATEADD(HOUR, 15, DATEADD(DAY, 2, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CONFIRMED', GETDATE(), GETDATE()),
(26, 'VFVF62KL3PA000801', DATEADD(HOUR, 9, DATEADD(DAY, 3, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CONFIRMED', GETDATE(), GETDATE()),

-- === 4. Đang chờ (PENDING) - 4 đơn (Tương lai xa) ===
(22, 'VFVF62KL3PA000401', DATEADD(HOUR, 9, DATEADD(DAY, 4, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'PENDING', GETDATE(), GETDATE()),
(23, 'VFVFE34D3PA000501', DATEADD(HOUR, 9, DATEADD(DAY, 5, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'PENDING', GETDATE(), GETDATE()),
(36, 'VFVF51BC3PA001001', DATEADD(HOUR, 9, DATEADD(DAY, 6, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'PENDING', GETDATE(), GETDATE()),
(38, 'VFVFE34D3PA001101', DATEADD(HOUR, 9, DATEADD(DAY, 7, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'PENDING', GETDATE(), GETDATE()),

-- === 5. Đã hủy (CANCELLED) - 3 đơn ===
(23, 'VFVFE34D3PA000501', DATEADD(HOUR, 9, DATEADD(DAY, -1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CANCELLED', DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -1, GETDATE())),
(25, 'VFVF93EF3PA000702', DATEADD(HOUR, 14, DATEADD(DAY, -1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CANCELLED', DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -1, GETDATE())),
(26, 'VFVF62KL3PA000801', DATEADD(HOUR, 9, DATEADD(DAY, -10, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'CANCELLED', DATEADD(DAY, -15, GETDATE()), DATEADD(DAY, -11, GETDATE()));

-- ================================================================
-- Bảng BOOKING_DETAILS
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM booking_details)
INSERT INTO booking_details
(booking_id, maintenance_catalog_model_id, description)
VALUES
-- === 1. MAINTENANCE_COMPLETE (Bookings 1-10) ===
(1, 5, N'Kiểm tra pin'), (1, 12, N'Đảo lốp'), (1, 33, N'Thay lọc gió cabin'), (1, 103, N'Bổ sung nước rửa kính'),
(2, 9, N'Đảo lốp'), (2, 16, N'Kiểm tra hệ thống phanh'),
(3, 42, N'Dịch vụ hệ thống làm mát'), (3, 56, N'Cân chỉnh bánh xe'), (3, 70, N'Thay dầu phanh'),
(4, 48, N'Kiểm tra hệ thống treo'), (4, 76, N'Kiểm tra ắc quy 12V'),
(5, 18, N'Kiểm tra hệ thống phanh'), (5, 95, N'Thay gạt mưa'),
(6, 8, N'Đảo lốp'), (6, 99, N'Bổ sung nước rửa kính'), (6, 71, N'Kiểm tra ắc quy 12V'),
(7, 87, N'Bảo dưỡng hệ thống điều hòa'), (7, 31, N'Thay lọc gió cabin'),
(8, 84, N'Thay thế ắc quy 12V'), (8, 28, N'Cập nhật phần mềm'),
(9, 68, N'Thay dầu phanh'), (9, 33, N'Thay lọc gió cabin'), (9, 47, N'Kiểm tra hệ thống treo'),
(10, 16, N'Kiểm tra hệ thống phanh'), (10, 100, N'Bổ sung nước rửa kính'),

-- === 2. IN_PROGRESS (Bookings 11-13) ===
(11, 61, N'Chẩn đoán tổng thể xe'), (11, 26, N'Cập nhật phần mềm'), (11, 75, N'Kiểm tra ắc quy 12V'), (11, 96, N'Thay gạt mưa'),
(12, 7, N'Kiểm tra pin điện áp cao'), (12, 35, N'Thay lọc gió cabin'),
(13, 95, N'Thay gạt mưa'), (13, 102, N'Bổ sung nước rửa kính'), (13, 11, N'Đảo lốp'),

-- === 3. CONFIRMED (Bookings 14-18) ===
(14, 34, N'Thay lọc gió cabin'), (14, 76, N'Kiểm tra ắc quy 12V'), (14, 13, N'Đảo lốp'), (14, 48, N'Kiểm tra hệ thống treo'),
(15, 9, N'Đảo lốp'), (15, 16, N'Kiểm tra hệ thống phanh'),
(16, 47, N'Kiểm tra hệ thống treo'), (16, 103, N'Bổ sung nước rửa kính'),
(17, 51, N'Cân chỉnh góc đặt bánh xe'), (17, 30, N'Thay lọc gió cabin'),
(18, 87, N'Bảo dưỡng hệ thống điều hòa'), (18, 66, N'Thay dầu phanh'),

-- === (MỚI) PAID chờ đến giờ (Booking 22) ===
(22, 15, N'Kiểm tra pin điện áp cao'), (22, 31, N'Thay lọc gió cabin'),

-- === 4. PENDING (Bookings 19-21, 23) ===
(19, 66, N'Thay dầu phanh'), (19, 17, N'Kiểm tra hệ thống phanh'),
(20, 7, N'Kiểm tra pin điện áp cao'), (20, 105, N'Bổ sung nước rửa kính'), (20, 77, N'Kiểm tra ắc quy 12V'),
(21, 9, N'Đảo lốp'), (21, 2, N'Kiểm tra pin điện áp cao'),
(23, 84, N'Thay thế ắc quy 12V'), (23, 63, N'Chẩn đoán tổng thể xe'),

-- === 5. CANCELLED (Bookings 24-26) ===
(24, 7, N'Kiểm tra pin'), (24, 77, N'Kiểm tra ắc quy 12V'),
(25, 55, N'Cân chỉnh góc đặt bánh xe'),
(26, 17, N'Kiểm tra hệ thống phanh'), (26, 10, N'Đảo lốp');

-- ================================================================
-- Bảng INVOICES (25 records)
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM invoices)
INSERT INTO invoices
(booking_id, invoice_number, issue_date, due_date, total_amount, status, created_at, updated_at, paid_at)
VALUES
-- === MAINTENANCE_COMPLETE (1-10) -> PAID ===
(1, 'INV-2025-00001', DATEADD(DAY, -30, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -30, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 1080000, 'PAID', DATEADD(DAY, -35, GETDATE()), DATEADD(DAY, -30, GETDATE()), DATEADD(DAY, -30, GETDATE())),
(2, 'INV-2025-00002', DATEADD(DAY, -25, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -25, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 300000, 'PAID', DATEADD(DAY, -26, GETDATE()), DATEADD(DAY, -25, GETDATE()), DATEADD(DAY, -25, GETDATE())),
(3, 'INV-2025-00003', DATEADD(DAY, -20, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -20, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 1880000, 'PAID', DATEADD(DAY, -22, GETDATE()), DATEADD(DAY, -20, GETDATE()), DATEADD(DAY, -20, GETDATE())),
(4, 'INV-2025-00004', DATEADD(DAY, -15, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -15, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 450000, 'PAID', DATEADD(DAY, -16, GETDATE()), DATEADD(DAY, -15, GETDATE()), DATEADD(DAY, -15, GETDATE())),
(5, 'INV-2025-00005', DATEADD(DAY, -10, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -10, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 610000, 'PAID', DATEADD(DAY, -11, GETDATE()), DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -10, GETDATE())),
(6, 'INV-2025-00006', DATEADD(DAY, -7, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -7, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 300000, 'PAID', DATEADD(DAY, -8, GETDATE()), DATEADD(DAY, -7, GETDATE()), DATEADD(DAY, -7, GETDATE())),
(7, 'INV-2025-00007', DATEADD(DAY, -5, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -5, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 720000, 'PAID', DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -5, GETDATE())),
(8, 'INV-2025-00008', DATEADD(DAY, -4, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -4, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 1900000, 'PAID', DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -4, GETDATE())),
(9, 'INV-2025-00009', DATEADD(DAY, -3, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -3, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 1340000, 'PAID', DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -3, GETDATE())),
(10, 'INV-2025-00010', DATEADD(DAY, -2, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -2, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 200000, 'PAID', DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, -2, GETDATE())),

-- === IN_PROGRESS (11-13) -> PAID ===
(11, 'INV-2025-00011', GETDATE(), DATEADD(HOUR, 8, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 1030000, 'PAID', DATEADD(DAY, -2, GETDATE()), GETDATE(), GETDATE()),
(12, 'INV-2025-00012', GETDATE(), DATEADD(HOUR, 9, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 500000, 'PAID', DATEADD(DAY, -3, GETDATE()), GETDATE(), GETDATE()),
(13, 'INV-2025-00013', GETDATE(), DATEADD(HOUR, 10, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 660000, 'PAID', DATEADD(DAY, -2, GETDATE()), GETDATE(), GETDATE()),

-- === CONFIRMED (14-18) -> UNPAID ===
(14, 'INV-2025-00014', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, 1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 1080000, 'UNPAID', DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE()), NULL),
(15, 'INV-2025-00015', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, 14, DATEADD(DAY, 1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 300000, 'UNPAID', DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE()), NULL),
(16, 'INV-2025-00016', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, 2, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 350000, 'UNPAID', DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE()), NULL),
(17, 'INV-2025-00017', GETDATE(), DATEADD(HOUR, 15, DATEADD(DAY, 2, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 600000, 'UNPAID', GETDATE(), GETDATE(), NULL),
(18, 'INV-2025-00018', GETDATE(), DATEADD(HOUR, 9, DATEADD(DAY, 3, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 880000, 'UNPAID', GETDATE(), GETDATE(), NULL),

-- === (MỚI) PAID chờ đến giờ (22) -> PAID ===
(22, 'INV-2025-00022', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, DATEPART(HOUR, GETDATE()) + 2, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 570000, 'PAID', DATEADD(DAY, -1, GETDATE()), GETDATE(), GETDATE()),

-- === PENDING (19-21, 23) -> DRAFT ===
(19, 'INV-2025-00019', GETDATE(), DATEADD(HOUR, 9, DATEADD(DAY, 4, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 610000, 'DRAFT', GETDATE(), GETDATE(), NULL),
(20, 'INV-2025-00020', GETDATE(), DATEADD(HOUR, 9, DATEADD(DAY, 5, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 450000, 'DRAFT', GETDATE(), GETDATE(), NULL),
(21, 'INV-2025-00021', GETDATE(), DATEADD(HOUR, 9, DATEADD(DAY, 6, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 450000, 'DRAFT', GETDATE(), GETDATE(), NULL),
(23, 'INV-2025-00023', GETDATE(), DATEADD(HOUR, 9, DATEADD(DAY, 7, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 2200000, 'DRAFT', GETDATE(), GETDATE(), NULL),

-- === CANCELLED (24-26) -> CANCELLED ===
(24, 'INV-2025-00024', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 400000, 'CANCELLED', DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -1, GETDATE()), NULL),
(25, 'INV-2025-00025', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, 14, DATEADD(DAY, -1, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 700000, 'CANCELLED', DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -1, GETDATE()), NULL),
(26, 'INV-2025-00026', DATEADD(DAY, -11, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -10, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 360000, 'CANCELLED', DATEADD(DAY, -15, GETDATE()), DATEADD(DAY, -11, GETDATE()), NULL);

-- ================================================================
-- Bảng INVOICE_LINES
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM invoice_lines)
INSERT INTO invoice_lines
(invoice_id, item_description, item_type, quantity, unit_price, line_total)
VALUES
-- Invoice 1
(1, N'DV: Kiểm tra pin điện áp cao', 'SERVICE', 1, 450000, 450000),
(1, N'DV: Đảo lốp', 'SERVICE', 1, 200000, 200000),
(1, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 80000, 80000),
(1, N'LK: Lọc gió cabin (Loại L)', 'PART', 1, 300000, 300000),
(1, N'DV: Bổ sung nước rửa kính', 'SERVICE', 1, 0, 0),
(1, N'LK: Nước rửa kính (2L)', 'PART', 1, 50000, 50000),
-- Invoice 2
(2, N'DV: Đảo lốp', 'SERVICE', 1, 150000, 150000),
(2, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 150000, 150000),
-- Invoice 3
(3, N'DV: Dịch vụ hệ thống làm mát pin', 'SERVICE', 1, 350000, 350000),
(3, N'LK: Nước làm mát pin (EV Coolant) (5L)', 'PART', 1, 750000, 750000),
(3, N'DV: Cân chỉnh góc đặt bánh xe', 'SERVICE', 1, 400000, 400000),
(3, N'DV: Thay dầu phanh', 'SERVICE', 1, 200000, 200000),
(3, N'LK: Dầu phanh DOT 4 (1L)', 'PART', 1, 180000, 180000),
-- Invoice 4
(4, N'DV: Kiểm tra hệ thống treo', 'SERVICE', 1, 350000, 350000),
(4, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000),
-- Invoice 5
(5, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 180000, 180000),
(5, N'DV: Thay gạt mưa', 'SERVICE', 1, 50000, 50000),
(5, N'LK: Gạt mưa Bosch 24"', 'PART', 1, 180000, 180000),
(5, N'LK: Gạt mưa Bosch 26"', 'PART', 1, 200000, 200000),
-- Invoice 6
(6, N'DV: Đảo lốp', 'SERVICE', 1, 150000, 150000),
(6, N'DV: Bổ sung nước rửa kính', 'SERVICE', 1, 0, 0),
(6, N'LK: Nước rửa kính (2L)', 'PART', 1, 50000, 50000),
(6, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000),
-- Invoice 7
(7, N'DV: Bảo dưỡng hệ thống điều hòa', 'SERVICE', 1, 450000, 450000),
(7, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 50000, 50000),
(7, N'LK: Lọc gió cabin (Loại M)', 'PART', 1, 220000, 220000),
-- Invoice 8
(8, N'DV: Thay thế ắc quy 12V', 'SERVICE', 1, 100000, 100000),
(8, N'LK: Ắc quy 12V 45Ah (AGM)', 'PART', 1, 1800000, 1800000),
(8, N'DV: Cập nhật phần mềm', 'SERVICE', 1, 0, 0),
-- Invoice 9
(9, N'DV: Thay dầu phanh', 'SERVICE', 1, 300000, 300000),
(9, N'LK: Dầu phanh DOT 4 (1L)', 'PART', 2, 180000, 360000),
(9, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 80000, 80000),
(9, N'LK: Lọc gió cabin (Loại L)', 'PART', 1, 300000, 300000),
(9, N'DV: Kiểm tra hệ thống treo', 'SERVICE', 1, 300000, 300000),
-- Invoice 10
(10, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 150000, 150000),
(10, N'DV: Bổ sung nước rửa kính', 'SERVICE', 1, 0, 0),
(10, N'LK: Nước rửa kính (2L)', 'PART', 1, 50000, 50000),
-- Invoice 11
(11, N'DV: Chẩn đoán tổng thể xe', 'SERVICE', 1, 500000, 500000),
(11, N'DV: Cập nhật phần mềm', 'SERVICE', 1, 0, 0),
(11, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000),
(11, N'DV: Thay gạt mưa', 'SERVICE', 1, 50000, 50000),
(11, N'LK: Gạt mưa Bosch 24"', 'PART', 1, 180000, 180000),
(11, N'LK: Gạt mưa Bosch 26"', 'PART', 1, 200000, 200000),
-- Invoice 12
(12, N'DV: Kiểm tra pin điện áp cao', 'SERVICE', 1, 300000, 300000),
(12, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 50000, 50000),
(12, N'LK: Lọc gió cabin (Loại S)', 'PART', 1, 150000, 150000),
-- Invoice 13
(13, N'DV: Thay gạt mưa', 'SERVICE', 1, 50000, 50000),
(13, N'LK: Gạt mưa Bosch 24"', 'PART', 1, 180000, 180000),
(13, N'LK: Gạt mưa Bosch 26"', 'PART', 1, 200000, 200000),
(13, N'DV: Bổ sung nước rửa kính', 'SERVICE', 1, 0, 0),
(13, N'LK: Nước rửa kính (2L)', 'PART', 1, 50000, 50000),
(13, N'DV: Đảo lốp', 'SERVICE', 1, 180000, 180000),
-- Invoice 14
(14, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 80000, 80000),
(14, N'LK: Lọc gió cabin (Loại L)', 'PART', 1, 300000, 300000),
(14, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000),
(14, N'DV: Đảo lốp', 'SERVICE', 1, 250000, 250000),
(14, N'DV: Kiểm tra hệ thống treo', 'SERVICE', 1, 350000, 350000),
-- Invoice 15
(15, N'DV: Đảo lốp', 'SERVICE', 1, 150000, 150000),
(15, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 150000, 150000),
-- Invoice 16
(16, N'DV: Kiểm tra hệ thống treo', 'SERVICE', 1, 300000, 300000),
(16, N'DV: Bổ sung nước rửa kính', 'SERVICE', 1, 0, 0),
(16, N'LK: Nước rửa kính (2L)', 'PART', 1, 50000, 50000),
-- Invoice 17
(17, N'DV: Cân chỉnh góc đặt bánh xe', 'SERVICE', 1, 400000, 400000),
(17, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 50000, 50000),
(17, N'LK: Lọc gió cabin (Loại S)', 'PART', 1, 150000, 150000),
-- Invoice 18
(18, N'DV: Bảo dưỡng hệ thống điều hòa', 'SERVICE', 1, 450000, 450000),
(18, N'DV: Thay dầu phanh', 'SERVICE', 1, 250000, 250000),
(18, N'LK: Dầu phanh DOT 4 (1L)', 'PART', 1, 180000, 180000),
-- Invoice 19
(19, N'DV: Thay dầu phanh', 'SERVICE', 1, 250000, 250000),
(19, N'LK: Dầu phanh DOT 4 (1L)', 'PART', 1, 180000, 180000),
(19, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 180000, 180000),
-- Invoice 20
(20, N'DV: Kiểm tra pin điện áp cao', 'SERVICE', 1, 300000, 300000),
(20, N'DV: Bổ sung nước rửa kính', 'SERVICE', 1, 0, 0),
(20, N'LK: Nước rửa kính (2L)', 'PART', 1, 50000, 50000),
(20, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000),
-- Invoice 21
(21, N'DV: Đảo lốp', 'SERVICE', 1, 150000, 150000),
(21, N'DV: Kiểm tra pin điện áp cao', 'SERVICE', 1, 300000, 300000),
-- Invoice 22 (MỚI - PAID chờ đến giờ)
(22, N'DV: Kiểm tra pin điện áp cao', 'SERVICE', 1, 300000, 300000),
(22, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 50000, 50000),
(22, N'LK: Lọc gió cabin (Loại M)', 'PART', 1, 220000, 220000),
-- Invoice 23
(23, N'DV: Thay thế ắc quy 12V', 'SERVICE', 1, 100000, 100000),
(23, N'LK: Ắc quy 12V 45Ah (AGM)', 'PART', 1, 1800000, 1800000),
(23, N'DV: Chẩn đoán tổng thể xe', 'SERVICE', 1, 300000, 300000),
-- Invoice 24 (CANCELLED)
(24, N'DV: Kiểm tra pin', 'SERVICE', 1, 300000, 300000), 
(24, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000),
-- Invoice 25 (CANCELLED)
(25, N'DV: Cân chỉnh góc đặt bánh xe', 'SERVICE', 1, 700000, 700000),
-- Invoice 26 (CANCELLED)
(26, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 180000, 180000),
(26, N'DV: Đảo lốp', 'SERVICE', 1, 180000, 180000);

-- ================================================================
-- Bảng JOBS (13 records)
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM jobs)
INSERT INTO jobs
(booking_id, technician_id, start_time, est_end_time, actual_end_time, notes, created_at, updated_at)
VALUES
-- === 10 JOBS cho MAINTENANCE_COMPLETE (Bookings 1-10) ===
(1, 9, DATEADD(HOUR, 9, DATEADD(DAY, -30, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -30, GETDATE())), DATEADD(MINUTE, 150, DATEADD(HOUR, 9, DATEADD(DAY, -30, GETDATE()))), N'Hoàn thành bảo dưỡng đầy đủ', DATEADD(DAY, -35, GETDATE()), DATEADD(DAY, -30, GETDATE())),
(2, 10, DATEADD(HOUR, 9, DATEADD(DAY, -25, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -25, GETDATE())), DATEADD(MINUTE, 180, DATEADD(HOUR, 9, DATEADD(DAY, -25, GETDATE()))), N'Hoàn thành đảo lốp và kiểm tra phanh', DATEADD(DAY, -26, GETDATE()), DATEADD(DAY, -25, GETDATE())),
(3, 11, DATEADD(HOUR, 9, DATEADD(DAY, -20, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -20, GETDATE())), DATEADD(MINUTE, 135, DATEADD(HOUR, 9, DATEADD(DAY, -20, GETDATE()))), N'Hoàn thành bảo dưỡng hệ thống làm mát', DATEADD(DAY, -22, GETDATE()), DATEADD(DAY, -20, GETDATE())),
(4, 12, DATEADD(HOUR, 9, DATEADD(DAY, -15, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -15, GETDATE())), DATEADD(MINUTE, 150, DATEADD(HOUR, 9, DATEADD(DAY, -15, GETDATE()))), N'Hoàn thành kiểm tra hệ thống treo', DATEADD(DAY, -16, GETDATE()), DATEADD(DAY, -15, GETDATE())),
(5, 13, DATEADD(HOUR, 9, DATEADD(DAY, -10, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -10, GETDATE())), DATEADD(MINUTE, 180, DATEADD(HOUR, 9, DATEADD(DAY, -10, GETDATE()))), N'Hoàn thành thay gạt mưa và kiểm tra', DATEADD(DAY, -11, GETDATE()), DATEADD(DAY, -10, GETDATE())),
(6, 14, DATEADD(HOUR, 9, DATEADD(DAY, -7, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -7, GETDATE())), DATEADD(MINUTE, 135, DATEADD(HOUR, 9, DATEADD(DAY, -7, GETDATE()))), N'Hoàn thành bảo dưỡng cơ bản', DATEADD(DAY, -8, GETDATE()), DATEADD(DAY, -7, GETDATE())),
(7, 15, DATEADD(HOUR, 9, DATEADD(DAY, -5, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -5, GETDATE())), DATEADD(MINUTE, 150, DATEADD(HOUR, 9, DATEADD(DAY, -5, GETDATE()))), N'Hoàn thành bảo dưỡng điều hòa', DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -5, GETDATE())),
(8, 16, DATEADD(HOUR, 9, DATEADD(DAY, -4, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -4, GETDATE())), DATEADD(MINUTE, 180, DATEADD(HOUR, 9, DATEADD(DAY, -4, GETDATE()))), N'Hoàn thành thay ắc quy 12V', DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -4, GETDATE())),
(9, 17, DATEADD(HOUR, 9, DATEADD(DAY, -3, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -3, GETDATE())), DATEADD(MINUTE, 135, DATEADD(HOUR, 9, DATEADD(DAY, -3, GETDATE()))), N'Hoàn thành thay dầu phanh và lọc gió', DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -3, GETDATE())),
(10, 18, DATEADD(HOUR, 9, DATEADD(DAY, -2, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -2, GETDATE())), DATEADD(MINUTE, 150, DATEADD(HOUR, 9, DATEADD(DAY, -2, GETDATE()))), N'Hoàn thành kiểm tra phanh', DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -2, GETDATE())),

-- === 3 JOBS cho IN_PROGRESS (Bookings 11-13) ===
(11, 9, DATEADD(HOUR, 8, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), DATEADD(HOUR, 10, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), NULL, N'Đang thực hiện chẩn đoán tổng thể hệ thống điện', DATEADD(HOUR, 8, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), DATEADD(HOUR, 8, CAST(CAST(GETDATE() AS DATE) AS DATETIME))),
(12, 10, DATEADD(HOUR, 9, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), DATEADD(HOUR, 11, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), NULL, N'Đang kiểm tra pin, SoH hiện tại 97.2%, dự kiến hoàn thành 11:00', DATEADD(HOUR, 9, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), DATEADD(HOUR, 9, CAST(CAST(GETDATE() AS DATE) AS DATETIME))),
(13, 11, DATEADD(HOUR, 10, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), DATEADD(HOUR, 12, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), NULL, N'Đang thực hiện thay gạt mưa và đảo lốp', DATEADD(HOUR, 10, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), DATEADD(HOUR, 10, CAST(CAST(GETDATE() AS DATE) AS DATETIME)));

-- ================================================================
-- Bảng PAYMENTS (14 records - ĐÃ SỬA + THÊM BOOKING 22)
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM payments)
INSERT INTO payments
(invoice_id, payment_method, amount, status, order_code, transaction_ref, response_code, paid_at, raw_response_data, created_at, updated_at)
VALUES
-- === PAID Invoices (1-13 + 22) -> SUCCESSFUL ===
(1, 'VNPAY', 1080000.00, 'SUCCESSFUL', 'PAY-INV-00001', '14523456', '00', DATEADD(DAY, -30, GETDATE()), N'{"vnp_Amount":"108000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -30, GETDATE()), DATEADD(DAY, -30, GETDATE())),
(2, 'VNPAY', 300000.00, 'SUCCESSFUL', 'PAY-INV-00002', '14523789', '00', DATEADD(DAY, -25, GETDATE()), N'{"vnp_Amount":"30000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -25, GETDATE()), DATEADD(DAY, -25, GETDATE())),
(3, 'VNPAY', 1880000.00, 'SUCCESSFUL', 'PAY-INV-00003', '14524001', '00', DATEADD(DAY, -20, GETDATE()), N'{"vnp_Amount":"188000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -20, GETDATE()), DATEADD(DAY, -20, GETDATE())),
(4, 'VNPAY', 450000.00, 'SUCCESSFUL', 'PAY-INV-00004', '14525123', '00', DATEADD(DAY, -15, GETDATE()), N'{"vnp_Amount":"45000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -15, GETDATE()), DATEADD(DAY, -15, GETDATE())),
(5, 'VNPAY', 610000.00, 'SUCCESSFUL', 'PAY-INV-00005', '14526456', '00', DATEADD(DAY, -10, GETDATE()), N'{"vnp_Amount":"61000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -10, GETDATE())),
(6, 'VNPAY', 300000.00, 'SUCCESSFUL', 'PAY-INV-00006', '14527789', '00', DATEADD(DAY, -7, GETDATE()), N'{"vnp_Amount":"30000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -7, GETDATE()), DATEADD(DAY, -7, GETDATE())),
(7, 'VNPAY', 720000.00, 'SUCCESSFUL', 'PAY-INV-00007', '14528012', '00', DATEADD(DAY, -5, GETDATE()), N'{"vnp_Amount":"72000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -5, GETDATE())),
(8, 'VNPAY', 1900000.00, 'SUCCESSFUL', 'PAY-INV-00008', '14529345', '00', DATEADD(DAY, -4, GETDATE()), N'{"vnp_Amount":"190000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -4, GETDATE())),
(9, 'VNPAY', 1340000.00, 'SUCCESSFUL', 'PAY-INV-00009', '14530678', '00', DATEADD(DAY, -3, GETDATE()), N'{"vnp_Amount":"134000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -3, GETDATE())),
(10, 'VNPAY', 200000.00, 'SUCCESSFUL', 'PAY-INV-00010', '14530901', '00', DATEADD(DAY, -2, GETDATE()), N'{"vnp_Amount":"20000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, -2, GETDATE())),
(11, 'VNPAY', 1030000.00, 'SUCCESSFUL', 'PAY-INV-00011', '14531234', '00', GETDATE(), N'{"vnp_Amount":"103000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE()),
(12, 'VNPAY', 500000.00, 'SUCCESSFUL', 'PAY-INV-00012', '14531567', '00', GETDATE(), N'{"vnp_Amount":"50000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE()),
(13, 'VNPAY', 660000.00, 'SUCCESSFUL', 'PAY-INV-00013', '14531890', '00', GETDATE(), N'{"vnp_Amount":"66000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE()),
(22, 'VNPAY', 570000.00, 'SUCCESSFUL', 'PAY-INV-00022', '14532123', '00', GETDATE(), N'{"vnp_Amount":"57000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE());

-- ================================================================
-- Bảng FEEDBACK TAGS
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM feedback_tags)
INSERT INTO feedback_tags (content, target_rating)
VALUES
-- Tag cho 1 Sao (Tiêu cực)
(N'Thái độ phục vụ tệ', 1),     -- ID 1
(N'Thời gian chờ quá lâu', 1),   -- ID 2
(N'Chất lượng sửa chữa kém', 1), -- ID 3

-- Tag cho 2 Sao (Kém)
(N'Vệ sinh xe chưa sạch', 2),    -- ID 4
(N'Giá dịch vụ quá cao', 2),     -- ID 5
(N'Phụ tùng không có sẵn', 2),   -- ID 6

-- Tag cho 3 Sao (Trung bình)
(N'Dịch vụ tạm ổn', 3),          -- ID 7
(N'Cần cải thiện quy trình', 3), -- ID 8
(N'Không gian chờ hơi hẹp', 3),  -- ID 9

-- Tag cho 4 Sao (Tốt)
(N'Nhân viên nhiệt tình', 4),    -- ID 10
(N'Tư vấn chuyên nghiệp', 4),    -- ID 11
(N'Quy trình nhanh gọn', 4),     -- ID 12

-- Tag cho 5 Sao (Tuyệt vời)
(N'Tuyệt vời', 5),               -- ID 13
(N'Xe chạy êm ái hơn hẳn', 5),   -- ID 14
(N'Giá cả hợp lý', 5),           -- ID 15
(N'Cơ sở vật chất tốt', 5);      -- ID 16

-- ================================================================
-- Bảng FEEDBACKS
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM feedbacks)
INSERT INTO feedbacks (booking_id, customer_id, rating, comment, created_at)
VALUES
-- Booking 2 (Khách 19 - Nguyễn Thu Thảo): 4 Sao
(2, 19, 4, N'Nhân viên nhiệt tình, nhưng chờ thanh toán hơi lâu một chút.', DATEADD(DAY, -25, GETDATE())),

-- Booking 3 (Khách 20 - Trần Văn Nam): 5 Sao
(3, 20, 5, N'Cân chỉnh bánh xe xong đi cao tốc rất đầm. Tuyệt vời!', DATEADD(DAY, -20, GETDATE())),

-- Booking 4 (Khách 21 - Lê Thị Mai): 3 Sao (Hơi khó tính)
(4, 21, 3, N'Bảo dưỡng ổn nhưng phòng chờ hôm nay điều hòa hơi nóng.', DATEADD(DAY, -15, GETDATE())),

-- Booking 5 (Khách 21 - Lê Thị Mai): 5 Sao
(5, 21, 5, N'Thay gạt mưa nhanh, hàng chính hãng nhìn xịn sò.', DATEADD(DAY, -10, GETDATE())),

-- Booking 6 (Khách 21 - Lê Thị Mai): 4 Sao
(6, 21, 4, N'Mọi thứ đều ổn, kỹ thuật viên giải thích kỹ.', DATEADD(DAY, -7, GETDATE())),

-- Booking 7 (Khách 22 - Phạm Hùng Dũng): 5 Sao
(7, 22, 5, N'Lần đầu làm dịch vụ ở đây, rất hài lòng về cách phục vụ.', DATEADD(DAY, -5, GETDATE())),

-- Booking 8 (Khách 23 - Hoàng Thị Lan Anh): 1 Sao (Phàn nàn)
(8, 23, 1, N'Hẹn 9h mà 9h30 mới được làm. Rất thất vọng về thời gian.', DATEADD(DAY, -4, GETDATE())),

-- Booking 9 (Khách 24 - Vũ Minh Hiếu): 5 Sao
(9, 24, 5, N'Xe bảo dưỡng xong sạch sẽ, thơm tho. 10 điểm.', DATEADD(DAY, -3, GETDATE())),

-- Booking 10 (Khách 25 - Đỗ Gia Hân): 2 Sao (Phàn nàn nhẹ)
(10, 25, 2, N'Giá công thợ hơi cao so với mặt bằng chung.', DATEADD(DAY, -2, GETDATE()));

-- ================================================================
-- Bảng FEEDBACK TAG MAPPING
-- ================================================================
IF NOT EXISTS (SELECT 1 FROM feedback_tag_mapping)
INSERT INTO feedback_tag_mapping (feedback_id, tag_id)
VALUES
-- Feedback 1 (thuộc Booking 2 - 4 Sao) -> Chọn tag 4 sao
(1, 10), -- Nhân viên nhiệt tình
(1, 12), -- Quy trình nhanh gọn

-- Feedback 2 (thuộc Booking 3 - 5 Sao) -> Chọn tag 5 sao
(2, 13), -- Tuyệt vời
(2, 15), -- Giá cả hợp lý

-- Feedback 3 (thuộc Booking 4 - 3 Sao) -> Chọn tag 3 sao
(3, 7),  -- Dịch vụ tạm ổn
(3, 9),  -- Không gian chờ hơi hẹp

-- Feedback 4 (thuộc Booking 5 - 5 Sao) -> Chọn tag 5 sao
(4, 15), -- Giá cả hợp lý
(4, 16), -- Cơ sở vật chất tốt

-- Feedback 5 (thuộc Booking 6 - 4 Sao) -> Chọn tag 4 sao
(5, 11), -- Tư vấn chuyên nghiệp

-- Feedback 6 (thuộc Booking 7 - 5 Sao) -> Chọn tag 5 sao
(6, 13), -- Tuyệt vời
(6, 14), -- Xe chạy êm ái hơn hẳn
(6, 16), -- Cơ sở vật chất tốt

-- Feedback 7 (thuộc Booking 8 - 1 Sao) -> Chọn tag 1 sao
(7, 2),  -- Thời gian chờ quá lâu
(7, 1),  -- Thái độ phục vụ tệ

-- Feedback 8 (thuộc Booking 9 - 5 Sao) -> Chọn tag 5 sao
(8, 13), -- Tuyệt vời
(8, 16), -- Cơ sở vật chất tốt

-- Feedback 9 (thuộc Booking 10 - 2 Sao) -> Chọn tag 2 sao
(9, 5);  -- Giá dịch vụ quá cao

-- ================================================================
-- ADDITIONAL DATA FOR REMINDER TESTING
-- ================================================================
-- Test Vehicle: Should trigger reminder
-- Last maintenance: 50 days ago at 8,500 km
-- Predicted: 8,500 + (50 × 40) = 10,500 km
-- Next threshold: 10,000 km (already passed, should look for 20,000 km)
-- Notify at: 18,000 km (20,000 - 2,000)
-- Should trigger: NO (10,500 < 18,000)

-- Let's create a better test case that WILL trigger:
-- Last maintenance: 100 days ago at 14,000 km
-- Predicted: 14,000 + (100 × 40) = 18,000 km
-- Next threshold: 20,000 km
-- Notify at: 18,000 km (20,000 - 2,000)
-- Should trigger: YES (18,000 >= 18,000)

   IF
NOT EXISTS (SELECT 1 FROM vehicles WHERE vin = 'VFVF51TS3PA000001')
INSERT INTO vehicles
(name, plate_number, color, vin, customer_id, vehicle_model_id, entity_status, purchased_at, distance_traveled_km, battery_degradation, created_at)
VALUES
(N'VF 5 Test Reminder', '51K-001.01', N'Đỏ', 'VFVF51TS3PA000001', 19, 2, 'ACTIVE', '2024-01-01', 14000, 99.0, GETDATE());

-- Booking for test vehicle (100 days ago) - This will be booking ID 27
      IF
NOT EXISTS (SELECT 1 FROM bookings WHERE vin = 'VFVF51TS3PA000001')
INSERT INTO bookings
(customer_id, vin, schedule_date, booking_status, created_at, updated_at)
VALUES
(19, 'VFVF51TS3PA000001', DATEADD(HOUR, 9, DATEADD(DAY, -100, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 'MAINTENANCE_COMPLETE', DATEADD(DAY, -101, GETDATE()), DATEADD(DAY, -100, GETDATE()));

-- Booking details for booking 27
         IF
NOT EXISTS (SELECT 1 FROM booking_details WHERE booking_id = 27)
INSERT INTO booking_details
(booking_id, maintenance_catalog_model_id, description)
VALUES
(27, 9, N'Đảo lốp'),
(27, 16, N'Kiểm tra hệ thống phanh');

-- Invoice for booking 27
            IF
NOT EXISTS (SELECT 1 FROM invoices WHERE booking_id = 27)
INSERT INTO invoices
(booking_id, invoice_number, issue_date, due_date, total_amount, status, created_at, updated_at, paid_at)
VALUES
(27, 'INV-2025-TEST01', DATEADD(DAY, -100, GETDATE()), DATEADD(HOUR, 9, DATEADD(DAY, -100, CAST(CAST(GETDATE() AS DATE) AS DATETIME))), 300000, 'PAID', DATEADD(DAY, -101, GETDATE()), DATEADD(DAY, -100, GETDATE()), DATEADD(DAY, -100, GETDATE()));

-- Invoice lines for invoice 27
               IF
NOT EXISTS (SELECT 1 FROM invoice_lines WHERE invoice_id = 27)
INSERT INTO invoice_lines
(invoice_id, item_description, item_type, quantity, unit_price, line_total)
VALUES
(27, N'DV: Đảo lốp', 'SERVICE', 1, 150000, 150000),
(27, N'DV: Kiểm tra hệ thống phanh', 'SERVICE', 1, 150000, 150000);

-- Payment for invoice 27
                  IF
NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = 27)
INSERT INTO payments
(invoice_id, payment_method, amount, status, order_code, transaction_ref, response_code, paid_at, raw_response_data, created_at, updated_at)
VALUES
(27, 'VNPAY', 300000.00, 'SUCCESSFUL', 'PAY-INV-TEST01', 'TEST14532456', '00', DATEADD(DAY, -100, GETDATE()), N'{"vnp_Amount":"30000000","vnp_ResponseCode":"00"}', DATEADD(DAY, -100, GETDATE()), DATEADD(DAY, -100, GETDATE()));

-- Job for booking 27
                     IF
NOT EXISTS (SELECT 1 FROM jobs WHERE booking_id = 27)
INSERT INTO jobs
(booking_id, technician_id, start_time, est_end_time, actual_end_time, notes, created_at, updated_at)
VALUES
(27, 9, DATEADD(HOUR, 9, DATEADD(DAY, -100, GETDATE())), DATEADD(HOUR, 11, DATEADD(DAY, -100, GETDATE())), DATEADD(MINUTE, 120, DATEADD(HOUR, 9, DATEADD(DAY, -100, GETDATE()))), N'Test booking - Hoàn thành bảo dưỡng', DATEADD(DAY, -101, GETDATE()), DATEADD(DAY, -100, GETDATE()));

-- ================================================================
-- DEMO DATA - ASSIGNED BOOKINGS (Ready to Start)
-- ================================================================
-- Add this at the END of data.sql (after reminder test data)
-- 4 ASSIGNED bookings with staggered start times for job demo
-- ================================================================
-- === BOOKINGS ===
IF NOT EXISTS (SELECT 1 FROM bookings WHERE id = 28)
INSERT INTO bookings
(customer_id, vin, schedule_date, booking_status, created_at, updated_at)
VALUES
(19, 'VFVF82AD3PA000101', DATEADD(HOUR, DATEPART(HOUR, GETDATE()) + 1, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'ASSIGNED', DATEADD(DAY, -1, GETDATE()), GETDATE()),
(20, 'VFVFE34D3PA000201', DATEADD(HOUR, DATEPART(HOUR, GETDATE()) + 2, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'ASSIGNED', DATEADD(DAY, -1, GETDATE()), GETDATE()),
(21, 'VFVF93EF3PA000301', DATEADD(HOUR, DATEPART(HOUR, GETDATE()) + 3, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'ASSIGNED', DATEADD(DAY, -1, GETDATE()), GETDATE()),
(22, 'VFVF62KL3PA000401', DATEADD(HOUR, DATEPART(HOUR, GETDATE()) + 1, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 'ASSIGNED', DATEADD(DAY, -1, GETDATE()), GETDATE());
-- === BOOKING DETAILS ===
IF NOT EXISTS (SELECT 1 FROM booking_details WHERE booking_id = 28)
INSERT INTO booking_details
(booking_id, maintenance_catalog_model_id, description)
VALUES
(28, 61, N'Chẩn đoán tổng thể xe'), (28, 26, N'Cập nhật phần mềm'),
(29, 7, N'Kiểm tra pin điện áp cao'), (29, 35, N'Thay lọc gió cabin'),
(30, 95, N'Thay gạt mưa'), (30, 102, N'Bổ sung nước rửa kính'),
(31, 48, N'Kiểm tra hệ thống treo'), (31, 76, N'Kiểm tra ắc quy 12V');
-- === INVOICES (PAID) ===
IF NOT EXISTS (SELECT 1 FROM invoices WHERE booking_id = 28)
INSERT INTO invoices
(booking_id, invoice_number, issue_date, due_date, total_amount, status, created_at, updated_at, paid_at)
VALUES
(28, 'INV-2025-00028', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, DATEPART(HOUR, GETDATE()), CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 500000, 'PAID', DATEADD(DAY, -1, GETDATE()), GETDATE(), GETDATE()),
(29, 'INV-2025-00029', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, DATEPART(HOUR, GETDATE()), CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 350000, 'PAID', DATEADD(DAY, -1, GETDATE()), GETDATE(), GETDATE()),
(30, 'INV-2025-00030', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, DATEPART(HOUR, GETDATE()), CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 230000, 'PAID', DATEADD(DAY, -1, GETDATE()), GETDATE(), GETDATE()),
(31, 'INV-2025-00031', DATEADD(DAY, -1, GETDATE()), DATEADD(HOUR, DATEPART(HOUR, GETDATE()), CAST(CAST(GETDATE() AS DATE) AS DATETIME)), 450000, 'PAID', DATEADD(DAY, -1, GETDATE()), GETDATE(), GETDATE());
-- === INVOICE LINES ===
IF NOT EXISTS (SELECT 1 FROM invoice_lines WHERE invoice_id = 28)
INSERT INTO invoice_lines
(invoice_id, item_description, item_type, quantity, unit_price, line_total)
VALUES
(28, N'DV: Chẩn đoán tổng thể xe', 'SERVICE', 1, 500000, 500000),
(28, N'DV: Cập nhật phần mềm', 'SERVICE', 1, 0, 0),
(29, N'DV: Kiểm tra pin điện áp cao', 'SERVICE', 1, 300000, 300000),
(29, N'DV: Thay lọc gió cabin', 'SERVICE', 1, 50000, 50000),
(30, N'DV: Thay gạt mưa', 'SERVICE', 1, 50000, 50000),
(30, N'LK: Gạt mưa Bosch 24"', 'PART', 1, 180000, 180000),
(31, N'DV: Kiểm tra hệ thống treo', 'SERVICE', 1, 350000, 350000),
(31, N'DV: Kiểm tra ắc quy 12V', 'SERVICE', 1, 100000, 100000);
-- === PAYMENTS (SUCCESSFUL) ===
IF NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = 28)
INSERT INTO payments
(invoice_id, payment_method, amount, status, order_code, transaction_ref, response_code, paid_at, raw_response_data, created_at, updated_at)
VALUES
(28, 'VNPAY', 500000.00, 'SUCCESSFUL', 'PAY-INV-00028', '14533001', '00', GETDATE(), N'{"vnp_Amount":"50000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE()),
(29, 'VNPAY', 350000.00, 'SUCCESSFUL', 'PAY-INV-00029', '14533002', '00', GETDATE(), N'{"vnp_Amount":"35000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE()),
(30, 'VNPAY', 230000.00, 'SUCCESSFUL', 'PAY-INV-00030', '14533003', '00', GETDATE(), N'{"vnp_Amount":"23000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE()),
(31, 'VNPAY', 450000.00, 'SUCCESSFUL', 'PAY-INV-00031', '14533004', '00', GETDATE(), N'{"vnp_Amount":"45000000","vnp_ResponseCode":"00"}', GETDATE(), GETDATE());
-- === JOBS (ASSIGNED - No start_time, ready to start) ===
IF NOT EXISTS (SELECT 1 FROM jobs WHERE booking_id = 28)
INSERT INTO jobs
(booking_id, technician_id, start_time, est_end_time, actual_end_time, notes, created_at, updated_at)
VALUES
(28, 9, NULL, NULL, NULL, N'Assigned - Ready to start', GETDATE(), GETDATE()),
(29, 10, NULL, NULL, NULL, N'Assigned - Backup slot', GETDATE(), GETDATE()),
(30, 11, NULL, NULL, NULL, N'Assigned - Second backup', GETDATE(), GETDATE()),
(31, 12, NULL, NULL, NULL, N'Assigned - Alternative slot', GETDATE(), GETDATE());
