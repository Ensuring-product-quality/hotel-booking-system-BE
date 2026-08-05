package com.hotelnow.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Runs at startup (Order 0, before DevDataInitializer) to migrate any legacy 'STAFF'
 * role values in the users table to 'MANAGER', preventing Hibernate enum mapping errors.
 */
@Component
@Order(0)
public class RoleMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public RoleMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 1. Widen the ENUM columns to VARCHAR to prevent MySQL truncation / value-constraint crashes
        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role VARCHAR(50) NOT NULL");
            log.info("[DatabaseMigration] Successfully modified users.role column to VARCHAR(50)");
        } catch (Exception e) {
            log.info("[DatabaseMigration] Skip users.role alteration: {}", e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE bookings MODIFY COLUMN status VARCHAR(50) NOT NULL");
            log.info("[DatabaseMigration] Successfully modified bookings.status column to VARCHAR(50)");
        } catch (Exception e) {
            log.info("[DatabaseMigration] Skip bookings.status alteration: {}", e.getMessage());
        }

        // 2. Perform the legacy role updates
        try {
            int updated = jdbcTemplate.update(
                    "UPDATE users SET role = 'MANAGER' WHERE role = 'STAFF'"
            );
            if (updated > 0) {
                log.info("[RoleMigration] Migrated {} user(s) from role STAFF -> MANAGER", updated);
            } else {
                log.info("[RoleMigration] No STAFF role records found. Nothing to migrate.");
            }
        } catch (Exception e) {
            log.warn("[RoleMigration] Could not run migration (table may not exist yet): {}", e.getMessage());
        }
    }
}
