-- V3: Add status tracking to alert_rules to prevent infinite notifications

-- Add status column with default PENDING
ALTER TABLE alert_rules
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Add index for efficient queries filtering by status
CREATE INDEX IF NOT EXISTS idx_alertrule_status ON alert_rules (status);

-- Update existing enabled/disabled alerts to use new status system
UPDATE alert_rules
SET status = 'DISABLED'
WHERE enabled = false;

UPDATE alert_rules
SET status = 'PENDING'
WHERE enabled = true;

-- Add check constraint to ensure valid status values
ALTER TABLE alert_rules
    ADD CONSTRAINT chk_alert_status CHECK (status IN ('PENDING', 'FIRED', 'DISABLED'));
