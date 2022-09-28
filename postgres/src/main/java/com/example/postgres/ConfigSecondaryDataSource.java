package com.example.postgres;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "hibernatedbEntityManager",
        transactionManagerRef = "jtaTransactionManager",
        basePackages = {"com.example.postgres.repository.hibernatedb"}
)
public class ConfigSecondaryDataSource {

    @Bean(name = "hibernatedbEntityManager")
    public LocalContainerEntityManagerFactoryBean getHibernatedbEntityManager(EntityManagerFactoryBuilder builder,
                                                                              @Qualifier("hibernatedbDataSource") DataSource hibernatedbDataSource) {
        return builder
                .dataSource(hibernatedbDataSource)
                .packages("com.example.postgres.model")
                .persistenceUnit("hibernatedb")
                .properties(additionalJpaProperties())
                .jta(true)
                .build();
    }

    Map<String, ?> additionalJpaProperties() {
        Map<String, String> map = new HashMap<>();
        // map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        map.put("hibernate.show_sql", "true");
        map.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        map.put("hibernate.transaction.jta.platform", "com.atomikos.icatch.jta.hibernate4.AtomikosPlatform");
        map.put("javax.persistence.transactionType", "JTA");
        return map;
    }

    @Bean("hibernatedbDataSourceProperties")
    @ConfigurationProperties("app.datasource.hibernatedb")
    public DataSourceProperties hibernatedbDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("hibernatedbDataSource")
    @ConfigurationProperties("app.datasource.hibernatedb")
    public DataSource hibernatedbDataSource(@Qualifier("hibernatedbDataSourceProperties") DataSourceProperties hibernatedbDataSourceProperties) {
       // return hibernatedbDataSourceProperties.initializeDataSourceBuilder().build();
        PGXADataSource ds =new PGXADataSource();
        ds.setUrl(hibernatedbDataSourceProperties.getUrl());
        ds.setUser(hibernatedbDataSourceProperties.getUsername());
        ds.setPassword(hibernatedbDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xads2");
        return xaDataSource;
    }

    // For non XA sources
/*    @Bean(name = "hibernatedbTransactionManager")
    public JpaTransactionManager transactionManager(@Qualifier("hibernatedbEntityManager") EntityManagerFactory hibernatedbEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(hibernatedbEntityManager);

        return transactionManager;
    }*/
}