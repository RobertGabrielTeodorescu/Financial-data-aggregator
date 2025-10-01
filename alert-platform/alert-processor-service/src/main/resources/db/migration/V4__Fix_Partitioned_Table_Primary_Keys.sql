-- V4: Fix partitioned table primary keys to prevent data loss
-- Issue: Using only timestamp as PK causes data loss when multiple trades occur at same microsecond
-- Solution: Use composite PK (timestamp, id) to ensure uniqueness while maintaining partition pruning

-- Drop existing partitioned tables and their partitions
-- Note: This will delete all existing market data. For production, use a migration strategy
-- that preserves existing data (e.g., rename tables, migrate data, then drop old tables)
DROP TABLE IF EXISTS trades CASCADE;
DROP TABLE IF EXISTS quotes CASCADE;

-- Recreate trades table with composite primary key
CREATE TABLE IF NOT EXISTS trades
(
    id          BIGSERIAL,
    "timestamp" TIMESTAMPTZ    NOT NULL,
    symbol      VARCHAR(32)    NOT NULL,
    price       NUMERIC(15, 5) NOT NULL,
    size        BIGINT         NOT NULL,
    PRIMARY KEY ("timestamp", id)
) PARTITION BY RANGE ("timestamp");

-- Recreate index for efficient querying by symbol and time
CREATE INDEX IF NOT EXISTS idx_trade_symbol_timestamp ON trades (symbol, "timestamp" DESC);

-- Recreate quotes table with composite primary key
CREATE TABLE IF NOT EXISTS quotes
(
    id          BIGSERIAL,
    "timestamp" TIMESTAMPTZ    NOT NULL,
    symbol      VARCHAR(32)    NOT NULL,
    bid_price   NUMERIC(15, 5) NOT NULL,
    bid_size    BIGINT         NOT NULL,
    ask_price   NUMERIC(15, 5) NOT NULL,
    ask_size    BIGINT         NOT NULL,
    PRIMARY KEY ("timestamp", id)
) PARTITION BY RANGE ("timestamp");

-- Recreate index for efficient querying by symbol and time
CREATE INDEX IF NOT EXISTS idx_quote_symbol_timestamp ON quotes (symbol, "timestamp" DESC);
