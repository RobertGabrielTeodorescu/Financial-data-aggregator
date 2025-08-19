package com.findataagg.alertprocessorservice.persistence.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.findataagg.alertprocessorservice.persistence.constants.TableConstants.PARTITIONED_TABLES;

@Slf4j
@Service
public class PartitionManagementService {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.partitioning.enabled:true}")
    private boolean partitioningEnabled;

    @Value("${app.partitioning.retention-months:24}")
    private int retentionMonths;

    @Value("${app.partitioning.auto-cleanup:true}")
    private boolean autoCleanup;

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initializePartitionsOnStartup() {
        if (!partitioningEnabled) {
            log.debug("Partition management is disabled");
            return;
        }

        log.info("Initializing partitions on application startup...");
        ensureCurrentMonthPartitionsExist();
    }

    @Scheduled(cron = "0 0 1 1 * ?") // First day of each month at midnight
    @Transactional
    public void createNextMonthPartition() {
        if (!partitioningEnabled) {
            log.debug("Partition management is disabled");
            return;
        }

        log.info("Running scheduled partition creation task...");
        ensureCurrentMonthPartitionsExist();
    }

    @Scheduled(cron = "0 0 2 1 * ?") // Second day of each month at midnight
    @Transactional
    public void cleanupOldPartitions() {
        if (!partitioningEnabled || !autoCleanup) {
            log.debug("Partition cleanup is disabled");
            return;
        }

        log.info("Running scheduled partition cleanup task...");
        for (String table : PARTITIONED_TABLES) {
            cleanupOldPartitionsForTable(table);
        }
    }

    private void ensureCurrentMonthPartitionsExist() {
        // Create partitions for the current month (to handle month boundaries)
        for (String table : PARTITIONED_TABLES) {
            ensurePartitionForCurrentMonth(table);
        }
    }

    private void ensurePartitionForCurrentMonth(String baseTableName) {
        // Validate input to prevent SQL injection
        if (isInvalidTableName(baseTableName)) {
            log.error("Invalid table name: {}", baseTableName);
            return;
        }

        LocalDate today = LocalDate.now();
        String partitionName = getPartitionName(baseTableName, today);

        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

        // Build SQL safely - PostgreSQL DDL doesn't support parameters for date values
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ")
                .append(partitionName)
                .append(" PARTITION OF ")
                .append(baseTableName)
                .append(" FOR VALUES FROM ('")
                .append(startOfMonth)
                .append("') TO ('")
                .append(startOfNextMonth)
                .append("')");

        try {
            log.info("Ensuring partition {} exists...", partitionName);
            entityManager.createNativeQuery(sqlBuilder.toString()).executeUpdate();
            log.info("Successfully created/verified partition: {}", partitionName);
        } catch (Exception e) {
            log.error("Failed to create partition {}: {}", partitionName, e.getMessage());
        }
    }

    private static String getPartitionName(String baseTableName, LocalDate today) {
        return baseTableName + "_" + today.format(DateTimeFormatter.ofPattern("yyyy_MM"));
    }

    private void cleanupOldPartitionsForTable(String baseTableName) {
        if (isInvalidTableName(baseTableName)) {
            log.error("Invalid table name for cleanup: {}", baseTableName);
            return;
        }

        LocalDate cutoffDate = LocalDate.now().minusMonths(retentionMonths);
        LocalDate partitionMonth = LocalDate.of(cutoffDate.getYear(), cutoffDate.getMonth(), 1);

        // Clean up partitions older than retention period
        while (partitionMonth.isBefore(LocalDate.now().minusMonths(retentionMonths))) {
            String partitionName = getPartitionName(baseTableName, partitionMonth);

            try {
                String dropSql = "DROP TABLE IF EXISTS " + partitionName;
                log.info("Dropping old partition: {}", partitionName);
                entityManager.createNativeQuery(dropSql).executeUpdate();
                log.info("Successfully dropped partition: {}", partitionName);
            } catch (Exception e) {
                log.error("Failed to drop partition {}: {}", partitionName, e.getMessage());
            }

            partitionMonth = partitionMonth.minusMonths(1);
        }
    }

    private boolean isInvalidTableName(String tableName) {
        // Allow only alphanumeric characters and underscores for table names
        return tableName == null || !tableName.matches("^[a-zA-Z0-9_]+$") ||
                !Arrays.asList(PARTITIONED_TABLES).contains(tableName);
    }

}
