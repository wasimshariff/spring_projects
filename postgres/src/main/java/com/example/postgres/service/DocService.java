package com.example.postgres.service;

import com.example.postgres.model.Doctors;
import com.example.postgres.model.Vets;
import com.example.postgres.repository.hibernatedb.DoctorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocService {

    @Autowired
    private DoctorsRepository doctorsRepository;

    public void addDocs(Doctors vo) {
        doctorsRepository.save(vo);
    }

    public List<Doctors> listDocs() {
        List<Doctors> docs = new ArrayList<>();
        doctorsRepository.findAll().forEach(docs::add);
        return docs;
    }


}
