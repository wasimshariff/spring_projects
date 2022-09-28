package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)

    private Flux<Integer> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        Employee emp = new Employee();
        emp.setId("1234");
        emp.setName("Wasim");

        Employee emp2 = new Employee();
        emp2.setId("4567");
        emp2.setName("Nadeem");

        Employee emp3 = new Employee();
        emp3.setId("8899");
        emp3.setName("zara");
        employees.add(emp);
        employees.add(emp2);
        employees.add(emp3);
        // return Flux.fromIterable(employees).delaySequence(Duration.ofMillis(5000));
        Flux<Integer> flux2 = Flux.just(15, 20, 25)
                .delayElements(Duration.ofMillis(5000));
        return flux2;
    }
}
