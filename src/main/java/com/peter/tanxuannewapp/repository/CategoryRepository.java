package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);
}
