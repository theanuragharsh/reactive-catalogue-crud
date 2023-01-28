package com.catalogue.service.impl;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.exceptions.ItemNotFoundException;
import com.catalogue.mapper.CatalogueMapper;
import com.catalogue.models.CatalogueItem;
import com.catalogue.repository.CatalogueRepository;
import com.catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueServiceImpl implements CatalogueService {

    private final CatalogueRepository catalogueRepository;
    private final CatalogueMapper catalogueMapper;

    @Override
    public Flux<CatalogueItemResponse> getCatalogueItems() {
        return catalogueRepository.findAll()
                .switchIfEmpty(Mono.defer(() -> {
                            log.warn("Database Empty");
                            return Mono.error(new ItemNotFoundException(HttpStatus.NO_CONTENT, "Database Empty"));
                        })
                )
                .map(catalogueItem -> CatalogueItemResponse.builder()
                        .id(catalogueItem.getId())
                        .sku(catalogueItem.getSku())
                        .name(catalogueItem.getName())
                        .description(catalogueItem.getDescription())
                        .category(catalogueItem.getCategory())
                        .price(catalogueItem.getPrice())
                        .createdOn(catalogueItem.getCreatedOn())
                        .updatedOn(catalogueItem.getUpdatedOn())
                        .build()
                );
    }

    @Override
    public Mono<ResponseEntity<CatalogueItemResponse>> createCatalogueItem(CatalogueItem catalogueItem) {
        return catalogueRepository.save(catalogueItem).map(item -> {
            log.info("Catalogue Item : {} Created", catalogueItem.getId());
            return new ResponseEntity<>(catalogueMapper
                    .toCatalogueResponse(catalogueItem), HttpStatus.CREATED);
        });
    }

    @Override
    public Mono<CatalogueItemResponse> findById(Long id) {
        return catalogueRepository.findById(id).switchIfEmpty(Mono.defer(() -> {
            log.warn("Catalogue Item {} was not found...processing", id);
            return Mono.error(new ItemNotFoundException(HttpStatus.NOT_FOUND, "Content not found"));
        })).map(catalogueItem -> {
            log.info("Catalogue Item {} found", id);
            return catalogueMapper.toCatalogueResponse(catalogueItem);
     /*       return CatalogueItemResponse.builder().id(catalogueItem.getId())
                    .sku(catalogueItem.getSku())
                    .name(catalogueItem.getName())
                    .description(catalogueItem.getDescription())
                    .category(catalogueItem.getCategory())
                    .price(catalogueItem.getPrice())
                    .createdOn(catalogueItem.getCreatedOn())
                    .updatedOn(catalogueItem.getUpdatedOn())
                    .build();*/
        });
    }
}
