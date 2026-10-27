
INSERT INTO roles (id, code) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_BUYER'),
(3, 'ROLE_SELLER'),
(4, 'ROLE_DRIVER'),
(5, 'ROLE_WAREHOUSE_OPERATOR')
ON CONFLICT (code) DO NOTHING;

-- Insertar usuario administrador
-- Contraseña encriptada con BCrypt para "Password123!": $2a$10$e8R.qX1v3Z8D2.Gz9S7e4e1y8u9I0O1P2Q3R4S5T6U7V8W9X0Y1Z2
INSERT INTO users (id, email, password_hash, first_name, last_name, is_active, created_at) 
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 
    'admin@ecommerce.com', 
    '$2a$10$e8R.qX1v3Z8D2.Gz9S7e4e1y8u9I0O1P2Q3R4S5T6U7V8W9X0Y1Z2', 
    'Admin', 
    'General', 
    true, 
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Asignar rol de Admin al usuario
INSERT INTO user_roles (user_id, role_id) 
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1) 
ON CONFLICT DO NOTHING;