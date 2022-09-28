package com.example.springbatch.hello;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.stream.Collectors;

public class MyCustomWriter implements ItemWriter<Person> {

    @Autowired
    @Qualifier("writer2")
    private JdbcBatchItemWriter batchItemWriter;


    @Override
    public void write(List<? extends Person> list) throws Exception {
        System.out.println("list :"+list);

        System.out.println("sizze b4 :"+list.size());
        List<Person> filteredItems = list.stream()
                .filter(person -> !person.isSkipFlag())
                .collect(Collectors.toList());
        System.out.println("sizze after :"+filteredItems.size());
        batchItemWriter.write(filteredItems);
    }
}
