package com.catalogue.repository;

import com.catalogue.models.CatalogueItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CatalogueRepository extends ReactiveCrudRepository<CatalogueItem, Long> {
    Mono<CatalogueItem> findBySku(String sku);
}
