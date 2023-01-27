package com.catalogue.service;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogueService {

    Flux<CatalogueItemResponse> getCatalogueItems();

    Mono<ResponseEntity<CatalogueItemResponse>> createCatalogueItem(CatalogueItem catalogueItem);

    Mono<CatalogueItemResponse> findById(Long id);
}