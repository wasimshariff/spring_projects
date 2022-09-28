package com.example.postgres.controller;

import com.example.postgres.model.Doctors;
import com.example.postgres.model.Pets;
import com.example.postgres.model.TestingProperties;
import com.example.postgres.model.Vets;
import com.example.postgres.service.DocService;
import com.example.postgres.service.PetsService;
import com.example.postgres.service.VetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.UUID;

@RestController
public class MyRestController {

    @Autowired
    private PetsService petsService;

    @Autowired
    private VetsService vetsService;

    @Autowired
    private DocService docService;

    @Autowired
    private TestingProperties testingProperties;

    @RequestMapping(path = "/pets", method = RequestMethod.POST)
    public ResponseEntity addPets(@RequestBody Pets pets) {
        pets.setId(UUID.randomUUID().toString());
        petsService.addPets(pets);
        return ResponseEntity.ok(pets);
    }

    @RequestMapping(path = "/vets", method = RequestMethod.POST)
    public ResponseEntity addVets(@RequestBody Vets vets) {
        vets.setId(UUID.randomUUID().toString());
        vetsService.addPets(vets);
        return ResponseEntity.ok(vets);
    }

    @RequestMapping(path = "/pets/{msg}", method = RequestMethod.GET)
    public ResponseEntity listPetsByMsg(@PathVariable("msg") String msg) {
        System.out.println("dept:"+testingProperties.getDepartment());
        List<Pets> pets = petsService.listPetsByMsg(msg);
        return ResponseEntity.ok(pets);
    }

    @RequestMapping(path = "/doctors", method = RequestMethod.POST)
    public ResponseEntity addDocs(@RequestBody Doctors docs) {
        docs.setId(UUID.randomUUID().toString());
        docService.addDocs(docs);
        return ResponseEntity.ok(docs);
    }

    @RequestMapping(path = "/pets", method = RequestMethod.GET)
    public ResponseEntity listPets() {
        List<Pets> voList = petsService.listPets();
        return ResponseEntity.ok(voList);
    }

    @RequestMapping(path = "/vets", method = RequestMethod.GET)
    public ResponseEntity listVets() {
        List<Vets> voList = vetsService.listVets();
        return ResponseEntity.ok(voList);
    }

    @RequestMapping(path = "/doctors", method = RequestMethod.GET)
    public ResponseEntity listDocs() {
        List<Doctors> voList = docService.listDocs();
        return ResponseEntity.ok(voList);
    }

    @RequestMapping(path = "/xa/{petMsg}/{docMsg}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity testXA(@PathVariable("petMsg") String petMsg, @PathVariable("docMsg") String docMsg) {
        Doctors d1 = new Doctors();
        d1.setId(UUID.randomUUID().toString());
        d1.setMsg(docMsg);
        docService.addDocs(d1);

        Pets p1 = new Pets();
        p1.setId(UUID.randomUUID().toString());
        p1.setMsg(petMsg);
        petsService.addPets(p1);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/xaError", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity testXAError() {
        Doctors d1 = new Doctors();
        d1.setId(UUID.randomUUID().toString());
        d1.setMsg("Test Msg Not to persist");
        docService.addDocs(d1);

        Pets p1 = new Pets();
        p1.setId(UUID.randomUUID().toString());
        p1.setMsg(null);
        petsService.addPets(p1);
        return ResponseEntity.ok().build();
    }

}
