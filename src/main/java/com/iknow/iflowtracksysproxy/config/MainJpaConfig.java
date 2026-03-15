package com.iknow.iflowtracksysproxy.config;

import com.iknow.iflowtracksysproxy.respository.AdvisorReportRepository;
import com.iknow.iflowtracksysproxy.respository.DealerReportRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.ComponentScan.Filter;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.iknow.iflowtracksysproxy.respository",
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        DealerReportRepository.class,
                        AdvisorReportRepository.class
                }
        ),
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class MainJpaConfig {

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mainDataSource") DataSource mainDataSource,
            JpaProperties jpaProperties
    ) {
        return builder
                .dataSource(mainDataSource)
                .packages("com.iknow.iflowtracksysproxy.entity")
                .persistenceUnit("main")
                .properties(new HashMap<>(jpaProperties.getProperties()))
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}