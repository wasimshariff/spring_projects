package com.example.postgres.repository.hibernatedb;

import com.example.postgres.model.Doctors;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorsRepository extends CrudRepository<Doctors, String> {
}
