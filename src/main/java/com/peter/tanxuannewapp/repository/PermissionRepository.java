package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer>, QuerydslPredicateExecutor<Permission> {
 boolean existsByNameAndMethodAndRoute(String name, String method,  String route);
 List<Permission> findByIdIn(List<Integer> ids);
}
