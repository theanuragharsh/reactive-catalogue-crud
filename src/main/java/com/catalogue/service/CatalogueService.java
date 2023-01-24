package com.catalogue.service;

import com.catalogue.models.CatalogueItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogueService {

    Flux<CatalogueItem> getCatalogueItems();

    Mono<CatalogueItem> createCatalogueItem(CatalogueItem catalogueItem);
}