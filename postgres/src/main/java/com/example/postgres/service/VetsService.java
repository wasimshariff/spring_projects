package com.example.postgres.service;

import com.example.postgres.model.Vets;
import com.example.postgres.repository.postgres.VetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VetsService {

    @Autowired
    private VetsRepository vetsRepository;

    public void addPets(Vets vo) {
        vetsRepository.save(vo);
    }

    public List<Vets> listVets() {
        List<Vets> vets = new ArrayList<>();
        vetsRepository.findAll().forEach(vets::add);
        return vets;
    }
}
