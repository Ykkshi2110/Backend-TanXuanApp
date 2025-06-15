package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    boolean existsByName(String name);
}
