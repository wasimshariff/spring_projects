package com.example.postgres.service;

import com.example.postgres.model.Pets;
import com.example.postgres.repository.postgres.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PetsService {

    @Autowired
    private PetsRepository petsRepository;

    public void addPets(Pets vo) {
        petsRepository.save(vo);
    }

    public List<Pets> listPets() {
        List<Pets> pets = new ArrayList<>();
        petsRepository.findAll().forEach(pets::add);
        return pets;
    }

    public List<Pets> listPetsByMsg(String msg) {
        List<Pets> pets = petsRepository.getPetsCustom(msg);
        return pets;
    }
}
