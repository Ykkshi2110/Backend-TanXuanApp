package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Category;
import com.peter.tanxuannewapp.domain.QCategory;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, QuerydslPredicateExecutor<Category>, QuerydslBinderCustomizer<QCategory> {
    boolean existsByName(String name);

    @Override
    default void customize(QuerydslBindings querydslBindings, QCategory qCategory) {
        querydslBindings.bind(qCategory.name).first(StringExpression::containsIgnoreCase);
    }
}
