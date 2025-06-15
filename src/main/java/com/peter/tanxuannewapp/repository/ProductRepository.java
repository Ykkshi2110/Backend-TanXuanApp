package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.Product;
import com.peter.tanxuannewapp.domain.QProduct;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface ProductRepository extends JpaRepository<Product, Integer>, QuerydslPredicateExecutor<Product>, QuerydslBinderCustomizer<QProduct> {
    boolean existsByName(String name);

    @Override
    default void customize(QuerydslBindings querydslBindings, QProduct qProduct) {
        querydslBindings.bind(qProduct.name).first(StringExpression::containsIgnoreCase);
    }
}
