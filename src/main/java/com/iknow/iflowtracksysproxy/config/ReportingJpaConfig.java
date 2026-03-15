package com.iknow.iflowtracksysproxy.config;

import com.iknow.iflowtracksysproxy.entity.AdvisorReport;
import com.iknow.iflowtracksysproxy.entity.DealerReport;
import com.iknow.iflowtracksysproxy.entity.base.ReportBase;
import com.iknow.iflowtracksysproxy.respository.AdvisorReportRepository;
import com.iknow.iflowtracksysproxy.respository.DealerReportRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.iknow.iflowtracksysproxy.respository",
        includeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        DealerReportRepository.class,
                        AdvisorReportRepository.class
                }
        ),
        entityManagerFactoryRef = "reportingEntityManagerFactory",
        transactionManagerRef = "reportingTransactionManager"
)
public class ReportingJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean reportingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("reportingDataSource") DataSource reportingDataSource,
            JpaProperties jpaProperties
    ) {
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.hbm2ddl.auto", "update");

        return builder
                .dataSource(reportingDataSource)
                .packages(
                        ReportBase.class,
                        DealerReport.class,
                        AdvisorReport.class
                )
                .persistenceUnit("reporting")
                .properties(properties)
                .build();
    }

    @Bean
    public PlatformTransactionManager reportingTransactionManager(
            @Qualifier("reportingEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}