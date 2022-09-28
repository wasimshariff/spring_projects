package com.example.springbatch.hello;

import org.springframework.batch.item.ItemProcessor;

public class PersonProcessor implements ItemProcessor<ErrorEvent, ErrorEvent> {

    @Override
    public ErrorEvent process(ErrorEvent person) throws Exception {
        System.out.println("Error Event payload : " + person.getProblemPayload());
        person.setId(3);
        return person;
    }
}
