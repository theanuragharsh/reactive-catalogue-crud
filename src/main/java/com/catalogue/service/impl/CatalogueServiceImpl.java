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
    private static final String DATABASE_EMPTY = "Database Empty";
    private static final String CONTENT_NOT_FOUND = "The provided Id does not matches";

    @Override
    public Flux<CatalogueResponseDto> getCatalogueItems() {
        return catalogueRepository.findAll()
                .switchIfEmpty(Mono.defer(() -> {
                            log.warn(DATABASE_EMPTY);
                            return Mono.error(new ItemNotFoundException(HttpStatus.NO_CONTENT, DATABASE_EMPTY));
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

    @Override
    public Mono<CatalogueResponseDto> findById(Long id) {
        return catalogueRepository.findById(id).switchIfEmpty(Mono.defer(() -> {
            log.warn("CONTENT_NOT_FOUND", id);
            return Mono.error(new ItemNotFoundException(HttpStatus.NOT_FOUND, CONTENT_NOT_FOUND));
        })).map(catalogueItem -> CatalogueResponseDto.builder().id(catalogueItem.getId())
                .sku(catalogueItem.getSku())
                .name(catalogueItem.getName())
                .description(catalogueItem.getDescription())
                .category(catalogueItem.getCategory())
                .price(catalogueItem.getPrice())
                .updatedOn(catalogueItem.getUpdatedOn())
                .build());
    }
}
