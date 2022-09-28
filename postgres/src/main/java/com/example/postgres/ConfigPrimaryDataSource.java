package com.example.postgres;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "postgresEntityManager",
        transactionManagerRef = "jtaTransactionManager",
        basePackages = {"com.example.postgres.repository.postgres"}
)
public class ConfigPrimaryDataSource {

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), additionalJpaProperties(), null);
        return builder;
    }

    @Bean(name = "postgresEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean getPostgresEntityManager(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return builder
                .dataSource(postgresDataSource)
                .packages("com.example.postgres.model")
                .persistenceUnit("postgres")
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


    @Bean("postgresDataSourceProperties")
    @Primary
    @ConfigurationProperties("app.datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean("postgresDataSource")
    @Primary
    @ConfigurationProperties("app.datasource.postgres")
    public DataSource postgresDataSource(@Qualifier("postgresDataSourceProperties") DataSourceProperties postgresDataSourceProperties) {
       // return postgresDataSourceProperties.initializeDataSourceBuilder().build();
        PGXADataSource ds =new PGXADataSource();
        ds.setUrl(postgresDataSourceProperties.getUrl());
        ds.setUser(postgresDataSourceProperties.getUsername());
        ds.setPassword(postgresDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xads1");
        return xaDataSource;
    }

    // for non XA
/*    @Bean(name = "postgresTransactionManager")
    public JpaTransactionManager transactionManager(@Qualifier("postgresEntityManager") EntityManagerFactory postgresEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(postgresEntityManager);
        return transactionManager;
    }*/

}