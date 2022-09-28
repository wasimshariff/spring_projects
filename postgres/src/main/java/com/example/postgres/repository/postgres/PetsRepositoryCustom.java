package com.example.postgres.repository.postgres;

import com.example.postgres.model.Pets;

import java.util.List;

public interface PetsRepositoryCustom {

    List<Pets> getPetsCustom(String msg);
}
