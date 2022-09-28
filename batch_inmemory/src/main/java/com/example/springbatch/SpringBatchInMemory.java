package com.example.springbatch;

import com.example.springbatch.hello.ErrorEvent;
import com.example.springbatch.hello.JobCompletionNotificationListener;
import com.example.springbatch.hello.MyCustomWriter;
import com.example.springbatch.hello.Person;
import com.example.springbatch.hello.PersonProcessor;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchInMemory extends DefaultBatchConfigurer {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired private Environment env;


    @Bean(name = "iaaDataSourceProperties")
  //  @Primary
    @ConfigurationProperties("spring.datasource.iaa")
    public DataSourceProperties iaaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "iaaDataSource")
  //  @Primary
    @ConfigurationProperties("spring.datasource.iaa.configuration")
    public HikariDataSource cdhDataSource(final @Qualifier("iaaDataSourceProperties") DataSourceProperties iaaDBProperties) {
        HikariDataSource ds = iaaDBProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        ds.setPoolName("IAA Pool");
        return ds;
    }

    @Bean(name = "jobDataSourceProperties")
    @Primary
    @ConfigurationProperties("spring.datasource.job")
    public DataSourceProperties jobDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "mainDataSource")
    @Primary
    public DataSource mainDataSource(final @Qualifier("jobDataSourceProperties") DataSourceProperties jobDBProperties) {
        /*final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.hsqldb.jdbcDriver());
       // dataSource.setUrl("jdbc:hsqldb:mem:mydb");
        dataSource.setUrl(jobDBProperties.getUrl());
        dataSource.setUsername(jobDBProperties.getUsername());
        dataSource.setPassword(jobDBProperties.getPassword());
        return dataSource;*/

        HikariDataSource ds = jobDBProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        ds.setPoolName("Job Pool");
        return ds;
    }

    @Override
    @Autowired
    public void setDataSource(@Qualifier("mainDataSource") DataSource batchDataSource) {
        super.setDataSource(batchDataSource);
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchInMemory.class, args), () -> 42));
    }

  /*  @Bean
    CommandLineRunner createException() {
        return args -> Integer.parseInt("test") ;
    }*/

    @Bean
    ExitCodeExceptionMapper exitCodeToexceptionMapper() {
        return exception -> {
            // set exit code base on the exception type
            if (exception.getCause() instanceof RuntimeException) {
                return 80;
            }
            return 1;
        };
    }
    @Bean
    public PersonProcessor personProcessor(){
        return new PersonProcessor();
    }

    /*@Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }*/

    @Bean(destroyMethod="")
    public JdbcCursorItemReader<ErrorEvent> reader(@Qualifier("iaaDataSource") DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<ErrorEvent>()
                .name("personItemReader")
                .dataSource(dataSource)
                .sql("SELECT * from ACTIVITY.ERROR_EVENT where id = 240852311")
                .beanRowMapper(ErrorEvent.class)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<ErrorEvent> writer(@Qualifier("mainDataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<ErrorEvent>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                //.sql("UPDATE ACTIVITY.ERROR_EVENT set PROBLEM_BRIEF_DESCRIPTION = 'problem desc updated' where id = :id")
                .sql("UPDATE PEOPLE set first_name = upper(first_name) where person_id = :id")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public DefaultTransactionAttribute transactionAttribute() {
        DefaultTransactionAttribute transactionWithIsolationReadCommited = new DefaultTransactionAttribute();
        transactionWithIsolationReadCommited.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);
        transactionWithIsolationReadCommited.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        return transactionWithIsolationReadCommited;
    }



    @Bean
    public Step step1(JdbcCursorItemReader<ErrorEvent> reader , JdbcBatchItemWriter<ErrorEvent> writer) {
        return stepBuilderFactory.get("step1")
                .<ErrorEvent, ErrorEvent> chunk(3)
                .reader(reader)
                .processor(personProcessor())
                .writer(writer)
               // .writer(compositeItemWriter)
                //  .transactionAttribute(transactionAttribute())
                .build();
    }

}
