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
        // Try MySQL datasource first if URL is set
        if (mysqlUrl != null && !mysqlUrl.isBlank()) {
            try {
                HikariConfig cfg = new HikariConfig();
                cfg.setJdbcUrl(mysqlUrl);
                cfg.setUsername(mysqlUser);
                cfg.setPassword(mysqlPass);
                cfg.setDriverClassName(mysqlDriver);
                cfg.setMaximumPoolSize(5);
                HikariDataSource ds = new HikariDataSource(cfg);
                // test connection
                try (Connection c = ds.getConnection()) {
                    // successful
                    System.out.println("Using MySQL datasource: " + mysqlUrl);
                    return ds;
                }
            } catch (Exception ex) {
                System.err.println("Failed to connect to MySQL at " + mysqlUrl + ": " + ex.getMessage());
                // fall through to H2
            }
        }

        // Fallback to H2 in-memory
        System.out.println("Falling back to in-memory H2 datasource");
        HikariConfig h2cfg = new HikariConfig();
        h2cfg.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
        h2cfg.setUsername("sa");
        h2cfg.setPassword("");
        h2cfg.setDriverClassName("org.h2.Driver");
        return new HikariDataSource(h2cfg);
    }
}
