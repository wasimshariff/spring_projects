package com.example.springbatch;

import com.example.springbatch.hello.JobCompletionNotificationListener;
import com.example.springbatch.hello.MyCustomWriter;
import com.example.springbatch.hello.Person;
import com.example.springbatch.hello.PersonProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@SpringBootApplication
@EnableBatchProcessing
public class SpringbatchApplication {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SpringbatchApplication.class, args), () -> 42));
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
    public JdbcCursorItemReader<Person> reader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Person>()
                .name("personItemReader")
                .dataSource(dataSource)
                .sql("select * from people")
                .beanRowMapper(Person.class)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("update people set first_name = :firstName, last_name = :lastName where person_id = :personId")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Person> writer2(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("update people set last_name = concat(last_name, 'wr') where person_id = :personId + 1")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public MyCustomWriter myCustomWriter() {
        return new MyCustomWriter();
    }

    @Bean
    public CompositeItemWriter<Person> compositeItemWriter(JdbcBatchItemWriter<Person> writer, JdbcBatchItemWriter<Person> writer2, MyCustomWriter customWriter){
        CompositeItemWriter compWriter = new CompositeItemWriter();
        compWriter.setDelegates(Arrays.asList(writer,customWriter));
        return compWriter;
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
    public Step step1(JdbcCursorItemReader<Person> reader , CompositeItemWriter<Person> compositeItemWriter, JdbcBatchItemWriter<Person> writer2, MyCustomWriter customWriter) {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(4)
                .reader(reader)
                .processor(personProcessor())
               //.writer(writer2)
                 .writer(compositeItemWriter)
              //  .transactionAttribute(transactionAttribute())
                .build();
    }

}
