package com.example.postgres.repository.postgres;

import com.example.postgres.model.Pets;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetsRepository extends CrudRepository<Pets, String>, PetsRepositoryCustom {
}
