package com.example.demo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.PathParam;

@RestController
@Validated
@RequestMapping(path = "/queue/", produces = "application/json")
public class QueueController {
    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Tracer tracer;

    @PostMapping("{queueName}")
    public ResponseEntity<String> postMessageToQueue(@RequestBody String payload, @PathVariable String queueName) {
        try {
            logger.info("inside controller 1");
            System.out.println("Posting to queue::" + queueName);
            /*this.jmsTemplate.convertAndSend(queueName, payload, m-> {
                m.setStringProperty("b3", "1dc25293498bd9cb-1111111111111111-0");
                return m;
            });*/
            this.jmsTemplate.convertAndSend(queueName, payload);
            logger.info("inside controller 2.1");

            return new ResponseEntity<String>("done", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Exception getIvrSignature", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping()
    public ResponseEntity getTraceId(){
        logger.info("Trace Id: " + tracer.currentSpan().context().traceId());

        logger.info("span Id: " + tracer.currentSpan().context().spanId());
        logger.info("sampled: " + tracer.currentSpan().context().sampled());

        logger.info("toString Id: " + tracer.currentSpan().context().toString());

        return ResponseEntity.ok(tracer.currentSpan().context().traceId());
    }
}
