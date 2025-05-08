package org.example.utils;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

public class DataSourceConfig {
    public static DataSource getDataSource() {
        Dotenv dotenv = Dotenv.load();
        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("POSTGRES_DB");
        String dbUser = dotenv.get("POSTGRES_USER");
        String dbPassword = dotenv.get("POSTGRES_PASSWORD");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName));
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(5);
        config.setIdleTimeout(5);  // Время простоя соединений в миллисекундах
        config.setMaxLifetime(30000);
        return new HikariDataSource(config);
    }
}