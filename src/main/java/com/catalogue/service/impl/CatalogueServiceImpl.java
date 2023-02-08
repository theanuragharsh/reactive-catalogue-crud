package com.catalogue.service.impl;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.exceptions.BadRequestException;
import com.catalogue.exceptions.DatabaseEmptyException;
import com.catalogue.exceptions.ItemNotFoundException;
import com.catalogue.mapper.CatalogueMapper;
import com.catalogue.models.CatalogueItem;
import com.catalogue.repository.CatalogueRepository;
import com.catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueServiceImpl implements CatalogueService {
// TODO: Implement error handling for the save operation

    private final CatalogueRepository catalogueRepository;
    private final CatalogueMapper catalogueMapper;

    @Override
    public Flux<CatalogueItemResponse> getCatalogueItems() {
        log.debug("Finding CatalogueItems");
        return this.catalogueRepository.findAll()
                .map(buildCatalogueItemResponseFromItemFunction())
                .switchIfEmpty(Mono.error(() -> {
                            log.warn("Database empty!");
                            return new DatabaseEmptyException("Database empty!");
                        })
                );
    }

    @Override
    public Mono<CatalogueItemResponse> findById(Long id) {
        if (id == null) {
            return Mono.error(new BadRequestException("ID must be a numerical value and not null"));
        }
        log.debug("Finding CatalogueItem with id: {}", id);
        return this.catalogueRepository.findById(id)
                .map(catalogueMapper::toCatalogueResponse)
                .switchIfEmpty(Mono.error(() -> {
                    log.warn("ID {} was not found", id);
                    return new ItemNotFoundException("Content not found");
                }))
                .doOnSuccess(item -> log.info("Catalogue Item {} found", id));
    }

    @Override
    public Mono<CatalogueItemResponse> findBySku(String sku) {
        log.debug("Finding CatalogueItem with sku: {}", sku);
        return this.catalogueRepository.findBySku(sku).switchIfEmpty(Mono.defer(() -> {
            log.warn("SKU {} was not found", sku);
            return Mono.error(new ItemNotFoundException("SKU not found"));
        })).map(catalogueItem -> {
            log.info("Catalogue Item {} found", sku);
            return catalogueMapper.toCatalogueResponse(catalogueItem);
        });
    }

    @Override
    public Mono<CatalogueItemResponse> createCatalogueItem(CatalogueItem catalogueItem) {
        return this.catalogueRepository.save(catalogueItem).map(item -> {
            catalogueItem.setCreatedOn(Instant.now());
            log.info("Catalogue Item : {} Created", catalogueItem.getId());
            return catalogueMapper.toCatalogueResponse(catalogueItem);
        });
    }


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
/*    @Override
    public Mono<CatalogueItemResponse> updateCatalogueItem(String sku, CatalogueItem catalogueItem) {
        return this.catalogueRepository.findBySku(sku)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Catalogue Item {} was not found", sku);
                    return Mono.error(new ItemNotFoundException(HttpStatus.NOT_FOUND, "Content not found"));
                }))
                .doOnNext(existingItem -> {
                    log.info("Catalogue Item {} found and updated", sku);
                    existingItem.setPrice(catalogueItem.getPrice());
                    existingItem.setUpdatedOn(Instant.now());
                })
                .flatMap(catalogueRepository::save)
                .map(buildCatalogueItemResponseFromItemFunction());
    }*/
    @Override
    public Mono<CatalogueItemResponse> updateCatalogueItem(String sku, CatalogueItem catalogueItem) {
        return this.catalogueRepository.findBySku(sku)
                .switchIfEmpty(Mono.error(new ItemNotFoundException("Content not found")))
                .flatMap(existingItem -> {
                    log.info(" Item {} found : updating", sku);
                    existingItem.setPrice(catalogueItem.getPrice());
                    existingItem.setUpdatedOn(Instant.now());
                    return catalogueRepository.save(existingItem);
                })
                .map(buildCatalogueItemResponseFromItemFunction())
                .onErrorResume(e -> {
                    if (e instanceof ItemNotFoundException) {
                        log.info(" Item {} not found", sku);
                    }
                    return Mono.error(e);
                });
    }

    /**
     * This method deletes a {@link CatalogueItem} from the repository based on the sku.
     *
     * @param sku The sku of the {@link CatalogueItem} to be deleted.
     * @return A Mono of type Void, which returns an empty Mono if the item was successfully deleted,
     * or a Mono error if the item with the specified sku was not found.
     */
    @Override
    public Mono<Void> removeCatalogueItem(String sku) {
        return this.catalogueRepository.deleteBySku(sku)
                .doOnError(ItemNotFoundException.class, ex -> log.warn("Catalogue Item {} was not found", sku))
                .then();
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

