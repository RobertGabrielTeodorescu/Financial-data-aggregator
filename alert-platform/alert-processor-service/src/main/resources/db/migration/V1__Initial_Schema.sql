-- V1: Create initial partitioned tables for market data and the alert rules table.
-- NOTE: Table names defined here must match constants in TableConstants.java

-- Create the base partitioned table for trades
CREATE TABLE IF NOT EXISTS trades
(
    "timestamp" TIMESTAMPTZ    NOT NULL,
    symbol      VARCHAR(32)    NOT NULL,
    price       NUMERIC(15, 5) NOT NULL,
    size        BIGINT         NOT NULL,
    PRIMARY KEY ("timestamp")
) PARTITION BY RANGE ("timestamp");

-- Create an index for efficient querying by symbol and time
CREATE INDEX IF NOT EXISTS idx_trade_symbol_timestamp ON trades (symbol, "timestamp" DESC);

-- Create the base partitioned table for quotes
CREATE TABLE IF NOT EXISTS quotes
(
    "timestamp" TIMESTAMPTZ    NOT NULL,
    symbol      VARCHAR(32)    NOT NULL,
    bid_price   NUMERIC(15, 5) NOT NULL,
    bid_size    BIGINT         NOT NULL,
    ask_price   NUMERIC(15, 5) NOT NULL,
    ask_size    BIGINT         NOT NULL,
    PRIMARY KEY ("timestamp")
) PARTITION BY RANGE ("timestamp");

-- Create an index for efficient querying by symbol and time
CREATE INDEX IF NOT EXISTS idx_quote_symbol_timestamp ON quotes (symbol, "timestamp" DESC);

-- Create the table for alert rules
CREATE TABLE IF NOT EXISTS alert_rules
(
    id             BIGSERIAL PRIMARY KEY,
    symbol         VARCHAR(32)    NOT NULL,
    condition_type VARCHAR(32)    NOT NULL,
    value          NUMERIC(15, 5) NOT NULL,
    enabled        BOOLEAN        NOT NULL DEFAULT TRUE,
    notes          VARCHAR(255)
);

-- Create an index for efficient rule lookup by symbol
CREATE INDEX IF NOT EXISTS idx_alertrule_symbol ON alert_rules (symbol);