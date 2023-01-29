package com.catalogue.controller;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import com.catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(CatalogueControllerApiPaths.BASE_PATH)
public class CatalogueController {

    private final CatalogueService catalogueService;

    /**
     * Get Catalogue Items available in database
     *
     * @return catalogueItems
     */
    @GetMapping(path = CatalogueControllerApiPaths.GET_ITEMS_STREAM, produces = MediaType.APPLICATION_NDJSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<CatalogueItemResponse> getCatalogueItems() {
        return catalogueService.getCatalogueItems();
    }

    /**
     * Create Catalogue Item
     *
     * @param catalogueItem
     * @return created CatalogueItem
     */

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = CatalogueControllerApiPaths.CREATE)
    public Mono<ResponseEntity<CatalogueItemResponse>> createCatalogueItem(@RequestBody CatalogueItem catalogueItem) {
        return catalogueService.createCatalogueItem(catalogueItem);
    }

    /**
     * Find By id
     *
     * @param id
     * @return CatalogueItem
     */
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = CatalogueControllerApiPaths.GET_ITEM_BY_ID, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<CatalogueItemResponse> findById(@PathVariable Long id) {
        return catalogueService.findById(id);
    }

    /**
     * Find By sku
     *
     * @param sku
     * @return CatalogueItem
     */
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = CatalogueControllerApiPaths.GET_ITEM_BY_SKU)
    public Mono<CatalogueItemResponse> findBySku(@PathVariable String sku) {
        return catalogueService.findBySku(sku);
    }
}
