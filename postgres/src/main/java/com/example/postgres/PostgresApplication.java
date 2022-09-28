package com.example.postgres;

import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.example.postgres.model.TestingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@SpringBootApplication
@EnableConfigurationProperties(TestingProperties.class)
public class PostgresApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostgresApplication.class, args);
	}

	@Bean
	@DependsOn("userTransactionServiceImp")
	public UserTransaction userTransaction()  {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		return userTransactionImp;
	}

	@Bean(initMethod = "init" , destroyMethod = "shutdownWait")
	public UserTransactionServiceImp userTransactionServiceImp() throws Throwable {
		UserTransactionServiceImp userTransactionImp = new UserTransactionServiceImp();
		return userTransactionImp;
	}

	@Bean
	@DependsOn("userTransactionServiceImp")
	public UserTransactionManager userTransactionManager() {
		UserTransactionManager userTransactionImp = new UserTransactionManager();
		return userTransactionImp;
	}

	@Bean
	public JtaTransactionManager jtaTransactionManager() {
		JtaTransactionManager userTransactionManager = new JtaTransactionManager();
		userTransactionManager.setUserTransaction(userTransaction());
		userTransactionManager.setTransactionManager(userTransactionManager());
		return userTransactionManager;
	}

	// Not sure if this is needed
/*	@Bean
	public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
		return new PersistenceAnnotationBeanPostProcessor();
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
		return new PersistenceExceptionTranslationPostProcessor();
	}*/

}
