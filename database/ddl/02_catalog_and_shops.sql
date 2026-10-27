-- =============================================================================
-- DOMINIO 1: USUARIOS, SEGURIDAD Y DIRECCIONES
-- =============================================================================

-- 1. Tabla de Roles (Aseguramos la columna description si no existe)
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE
);

ALTER TABLE roles ADD COLUMN IF NOT EXISTS description VARCHAR(255);

-- Insertar roles por defecto si no existen
INSERT INTO roles (code, description) VALUES 
('ROLE_BUYER', 'Comprador final'),
('ROLE_SELLER', 'Vendedor / Tienda'),
('ROLE_DRIVER', 'Repartidor On-Demand'),
('ROLE_WAREHOUSE', 'Operador de Depósito / Fulfillment'),
('ROLE_ADMIN', 'Administrador Global')
ON CONFLICT (code) DO NOTHING;

-- 2. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla Intermedia Usuarios-Roles (RBAC)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 4. Direcciones de Usuarios
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================================================
-- DOMINIO 2: CATÁLOGO Y TIENDAS
-- =============================================================================

-- 5. Tiendas (Sellers)
CREATE TABLE IF NOT EXISTS shops (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shops_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- 6. Categorías de Productos
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 7. Aranceles Aduaneros / Partidas Arancelarias (Cross-Border)
CREATE TABLE IF NOT EXISTS customs_tariffs (
    id BIGSERIAL PRIMARY KEY,
    hs_code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    duty_rate DECIMAL(5, 2) NOT NULL,
    vat_rate DECIMAL(5, 2) NOT NULL,
    requires_permit BOOLEAN DEFAULT FALSE
);

-- 8. Productos
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    customs_tariff_id BIGINT,
    sku VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    weight_kg DECIMAL(8, 3) NOT NULL DEFAULT 0.000,
    length_cm DECIMAL(8, 2) DEFAULT 0.00,
    width_cm DECIMAL(8, 2) DEFAULT 0.00,
    height_cm DECIMAL(8, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_shop FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_products_customs FOREIGN KEY (customs_tariff_id) REFERENCES customs_tariffs(id) ON DELETE SET NULL
);

-- 9. Imágenes de Productos
CREATE TABLE IF NOT EXISTS product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Índices de optimización
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_products_shop ON products(shop_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_categories_parent ON categories(parent_id);