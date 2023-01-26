package com.catalogue.service.impl;

import com.catalogue.dto.CatalogueResponseDto;
import com.catalogue.exceptions.ItemNotFoundException;
import com.catalogue.models.CatalogueItem;
import com.catalogue.repository.CatalogueRepository;
import com.catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueServiceImpl implements CatalogueService {

    private final CatalogueRepository catalogueRepository;

    @Override
    public Flux<CatalogueResponseDto> getCatalogueItems() {
        return catalogueRepository.findAll()
                .switchIfEmpty(Mono.defer(() -> {
                            log.warn("Database Empty");
                            return Mono.error(new ItemNotFoundException(HttpStatus.NO_CONTENT, "Database Empty"));
                        })
                )
                .map(catalogueItem -> CatalogueResponseDto.builder()
                        .id(catalogueItem.getId())
                        .sku(catalogueItem.getSku())
                        .name(catalogueItem.getName())
                        .description(catalogueItem.getDescription())
                        .category(catalogueItem.getCategory())
                        .price(catalogueItem.getPrice())
                        .updatedOn(catalogueItem.getUpdatedOn())
                        .build()
                );
    }

    @Override
    public Mono<CatalogueItem> createCatalogueItem(CatalogueItem catalogueItem) {
        return catalogueRepository.save(catalogueItem).log();
    }
}
