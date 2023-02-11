package com.catalogue.controller;

import com.catalogue.dto.ApiErrorResponse;
import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.exceptions.DatabaseEmptyException;
import com.catalogue.exceptions.ItemNotFoundException;
import com.catalogue.models.CatalogueItem;
import com.catalogue.service.CatalogueService;
import com.catalogue.util.CatalogueItemGenerator;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CatalogueControllerTest {

    @MockBean
    private CatalogueService catalogueService;
    @Autowired
    private WebTestClient webTestClient;

    private final Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
    private final CatalogueItem catalogueItem = CatalogueItemGenerator.createItem();
    private final CatalogueItemResponse catalogueItemResponse = CatalogueItemGenerator.createItemResponse();
    private final CatalogueItem updateItemRequest = CatalogueItemGenerator.updateItemPriceRequest();
    private final CatalogueItemResponse updatedItemResponse = CatalogueItemGenerator.updatedItemPriceResponse();

    @Test
    @Order(1)
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
    @Order(2)
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
    @Order(3)
    public void testFindById() {

        when(catalogueService.findById(1000L)).thenReturn(Mono.just(catalogueItemResponse));
        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/id/1000").exchange().expectStatus().is2xxSuccessful().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(catalogueItemResponse)
                .expectComplete()
                .verify();
    }

    @Test
    @Order(40)
    public void testFindByIdWhenItemNotFound() {

        when(catalogueService.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.error(() -> new ItemNotFoundException("Content not found")));
        StepVerifier.create(catalogueService.findById(1L))
                .expectError(ItemNotFoundException.class)
                .verify();
    }

    @Test
    @Order(50)
    public void testFindBySku() {

        when(catalogueService.findBySku("TLG-SKU-0010")).thenReturn(Mono.just(catalogueItemResponse));

        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/sku/TLG-SKU-0010").exchange().expectStatus().is2xxSuccessful().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(catalogueItemResponse)
                .expectComplete().verify();
    }

    @Test
    @Order(60)
    public void testCreateCatalogueItem() {
        // Given
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
    @Order(70)
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
    @Order(80)
    public void testUpdateCatalogueItem() {
        String sku = updateItemRequest.getSku();

        when(catalogueService.updateCatalogueItem(sku, updateItemRequest)).thenReturn(Mono.just(updatedItemResponse));

        webTestClient
                .put()
                .uri(String.format("/api/v1/%s", sku))
                .body(Mono.just(updateItemRequest), CatalogueItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CatalogueItemResponse.class)
                .isEqualTo(updatedItemResponse);
    }
}
