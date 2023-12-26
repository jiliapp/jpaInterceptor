package com.example.demo2.domain.po.repository;

import com.example.demo2.domain.po.ReactiveFindPageSpecificationInterceptor2;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.hibernate.reactive.repository.jpa.ReactorJpaSpecificationExecutor;
import io.micronaut.data.hibernate.reactive.repository.jpa.intercept.ReactiveFindPageSpecificationInterceptor;
import io.micronaut.data.intercept.annotation.DataMethod;
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor;
import io.micronaut.data.jpa.repository.criteria.Specification;
import io.micronaut.data.jpa.repository.intercept.FindPageSpecificationInterceptor;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface BaseJpaSpecificationExecutor<T> extends ReactorJpaSpecificationExecutor {


    @DataMethod(interceptor = ReactiveFindPageSpecificationInterceptor2.class)
    Publisher<Page<T>> findSlice(@Nullable Specification<T> spec, Pageable pageable);

}
