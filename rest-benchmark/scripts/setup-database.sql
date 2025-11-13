-- Script to setup database and populate initial data
-- Run this after starting PostgreSQL container

-- Connect to default postgres database first
-- \c postgres

-- Create database and user (if not exists via docker-compose)
-- CREATE DATABASE benchmark_db;
-- CREATE USER benchmark_user WITH PASSWORD 'benchmark_pass';
-- GRANT ALL PRIVILEGES ON DATABASE benchmark_db TO benchmark_user;

-- Connect to benchmark_db
\c benchmark_db

-- Tables are auto-created by Hibernate
-- This script is for manual data population if needed

-- Example: Check if tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';

-- Example: Check counts
SELECT 
    (SELECT COUNT(*) FROM category) as category_count,
    (SELECT COUNT(*) FROM item) as item_count;

-- Create indexes for performance (if not auto-created)
CREATE INDEX IF NOT EXISTS idx_item_category_id ON item(category_id);
CREATE INDEX IF NOT EXISTS idx_category_code ON category(code);

-- Analyze tables for query planner
ANALYZE category;
ANALYZE item;

-- Show table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

