-- Migration V3: Create Invoice System Tables
-- This migration creates all tables for the invoice, shipment, and audit microservices

-- ============================================
-- SHIPMENT SERVICE TABLES
-- ============================================

-- Drop existing conflicting tables if they have wrong schema
DROP TABLE IF EXISTS pdf_logs CASCADE;
DROP TABLE IF EXISTS invoice_history CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS invoice_shipments CASCADE;
DROP TABLE IF EXISTS invoice_items CASCADE;
DROP TABLE IF EXISTS invoices CASCADE;
DROP TABLE IF EXISTS shipments CASCADE;

CREATE TABLE IF NOT EXISTS shipments (
    shipment_id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    origin_address TEXT NOT NULL,
    destination_address TEXT NOT NULL,
    total_weight DECIMAL(10, 2) NOT NULL,
    total_volume DECIMAL(10, 2) NOT NULL,
    shipment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    tracking_number VARCHAR(100) UNIQUE,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipment_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_shipment_status ON shipments(shipment_status);
CREATE INDEX IF NOT EXISTS idx_shipment_tracking ON shipments(tracking_number);
CREATE INDEX IF NOT EXISTS idx_shipment_created ON shipments(created_at);

-- ============================================
-- INVOICE SERVICE TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS invoices (
    invoice_id BIGSERIAL PRIMARY KEY,
    fiscal_folio VARCHAR(100) UNIQUE,
    invoice_number VARCHAR(100) UNIQUE NOT NULL,
    client_name VARCHAR(255) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    invoice_status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    pdf_url TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    CONSTRAINT fk_invoice_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT chk_invoice_status CHECK (invoice_status IN ('DRAFT', 'ISSUED', 'PAID', 'CANCELLED')),
    CONSTRAINT chk_invoice_amounts CHECK (total_amount >= 0 AND subtotal >= 0 AND tax_amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_invoice_status ON invoices(invoice_status);
CREATE INDEX IF NOT EXISTS idx_invoice_fiscal ON invoices(fiscal_folio);
CREATE INDEX IF NOT EXISTS idx_invoice_date ON invoices(invoice_date);
CREATE INDEX IF NOT EXISTS idx_invoice_client ON invoices(client_name);
CREATE INDEX IF NOT EXISTS idx_invoice_created ON invoices(created_at);

CREATE TABLE IF NOT EXISTS invoice_items (
    item_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    shipment_id BIGINT,
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    CONSTRAINT fk_item_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id) ON DELETE SET NULL,
    CONSTRAINT chk_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_item_price CHECK (total_price >= 0)
);

CREATE INDEX IF NOT EXISTS idx_item_invoice ON invoice_items(invoice_id);
CREATE INDEX IF NOT EXISTS idx_item_shipment ON invoice_items(shipment_id);

CREATE TABLE IF NOT EXISTS invoice_shipments (
    invoice_shipment_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    shipment_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_ship_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_ship_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id) ON DELETE RESTRICT,
    CONSTRAINT uk_invoice_shipment UNIQUE (invoice_id, shipment_id)
);

CREATE INDEX IF NOT EXISTS idx_inv_ship_invoice ON invoice_shipments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_inv_ship_shipment ON invoice_shipments(shipment_id);

-- ============================================
-- AUDIT SERVICE TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS audit_logs (
    audit_log_id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    changed_by BIGINT,
    old_data JSONB,
    new_data JSONB,
    change_summary TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (changed_by) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_audit_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'ISSUE', 'REVERT', 'PUBLISH'))
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(changed_by);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_date ON audit_logs(created_at);

CREATE TABLE IF NOT EXISTS invoice_history (
    history_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    version INTEGER NOT NULL,
    fiscal_folio VARCHAR(100),
    invoice_number VARCHAR(100) NOT NULL,
    invoice_data JSONB NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_reverted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_history_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT uk_invoice_version UNIQUE (invoice_id, version)
);

CREATE INDEX IF NOT EXISTS idx_history_invoice ON invoice_history(invoice_id);
CREATE INDEX IF NOT EXISTS idx_history_version ON invoice_history(invoice_id, version);

-- ============================================
-- PDF SERVICE TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS pdf_logs (
    pdf_log_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    pdf_url TEXT,
    generation_status VARCHAR(50) NOT NULL,
    error_message TEXT,
    template_type VARCHAR(100),
    generated_by BIGINT,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pdf_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    CONSTRAINT fk_pdf_user FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_pdf_status CHECK (generation_status IN ('SUCCESS', 'FAILED', 'PENDING'))
);

CREATE INDEX IF NOT EXISTS idx_pdf_invoice ON pdf_logs(invoice_id);
CREATE INDEX IF NOT EXISTS idx_pdf_status ON pdf_logs(generation_status);
CREATE INDEX IF NOT EXISTS idx_pdf_date ON pdf_logs(generated_at);

-- ============================================
-- COMMENTS AND NOTES
-- ============================================

COMMENT ON TABLE shipments IS 'Shipment information for tracking and invoicing';
COMMENT ON TABLE invoices IS 'Main invoice table with fiscal information and status tracking';
COMMENT ON TABLE invoice_items IS 'Line items for each invoice';
COMMENT ON TABLE invoice_shipments IS 'Many-to-many relationship between invoices and shipments';
COMMENT ON TABLE audit_logs IS 'Audit trail for all entity changes across the system';
COMMENT ON TABLE invoice_history IS 'Version history for invoices to enable undo/revert functionality';
COMMENT ON TABLE pdf_logs IS 'Log of PDF generation attempts and results';

