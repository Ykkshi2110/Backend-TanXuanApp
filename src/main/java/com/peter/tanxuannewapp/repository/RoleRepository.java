package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
    boolean existsByName(String name);
}
