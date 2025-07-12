package com.peter.tanxuannewapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.peter.tanxuannewapp.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>, QuerydslPredicateExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByRefreshTokenAndEmail(String refreshToken, String email);
    boolean existsByEmail(String email);
}
