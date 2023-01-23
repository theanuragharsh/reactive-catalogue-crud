package com.catalogue.controller;

import com.catalogue.models.CatalogueItem;
import com.catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(CatalogueControllerAPIPaths.BASE_PATH)
public class CatalogueController {

    private final CatalogueService catalogueService;

    @GetMapping(path = CatalogueControllerAPIPaths.GET_ITEMS_STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<CatalogueItem> getCatalogueItems() {
        return catalogueService.getCatalogueItems()
                .delayElements(Duration.ofMillis(50));
    }
}
