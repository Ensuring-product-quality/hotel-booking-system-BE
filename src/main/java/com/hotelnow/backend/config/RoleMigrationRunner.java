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
