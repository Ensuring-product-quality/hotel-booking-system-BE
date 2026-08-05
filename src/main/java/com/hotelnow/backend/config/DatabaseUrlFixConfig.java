package com.hotelnow.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class DatabaseUrlFixConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        String rawUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (!StringUtils.hasText(rawUrl)) {
            rawUrl = System.getenv("MYSQL_URL");
        }
        if (!StringUtils.hasText(rawUrl)) {
            rawUrl = System.getenv("MYSQLURL");
        }
        if (!StringUtils.hasText(rawUrl)) {
            rawUrl = System.getenv("DATABASE_URL");
        }

        String finalUrl = null;

        if (StringUtils.hasText(rawUrl)) {
            // Fix: MySQL JDBC Driver requires 'jdbc:mysql://', convert 'mysql://' if present
            if (rawUrl.startsWith("mysql://")) {
                finalUrl = "jdbc:" + rawUrl;
            } else if (rawUrl.startsWith("postgresql://") || rawUrl.startsWith("postgres://")) {
                finalUrl = "jdbc:" + rawUrl.replace("postgres://", "postgresql://");
            } else if (!rawUrl.startsWith("jdbc:")) {
                finalUrl = "jdbc:" + rawUrl;
            } else {
                finalUrl = rawUrl;
            }
        }

        // Fallback to building JDBC URL from host/port/database env vars
        if (finalUrl == null) {
            String host = System.getenv("MYSQLHOST");
            String port = System.getenv("MYSQLPORT");
            String database = System.getenv("MYSQLDATABASE");
            if (StringUtils.hasText(host) && StringUtils.hasText(database)) {
                port = StringUtils.hasText(port) ? port : "3306";
                finalUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            }
        }

        boolean isH2 = false;
        if (finalUrl == null) {
            // Local development fallback: use H2 memory database
            finalUrl = "jdbc:h2:mem:hotelnow_db;DB_CLOSE_DELAY=-1;MODE=MySQL";
            dataSource.setDriverClassName("org.h2.Driver");
            isH2 = true;
        } else {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }

        dataSource.setJdbcUrl(finalUrl);

        if (isH2) {
            dataSource.setUsername("sa");
            dataSource.setPassword("");
        } else {
            // Resolve username
            String username = System.getenv("SPRING_DATASOURCE_USERNAME");
            if (!StringUtils.hasText(username)) {
                username = System.getenv("MYSQLUSER");
            }
            if (!StringUtils.hasText(username)) {
                username = System.getenv("DATABASE_USER");
            }
            if (StringUtils.hasText(username)) {
                dataSource.setUsername(username);
            } else {
                dataSource.setUsername("root"); // Local MySQL default
            }

            // Resolve password
            String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
            if (!StringUtils.hasText(password)) {
                password = System.getenv("MYSQLPASSWORD");
            }
            if (!StringUtils.hasText(password)) {
                password = System.getenv("DATABASE_PASSWORD");
            }
            if (StringUtils.hasText(password)) {
                dataSource.setPassword(password);
            }
        }

        dataSource.setInitializationFailTimeout(0);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);

        return dataSource;
    }
}
