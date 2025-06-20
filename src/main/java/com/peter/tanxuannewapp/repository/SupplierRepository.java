package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer>, QuerydslPredicateExecutor<Supplier> {
    boolean existsByName(String name);
}
