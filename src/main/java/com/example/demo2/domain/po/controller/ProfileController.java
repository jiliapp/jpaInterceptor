package com.example.demo2.domain.po.controller;


import com.example.demo2.domain.po.Profile;
import com.example.demo2.domain.po.repository.ProfileRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.jpa.repository.criteria.Specification;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static io.vertx.sqlclient.data.NullValue.LocalDateTime;

@Controller
public class ProfileController {



    @Inject
    ProfileRepository profileRepository;

    @Get("/profile1")
    public Mono<List<Profile>> info1(){
      Mono<Page> page=  profileRepository.findAll(new Specification() {
          @Override
          public @Nullable Predicate toPredicate(@NonNull Root root, @NonNull CriteriaQuery query, @NonNull CriteriaBuilder builder) {
              List<Predicate> predicates = new ArrayList <Predicate>();
              Path deletedAtPath = root.get("deletedAt");
              predicates.add(builder.isNull(deletedAtPath));
              Predicate[] predicateList=predicates.toArray(new Predicate[0]);
              return builder.createQuery().where(predicateList).getRestriction();
          }
      }, Pageable.from(0, 2));
      return page.map(Page::getContent);
    }

    @Get("/profile2")
    public Mono<List<Profile>> info2(){
        Mono<Page> page=  profileRepository.findSlice(new Specification() {
            @Override
            public @Nullable Predicate toPredicate(@NonNull Root root, @NonNull CriteriaQuery query, @NonNull CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList <Predicate>();
                Path deletedAtPath = root.get("deletedAt");
                predicates.add(builder.isNull(deletedAtPath));
                Predicate[] predicateList=predicates.toArray(new Predicate[0]);
                return builder.createQuery().where(predicateList).getRestriction();
            }
        }, Pageable.from(0, 2));
        return page.map(Page::getContent);
    }


}
