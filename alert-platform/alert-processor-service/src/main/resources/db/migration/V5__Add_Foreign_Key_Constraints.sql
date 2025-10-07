-- V5: Add foreign key constraint for alert_rules.user_id
-- This migration adds referential integrity to ensure alert rules cannot be orphaned

-- Add foreign key constraint with cascade delete
-- When a user is deleted, their alert rules will be automatically deleted
ALTER TABLE alert_rules
ADD CONSTRAINT fk_alert_rules_user_id
FOREIGN KEY (user_id)
REFERENCES users(id)
ON DELETE CASCADE;

-- Add index on user_id to improve query performance for user-based lookups
CREATE INDEX IF NOT EXISTS idx_alert_rules_user_id ON alert_rules(user_id);
