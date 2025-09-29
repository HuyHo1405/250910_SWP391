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
