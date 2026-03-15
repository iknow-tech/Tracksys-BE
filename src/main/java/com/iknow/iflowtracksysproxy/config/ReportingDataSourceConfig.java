package com.iknow.iflowtracksysproxy.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ReportingDataSourceConfig {

    @Bean
    @ConfigurationProperties("reporting.datasource")
    public DataSourceProperties reportingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource reportingDataSource() {
        return reportingDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}