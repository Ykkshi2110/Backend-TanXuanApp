package com.peter.tanxuannewapp.repository;

import com.peter.tanxuannewapp.domain.QUser;
import com.peter.tanxuannewapp.domain.User;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    boolean existsByEmail(String email);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByRefreshTokenAndEmail(String refreshToken, String email);

    @Override
    default void customize(QuerydslBindings querydslBindings, QUser qUser) {
        querydslBindings.bind(qUser.name).first(StringExpression::containsIgnoreCase);
    }
}
