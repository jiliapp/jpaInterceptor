package com.example.demo2.domain.po.repository;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.hibernate.reactive.repository.jpa.ReactorJpaSpecificationExecutor;
import io.micronaut.data.hibernate.reactive.repository.jpa.intercept.ReactiveFindPageSpecificationInterceptor;
import io.micronaut.data.intercept.annotation.DataMethod;
import io.micronaut.data.jpa.repository.criteria.Specification;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface BaseJpaSpecificationExecutor2<T> extends BaseJpaSpecificationExecutor<T> {

    Mono<Page<T>> findSlice(@Nullable Specification<T> spec, Pageable pageable);


}
