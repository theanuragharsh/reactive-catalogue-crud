package com.catalogue.controller;

import com.catalogue.dto.ApiErrorResponse;
import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.exceptions.DatabaseEmptyException;
import com.catalogue.service.CatalogueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;

@WebFluxTest(CatalogueController.class)
@RunWith(SpringRunner.class)
public class CatalogueControllerTest {

    @MockBean
    private CatalogueService catalogueService;
    @Autowired
    private WebTestClient webTestClient;

    private final Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

    @Test
    public void getCatalogueItemsTest() {
        Flux<CatalogueItemResponse> catalogueItemResponseFlux = Flux
                .just(new CatalogueItemResponse(1000L, "TLG-SKU-0010", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now),
                        new CatalogueItemResponse(1111L, "TLG-SKU-0011", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now));

        when(catalogueService.getCatalogueItems()).thenReturn(catalogueItemResponseFlux);

        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/stream").exchange().expectStatus().isOk().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(new CatalogueItemResponse(1000L, "TLG-SKU-0010", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now))
                .expectNext(new CatalogueItemResponse(1111L, "TLG-SKU-0011", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now))
                .expectComplete()
                .verify();
    }

    @Test
    public void getCatalogueItemsWhenNoItemPresent() {
        when(catalogueService.getCatalogueItems()).thenThrow(new DatabaseEmptyException(HttpStatus.NO_CONTENT, "Database Empty"));

        Mono<ApiErrorResponse> apiErrorResponseMono = Mono
                .just(new ApiErrorResponse(LocalDateTime.now(), "API_ERROR", HttpStatus.NO_CONTENT, "Database Empty"));
        Flux<ApiErrorResponse> responseBody = webTestClient.get().uri("/api/v1/stream").exchange()
                .expectStatus().isNoContent().returnResult(ApiErrorResponse.class).getResponseBody();
        StepVerifier
                .create(responseBody)
                .expectNext(new ApiErrorResponse(LocalDateTime.now(), "API_ERROR", HttpStatus.NO_CONTENT, "Database Empty"))
                .expectComplete()
                .verify();
    }
}
