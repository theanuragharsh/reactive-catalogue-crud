package com.catalogue.service.impl;

import com.catalogue.models.CatalogueItem;
import com.catalogue.repository.CatalogueRepository;
import com.catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueServiceImpl implements CatalogueService {

    private final CatalogueRepository catalogueRepository;

    @Override
    public Flux<CatalogueItem> getCatalogueItems() {
        return catalogueRepository.findAll()
                .switchIfEmpty(Mono.error(new ClassNotFoundException("ABC")));
//        .switchIfEmpty(Mono.defer(() -> {
//            log.warn("No data found in database");
//            return Mono.empty();
//        }));

    }
}
