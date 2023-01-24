package com.catalogue.controller;

import com.catalogue.models.CatalogueItem;
import com.catalogue.service.CatalogueService;
import jakarta.validation.Valid;
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
@RequestMapping(CatalogueControllerAPIPaths.BASE_PATH)
public class CatalogueController {

    private final CatalogueService catalogueService;

    @GetMapping(path = CatalogueControllerAPIPaths.GET_ITEMS_STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<CatalogueItem> getCatalogueItems() {
        return catalogueService.getCatalogueItems();
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = CatalogueControllerAPIPaths.CREATE)
    public Mono<ResponseEntity> createCatalogueItem(@RequestBody(required = true) @Valid CatalogueItem catalogueItem) {
        return catalogueService.createCatalogueItem(catalogueItem)
                .map(catalogueItem1 -> ResponseEntity.status(HttpStatus.CREATED).body(catalogueItem));
    }
}
