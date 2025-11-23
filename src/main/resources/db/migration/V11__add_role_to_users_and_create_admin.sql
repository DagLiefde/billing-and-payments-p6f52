-- Migration V11: Add role column to users table
-- This migration adds role support to users table
-- Note: Admin user will be created by DataLoader component on application startup

-- Add role column to users table if it doesn't exist
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS role VARCHAR(50) NOT NULL DEFAULT 'USER';

-- Create index on role for faster queries
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

