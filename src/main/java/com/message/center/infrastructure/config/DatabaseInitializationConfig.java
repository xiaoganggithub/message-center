package com.message.center.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

/**
 * 数据库初始化配置
 * 用于在应用启动时执行数据库初始化脚本
 */
@Configuration
public class DatabaseInitializationConfig {

    @Value("classpath:init.sql")
    private Resource initSqlResource;

    /**
     * 创建数据源初始化器
     * @param dataSource 数据源
     * @return 数据源初始化器
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    /**
     * 创建数据库填充器
     * @return 数据库填充器
     */
    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(initSqlResource);
        populator.setContinueOnError(true);
        return populator;
    }
}
