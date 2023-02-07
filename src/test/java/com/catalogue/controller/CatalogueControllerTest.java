package com.catalogue.controller;

import com.catalogue.dto.CatalogueItemResponse;
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
    public void findByIdTest() {

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
    public void findBySku() {

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
    public void createCatalogueItemTest() {
        Mono<CatalogueItem> catalogueItem = Mono.just(CatalogueItem.builder()
                .id(1000L).sku("TLG-SKU-0010").name("ITEM 0010").description("ITEM DESC 0010").category("Books").price(1000.0).inventory(10).createdOn(now)
                .build());
        when(catalogueService.createCatalogueItem(new CatalogueItem(1000L, "TLG-SKU-0010", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, 10, now, now)));
        Flux<CatalogueItemResponse> responseBody = webTestClient.post().uri("/api/v1/").exchange().expectStatus().isCreated().returnResult(CatalogueItemResponse.class).getResponseBody();
        StepVerifier.create(responseBody).expectNext(new CatalogueItemResponse(1000L, "TLG-SKU-0010", "ITEM 0010", "ITEM DESC 0010", "Books", 1000.0, now, now))
                .expectComplete().verify();
    }


/*    @Test
    public void getCatalogueItemsWhenNoItemPresent() {

        Mono<ApiErrorResponse> apiErrorResponseMono = Mono.just(ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now()).category("API_ERROR").status(HttpStatus.NO_CONTENT).message("Database Empty")
                .build());

        when(catalogueService.getCatalogueItems());
//                .thenReturn(new ApiErrorResponse(LocalDateTime.now(),"API_ERROR",HttpStatus.NO_CONTENT,"Database Empty"));

        Flux<ApiErrorResponse> responseBody = webTestClient.get().uri("/api/v1/stream").exchange().expectStatus().isNoContent().returnResult(ApiErrorResponse.class).getResponseBody();
//                .expectStatus().isNoContent().returnResult(ApiErrorResponse.class).getResponseBody();
        StepVerifier
                .create(responseBody)
                .expectNext(new ApiErrorResponse(LocalDateTime.now(), "API_ERROR", HttpStatus.NO_CONTENT, "Database Empty"))
                .expectComplete()
                .verify();
    }*/
}
