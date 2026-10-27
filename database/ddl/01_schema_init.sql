CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS user_addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_default BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    parent_id INT REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    commission_percentage DECIMAL(5, 2) NOT NULL DEFAULT 5.00
);

CREATE TABLE IF NOT EXISTS customs_tariffs (
    id SERIAL PRIMARY KEY,
    hs_code VARCHAR(20) NOT NULL UNIQUE,
    duty_tax_rate DECIMAL(5, 2) NOT NULL,
    vat_rate DECIMAL(5, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS shops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    rating DECIMAL(3, 2) DEFAULT 5.00,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    address_id UUID REFERENCES user_addresses(id)
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    category_id INT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    customs_tariff_id INT REFERENCES customs_tariffs(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    weight_kg DECIMAL(8, 3) NOT NULL,
    dimensions_cm VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PAUSED', 'OUT_OF_STOCK')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_images (
    id SERIAL PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    buyer_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id SERIAL PRIMARY KEY,
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    buyer_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    shipping_address_id UUID NOT NULL REFERENCES user_addresses(id) ON DELETE RESTRICT,
    total_products_amount DECIMAL(12, 2) NOT NULL,
    total_shipping_amount DECIMAL(12, 2) NOT NULL,
    total_tax_amount DECIMAL(12, 2) NOT NULL,
    grand_total DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT' 
        CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'IN_FULFILLMENT', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    payment_ref VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE RESTRICT,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12, 2) NOT NULL,
    platform_commission_fee DECIMAL(12, 2) NOT NULL,
    seller_net_payout DECIMAL(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS seller_wallets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shop_id UUID NOT NULL UNIQUE REFERENCES shops(id) ON DELETE RESTRICT,
    available_balance DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    pending_clearance_balance DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD'
);

CREATE TABLE IF NOT EXISTS wallet_transactions (
    id SERIAL PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES seller_wallets(id) ON DELETE CASCADE,
    amount DECIMAL(12, 2) NOT NULL,
    type VARCHAR(30) NOT NULL CHECK (type IN ('SALE_PAYOUT', 'COMMISSION_DEDUCTION', 'AD_CAMPAIGN_CHARGE', 'WITHDRAWAL')),
    reference_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ad_campaigns (
    id SERIAL PRIMARY KEY,
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    daily_budget DECIMAL(10, 2) NOT NULL,
    cpc_bid DECIMAL(6, 2) NOT NULL,
    spent_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PAUSED', 'EXHAUSTED')),
    impressions_count INT NOT NULL DEFAULT 0,
    clicks_count INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS drivers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('BIKE', 'MOTORCYCLE', 'CAR', 'VAN', 'TRUCK')),
    license_plate VARCHAR(30),
    driver_license_number VARCHAR(50) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    is_online BOOLEAN DEFAULT FALSE,
    current_latitude DECIMAL(10, 8),
    current_longitude DECIMAL(11, 8),
    rating_score DECIMAL(3, 2) DEFAULT 5.00
);

CREATE TABLE IF NOT EXISTS driver_wallets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    driver_id UUID NOT NULL UNIQUE REFERENCES drivers(id) ON DELETE RESTRICT,
    available_balance DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD'
);

CREATE TABLE IF NOT EXISTS shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    fulfillment_type VARCHAR(30) NOT NULL CHECK (fulfillment_type IN ('EXTERNAL_COURIER', 'ON_DEMAND_DRIVER', 'WAREHOUSE_CROSS_DOCK')),
    assigned_driver_id UUID REFERENCES drivers(id) ON DELETE SET NULL,
    carrier_name VARCHAR(100),
    tracking_number VARCHAR(100),
    shipping_cost_buyer DECIMAL(10, 2) NOT NULL,
    shipping_cost_actual DECIMAL(10, 2) NOT NULL,
    driver_payout_amount DECIMAL(10, 2),
    pickup_address_id UUID NOT NULL REFERENCES user_addresses(id) ON DELETE RESTRICT,
    delivery_address_id UUID NOT NULL REFERENCES user_addresses(id) ON DELETE RESTRICT,
    status VARCHAR(30) NOT NULL DEFAULT 'PREPARING'
        CHECK (status IN ('PREPARING', 'OFFERED_TO_DRIVERS', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'FAILED'))
);

CREATE TABLE IF NOT EXISTS shipment_tracking_events (
    id SERIAL PRIMARY KEY,
    shipment_id UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    status_description VARCHAR(255) NOT NULL,
    location_name VARCHAR(150),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS delivery_offers (
    id SERIAL PRIMARY KEY,
    shipment_id UUID NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    driver_id UUID NOT NULL REFERENCES drivers(id) ON DELETE CASCADE,
    offered_payout DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address_id UUID NOT NULL REFERENCES user_addresses(id) ON DELETE RESTRICT,
    storage_capacity_m3 DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS warehouse_inventories (
    id SERIAL PRIMARY KEY,
    warehouse_id INT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    allocated_quantity INT NOT NULL DEFAULT 0 CHECK (allocated_quantity >= 0),
    aisle_location VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS fulfillment_rates (
    id SERIAL PRIMARY KEY,
    storage_monthly_rate_m3 DECIMAL(10, 2) NOT NULL,
    pick_and_pack_unit_fee DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS inbound_shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    destination_warehouse_id INT NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
    origin_country VARCHAR(3) NOT NULL,
    total_declared_value_usd DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SHIPPED_FROM_ORIGIN'
        CHECK (status IN ('SHIPPED_FROM_ORIGIN', 'IN_CUSTOMS', 'DUTIES_PAID', 'RECEIVED_IN_WAREHOUSE'))
);

CREATE TABLE IF NOT EXISTS customs_declarations (
    id SERIAL PRIMARY KEY,
    inbound_shipment_id UUID NOT NULL REFERENCES inbound_shipments(id) ON DELETE CASCADE,
    hs_code_id INT NOT NULL REFERENCES customs_tariffs(id) ON DELETE RESTRICT,
    duties_amount_paid DECIMAL(12, 2) NOT NULL,
    clearance_fee_platform DECIMAL(12, 2) NOT NULL,
    cleared_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_products_shop_category ON products(shop_id, category_id);
CREATE INDEX IF NOT EXISTS idx_orders_buyer_status ON orders(buyer_id, status);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_drivers_geo_online ON drivers(current_latitude, current_longitude) WHERE is_online = TRUE;
CREATE INDEX IF NOT EXISTS dx_tracking_events_shipment ON shipment_tracking_events(shipment_id, created_at DESC);