## Micronaut JPA 自定义拦截器 实现 Mono<Page<T>> findAll(@Nullable Specification<T> spec, Pageable pageable);


ReactiveFindPageSpecificationInterceptor2 拦截器  分页-去掉count统计总数
````ignorelang


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


````


BaseJpaSpecificationExecutor 拦截器组装
````ignorelang

public interface BaseJpaSpecificationExecutor<T> extends ReactorJpaSpecificationExecutor {


    @DataMethod(interceptor = ReactiveFindPageSpecificationInterceptor2.class)
    Publisher<Page<T>> findSlice(@Nullable Specification<T> spec, Pageable pageable);

}

````

再定义一层 就不会报错：No root entity present in method
````ignorelang
public interface BaseJpaSpecificationExecutor2<T> extends BaseJpaSpecificationExecutor<T> {

    Mono<Page<T>> findSlice(@Nullable Specification<T> spec, Pageable pageable);


}


````
使用 就不报错啦
````ignorelang

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder.class)
public interface ProfileRepository extends ReactorPageableRepository<Profile,Long>,  BaseJpaSpecificationExecutor2<Profile> {



}


````