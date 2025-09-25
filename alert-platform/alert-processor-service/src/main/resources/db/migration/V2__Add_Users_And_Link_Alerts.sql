-- V2: Create the users table and associate alert rules with users.

CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

-- Add the user_id column to the alert_rules table
ALTER TABLE alert_rules
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

-- Note: We will add the foreign key constraint in a separate, later migration
-- to avoid circular dependencies during startup if services are deployed independently.
-- For now, we are just adding the column.