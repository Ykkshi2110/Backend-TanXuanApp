package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
 boolean existsByNameAndRoute(String name, String route);
 List<Permission> findByIdIn(List<Integer> ids);
}
