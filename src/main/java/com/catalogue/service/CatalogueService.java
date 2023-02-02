package com.catalogue.service;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogueService {
// TODO: Implement error handling for the save operation

    Flux<CatalogueItemResponse> getCatalogueItems();

    Mono<CatalogueItemResponse> createCatalogueItem(CatalogueItem catalogueItem);

    Mono<CatalogueItemResponse> findById(Long id);

    Mono<CatalogueItemResponse> findBySku(String sku);

    Mono<CatalogueItemResponse> updateCatalogueItem(String sku, CatalogueItem catalogueItem);

    Mono<Void> removeCatalogueItem(String sku);
}