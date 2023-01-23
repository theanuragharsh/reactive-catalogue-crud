package com.catalogue.service;

import com.catalogue.models.CatalogueItem;
import reactor.core.publisher.Flux;

public interface CatalogueService {

    Flux<CatalogueItem> getCatalogueItems();
}