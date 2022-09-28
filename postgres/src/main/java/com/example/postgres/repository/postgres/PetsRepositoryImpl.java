package com.example.postgres.repository.postgres;

import com.example.postgres.model.Pets;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class PetsRepositoryImpl implements PetsRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Pets> getPetsCustom(String msg) {
       List<Pets> pets = em.createQuery(
                "SELECT c FROM Pets c WHERE c.msg LIKE :custName")
                .setParameter("custName", msg)
                .getResultList();
        return pets;
    }
}
