package com.example.springbatch.hello;

import org.springframework.batch.item.ItemProcessor;

public class PersonProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Person transformedPerson = new Person(person.getPersonId(), firstName, lastName);
        if( transformedPerson.getFirstName().equalsIgnoreCase("ROOPESH")) {
            //transformedPerson.setSkipFlag(true);
            return null;
        }
        System.out.println(("Converting (" + person + ") into (" + transformedPerson + ")"));

        return transformedPerson;
    }
}
