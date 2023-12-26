package com.example.demo2.domain.po;


import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.hibernate.reactive.repository.jpa.intercept.AbstractSpecificationInterceptor;
import io.micronaut.data.intercept.RepositoryMethodKey;
import io.micronaut.data.jpa.repository.criteria.Specification;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.operations.RepositoryOperations;
import org.hibernate.reactive.stage.Stage;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Runtime implementation of {@code Page find(Specification, Pageable)}.
 *
 * @author Denis Stepanov
 * @since 3.5.0
 */
@Internal
public class ReactiveFindPageSpecificationInterceptor2 extends AbstractSpecificationInterceptor {

    /**
     * Default constructor.
     *
     * @param operations The operations
     */
    protected ReactiveFindPageSpecificationInterceptor2(@NonNull RepositoryOperations operations) {
        super(operations);
    }

    protected final Pageable getPageable(MethodInvocationContext<?, ?> context) {
        final Object parameterValue = context.getParameterValues()[1];
        if (parameterValue instanceof Pageable) {
            return (Pageable) parameterValue;
        }
        return Pageable.UNPAGED;
    }

    @Override
    protected Publisher<?> interceptPublisher(RepositoryMethodKey methodKey, MethodInvocationContext<Object, Object> context) {
        if (context.getParameterValues().length != 2) {
            throw new IllegalStateException("Expected exactly 2 arguments to method");
        }
        Specification<Object> specification = getSpecification(context);
        final CriteriaBuilder criteriaBuilder = operations.getCriteriaBuilder();
        Class<Object> rootEntity = getRequiredRootEntity(context);
        final CriteriaQuery<Object> query = criteriaBuilder.createQuery(rootEntity);
        final Root<Object> root = query.from(rootEntity);
        final Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
        if (predicate != null) {
            query.where(predicate);
        }
        query.select(root);

        Pageable pageable = getPageable(context);
        final Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            query.orderBy(getOrders(sort, root, criteriaBuilder));
        }
        return operations.withSession(session -> {
            if (pageable.isUnpaged()) {
                return Mono.fromCompletionStage(() -> session.createQuery(query).getResultList())
                        .map(resultList -> Page.of(resultList, pageable, resultList.size()));
            }
            return Mono.fromCompletionStage(() -> {
                Stage.SelectionQuery<Object> q = session.createQuery(query);
                q.setFirstResult((int) pageable.getOffset());
                q.setMaxResults(pageable.getSize());
                return q.getResultList();
            }).map(resultList -> Page.of(resultList, pageable, resultList.size()));
        });
    }

}
