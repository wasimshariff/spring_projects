package com.example.postgres.repository.postgres;

import com.example.postgres.model.Vets;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VetsRepository extends CrudRepository<Vets, String> {
}
