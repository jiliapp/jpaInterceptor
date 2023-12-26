package com.example.demo2.domain.po.repository;


import com.example.demo2.domain.po.Profile;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.annotation.RepositoryConfiguration;
import io.micronaut.data.hibernate.reactive.repository.jpa.intercept.ReactiveFindPageSpecificationInterceptor;
import io.micronaut.data.intercept.annotation.DataMethod;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor;
import io.micronaut.data.jpa.repository.criteria.Specification;
import io.micronaut.data.jpa.repository.intercept.FindPageSpecificationInterceptor;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder.class)
public interface ProfileRepository extends ReactorPageableRepository<Profile,Long>,  BaseJpaSpecificationExecutor2<Profile> {



}
