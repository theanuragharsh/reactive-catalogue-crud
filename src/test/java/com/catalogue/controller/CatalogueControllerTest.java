package com.catalogue.controller;

import com.catalogue.dto.ApiErrorResponse;
import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.exceptions.DatabaseEmptyException;
import com.catalogue.models.CatalogueItem;
import com.catalogue.service.CatalogueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    public void testGetCatalogueItems() {
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
    public void testGetCatalogueItemsWhenDatabaseEmpty() {

        when(catalogueService.getCatalogueItems()).thenReturn(Flux.error(new DatabaseEmptyException("Database Empty")));

        webTestClient.get()
                .uri("/api/v1/stream")
                .exchange()
                .expectStatus().isNotFound()
                .returnResult(ApiErrorResponse.class)
                .getResponseBody().toStream().forEach(System.out::println);
    }

    @Test
    public void testFindById() {

        Mono<CatalogueItemResponse> catalogueItemResponse = Mono.just(CatalogueItemResponse.builder()
                .id(1000L).sku("TLG-SKU-0010").name("ITEM 0010").description("ITEM DESC 0010").category("Books").price(1000.0).createdOn(now).updatedOn(now)
                .build());

        when(catalogueService.findById(1000L)).thenReturn(catalogueItemResponse);
        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/id/1000").exchange().expectStatus().is2xxSuccessful().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(new CatalogueItemResponse(1000L, "TLG-SKU-0010", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now))
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindBySku() {

        Mono<CatalogueItemResponse> catalogueItemResponse = Mono.just(CatalogueItemResponse.builder()
                .id(1000L).sku("TLG-SKU-0010").name("ITEM 0010").description("ITEM DESC 0010").category("Books").price(1000.0).createdOn(now).updatedOn(now)
                .build());

        when(catalogueService.findBySku("TLG-SKU-0010")).thenReturn(catalogueItemResponse);

        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/sku/TLG-SKU-0010").exchange().expectStatus().is2xxSuccessful().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(new CatalogueItemResponse(1000L, "TLG-SKU-0010", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now))
                .expectComplete().verify();
    }

    @Test
    public void testCreateCatalogueItem() {
        // Given
        CatalogueItem catalogueItem = new CatalogueItem(1L, "TLG-SKU-0001", "ITEM 0001", "ITEM DESC 0001", "Books", 1000.0, 1, now, null);
        CatalogueItemResponse catalogueItemResponse = new CatalogueItemResponse(1L, "TLG-SKU-0001", "ITEM 0001", "ITEM DESC 0001", "Books", 1000.0, now, null);
        when(catalogueService.createCatalogueItem(catalogueItem)).thenReturn(Mono.just(catalogueItemResponse));

        // When
        StepVerifier.create(webTestClient.post().uri("/api/v1/")
                        .bodyValue(catalogueItem)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .returnResult(CatalogueItemResponse.class)
                        .getResponseBody())
                .expectNext(catalogueItemResponse)
                .expectComplete()
                .verify();
    }

    @Test
    public void testRemoveCatalogueItem() {

        given(catalogueService.removeCatalogueItem(any()))
                .willReturn(Mono.empty());
        webTestClient.
                delete()
                .uri("/api/v1/TLG-SKU-0010")
                .exchange()
                .expectStatus()
                .isNoContent()
                .returnResult(Void.class)
                .getResponseBody();
    }

    @Test
    public void testUpdateCatalogueItem() {
        CatalogueItem requestedCatalogueItem = new CatalogueItem(1L, "TLG-SKU-0001", "ITEM 0001", "ITEM DESC 0001", "Books", 1000.0, 1, now, now);

        CatalogueItemResponse expectedCatalogueItemResponse = CatalogueItemResponse.builder()
                .id(requestedCatalogueItem.getId())
                .category(requestedCatalogueItem.getCategory())
                .sku(requestedCatalogueItem.getSku())
                .description(requestedCatalogueItem.getDescription())
                .name(requestedCatalogueItem.getName())
                .price(requestedCatalogueItem.getPrice())
                .createdOn(requestedCatalogueItem.getCreatedOn())
                .updatedOn(requestedCatalogueItem.getUpdatedOn())
                .build();
        when(catalogueService
                .updateCatalogueItem(requestedCatalogueItem.getSku(), requestedCatalogueItem))
                .thenReturn(Mono.just(expectedCatalogueItemResponse));
        Flux<CatalogueItemResponse> responseBody = webTestClient
                .put()
                .uri("/api/v1/TLG-SKU-0001")
                .bodyValue(requestedCatalogueItem)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CatalogueItemResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody).expectNext(expectedCatalogueItemResponse)
                .expectComplete()
                .verify();
    }

}
