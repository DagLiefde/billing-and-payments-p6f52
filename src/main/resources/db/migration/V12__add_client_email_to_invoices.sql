-- Migration V12: Add client_email column to invoices table
-- This migration adds email support to invoices for sending invoices via email

-- Add client_email column to invoices table if it doesn't exist
ALTER TABLE invoices
ADD COLUMN IF NOT EXISTS client_email VARCHAR(255);

-- Create index on client_email for faster queries
CREATE INDEX IF NOT EXISTS idx_invoices_client_email ON invoices(client_email);

