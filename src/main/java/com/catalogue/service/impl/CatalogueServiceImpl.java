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

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

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
                .map(buildCatalogueItemResponseFromItemFunction());
    }

    @Override
    public Mono<CatalogueItemResponse> createCatalogueItem(CatalogueItem catalogueItem) {
        return catalogueRepository.save(catalogueItem).map(item -> {
            log.info("Catalogue Item : {} Created", catalogueItem.getId());
            return catalogueMapper.toCatalogueResponse(catalogueItem);
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
        });
    }

    @Override
    public Mono<CatalogueItemResponse> findBySku(String sku) {
        return catalogueRepository.findBySku(sku).switchIfEmpty(Mono.defer(() -> {
            log.warn("Catalogue Item {} was not found...processing", sku);
            return Mono.error(new ItemNotFoundException(HttpStatus.NOT_FOUND, "Content not found"));
        })).map(catalogueItem -> {
            log.info("Catalogue Item {} found", sku);
            return catalogueMapper.toCatalogueResponse(catalogueItem);
        });
    }

    @Override
/*    public Mono<CatalogueItemResponse> updateCatalogueItem(String sku, CatalogueItem catalogueItem) {
        return catalogueRepository.findBySku(sku).flatMap(existingItem -> {
            log.info("Catalogue Item {} found", sku);
            existingItem.setPrice(catalogueItem.getPrice());
            existingItem.setUpdatedOn(Instant.now());
            return catalogueRepository.save(existingItem).map(buildCatalogueItemResponseFromItemFunction());
        });
    }*/
/*    In this optimized version, we have used the doOnNext operator instead of flatMap to update the existingItem.
    This way we can avoid unnecessary flatMap operations.
    Additionally, the use of a method reference to the
    catalogueRepository.save method helps make the code more concise and readable.
  */

    public Mono<CatalogueItemResponse> updateCatalogueItem(String sku, CatalogueItem catalogueItem) {
        return catalogueRepository.findBySku(sku)
                .doOnNext(existingItem -> {
                    log.info("Catalogue Item {} found and updated", sku);
                    existingItem.setPrice(catalogueItem.getPrice());
                    existingItem.setUpdatedOn(Instant.now());
                })
                .flatMap(catalogueRepository::save)
                .map(buildCatalogueItemResponseFromItemFunction());
    }

    /**
     * @return Function to map CatalogueItem into CatalogueItemResponse object using builder pattern
     */
    private final Function<CatalogueItem, CatalogueItemResponse> buildCatalogueItemResponseFromItemFunction() {
        return catalogueItem ->
                CatalogueItemResponse.builder()
                        .id(catalogueItem.getId())
                        .category(catalogueItem.getCategory())
                        .sku(catalogueItem.getSku())
                        .description(catalogueItem.getDescription())
                        .name(catalogueItem.getName())
                        .price(catalogueItem.getPrice())
                        .createdOn(catalogueItem.getCreatedOn())
                        .updatedOn(catalogueItem.getUpdatedOn())
                        .build();
    }
}

