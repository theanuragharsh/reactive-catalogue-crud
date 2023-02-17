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
import org.springframework.http.MediaType;
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

@WebFluxTest(controllers = CatalogueController.class)
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
    private final Flux<CatalogueItem> catalogueItemFlux = CatalogueItemGenerator.generateCatalogueItemList();
    private final CatalogueItemResponse catalogueItemResponse = CatalogueItemGenerator.createItemResponse();
    private final Flux<CatalogueItemResponse> catalogueItemResponseFlux = CatalogueItemGenerator.generateCatalogueItemResponseList();
    private final CatalogueItem updateItemRequest = CatalogueItemGenerator.updateItemPriceRequest();
    private final CatalogueItemResponse updatedItemResponse = CatalogueItemGenerator.updatedItemPriceResponse();

    @Test
    @Order(10)
    public void testGetCatalogueItems() {

        when(catalogueService.getCatalogueItems()).thenReturn(catalogueItemResponseFlux);

        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/stream").exchange().expectStatus().isOk().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextCount(99)
                .expectComplete()
                .verify();
    }

    @Test
    @Order(20)
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
    @Order(30)
    public void testFindById() {

        when(catalogueService.findById(1000L)).thenReturn(Mono.just(catalogueItemResponse));
        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/id/{id}", 1000).exchange().expectStatus().is2xxSuccessful().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(catalogueItemResponse)
                .expectComplete()
                .verify();
    }

    @Test
    @Order(40)
    public void testFindByIdWhenItemNotFound() {

        when(catalogueService.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.error(new ItemNotFoundException("Content not found")));
        StepVerifier.create(catalogueService.findById(1L))
                .expectErrorMatches(throwable -> throwable instanceof ItemNotFoundException &&
                        ((ItemNotFoundException) throwable).getReason().equals("Content not found"))
                .verify();
    }

    @Test
    @Order(50)
    public void testFindBySku() {

        when(catalogueService.findBySku(ArgumentMatchers.any())).thenReturn(Mono.just(catalogueItemResponse));

        Flux<CatalogueItemResponse> responseBody = webTestClient.get().uri("/api/v1/sku/{sku}", "TLG-SKU-0010").exchange().expectStatus().is2xxSuccessful().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext(catalogueItemResponse)
                .expectComplete().verify();
    }

    @Test
    public void testFindBySkuWhenNotPresent() {
        when(catalogueService.findBySku(ArgumentMatchers.any())).thenReturn(Mono.error(new ItemNotFoundException("SKU not found")));
        Flux<ApiErrorResponse> responseBody = webTestClient.get().uri("/api/v1/sku/{sku}", "TLG-SKU-0010").exchange()
                .expectStatus().isNotFound().returnResult(ApiErrorResponse.class).getResponseBody();
    }

    @Test
    @Order(60)
    public void testCreateCatalogueItem() {
        // Given
        when(catalogueService.createCatalogueItem(catalogueItem)).thenReturn(Mono.just(catalogueItemResponse));

        // When
        StepVerifier.create(webTestClient.post().uri("/api/v1/")
                        .accept(MediaType.APPLICATION_JSON)
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
                .uri("/api/v1/{sku}", "TLG-SKU-0010")
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
                .uri("/api/v1/{sku}", sku)
                .body(Mono.just(updateItemRequest), CatalogueItem.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CatalogueItemResponse.class)
                .isEqualTo(updatedItemResponse);
    }
}
