package edu.miu.cs.srmweb.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.Connection;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:}")
    private String mysqlUser;

    @Value("${spring.datasource.password:}")
    private String mysqlPass;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriver;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Require MySQL datasource: fail fast if not available
        if (mysqlUrl == null || mysqlUrl.isBlank()) {
            throw new IllegalStateException("MySQL datasource URL is not configured (spring.datasource.url)");
        }
        try {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(mysqlUrl);
            cfg.setUsername(mysqlUser);
            cfg.setPassword(mysqlPass);
            cfg.setDriverClassName(mysqlDriver);
            cfg.setMaximumPoolSize(10);
            HikariDataSource ds = new HikariDataSource(cfg);
            // test connection
            try (Connection c = ds.getConnection()) {
                System.out.println("Using MySQL datasource: " + mysqlUrl);
                return ds;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to connect to MySQL at " + mysqlUrl + ": " + ex.getMessage(), ex);
        }
    }
}
