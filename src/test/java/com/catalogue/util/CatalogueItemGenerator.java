package com.catalogue.util;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;

@UtilityClass
public class CatalogueItemGenerator {

    private static final Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

    /**
     * Generate sample Catalogue Item which will be used in test classes
     *
     * @return Flux<CatalogueItem>
     */
    public static Flux<CatalogueItem> generateCatalogueItemList() {
        return Flux.fromStream(LongStream.range(1, 100).mapToObj(value -> createItem(value)));
    }

    public static CatalogueItem createItem(Long id) {
        return CatalogueItem.builder()
                .id(id)
                .sku("TLG-SKU-1000")
                .name("ITEM 1000")
                .description("ITEM DESC 1000")
                .category("Books")
                .price(1000.0)
                .inventory(1000)
                .createdOn(now)
                .updatedOn(null)
                .build();
    }

    /**
     * Generate sample Catalogue Item which will be used in test classes
     *
     * @return Flux<CatalogueItemResponse>
     */
    public static Flux<CatalogueItemResponse> generateCatalogueItemResponseList() {
        return Flux.fromStream(LongStream.range(1, 100).mapToObj(value -> createItemResponse(value)));
    }

    public static CatalogueItemResponse createItemResponse(Long id) {
        return CatalogueItemResponse.builder()
                .id(id)
                .sku("TLG-SKU-1000")
                .name("ITEM 1000")
                .description("ITEM DESC 1000")
                .category("Books")
                .price(1000.0)
                .createdOn(createItem().getCreatedOn())
                .updatedOn(createItem().getUpdatedOn())
                .build();
    }

    /**
     * Generate sample Catalogue Item which will be used in createItem Test Case
     *
     * @return CatalogueItem
     */
    public static CatalogueItem createItem() {
        return CatalogueItem.builder()
                .id(1000L)
                .sku("TLG-SKU-1000")
                .name("ITEM 1000")
                .description("ITEM DESC 1000")
                .category("Books")
                .price(1000.0)
                .inventory(1000)
                .createdOn(now)
                .updatedOn(null)
                .build();
    }

    /**
     * Generate sample Catalogue Item Response which will be used in createItem Test Case
     *
     * @return created CatalogueItemResponse
     */
    public static CatalogueItemResponse createItemResponse() {
        return CatalogueItemResponse.builder()
                .id(1000L)
                .sku("TLG-SKU-1000")
                .name("ITEM 1000")
                .description("ITEM DESC 1000")
                .category("Books")
                .price(1000.0)
                .createdOn(createItem().getCreatedOn())
                .updatedOn(createItem().getUpdatedOn())
                .build();
    }

    /**
     * Generate sample Catalogue Item which will be used in updateItemBySKu Test Case
     *
     * @return update CatalogueItem
     */
    public static CatalogueItem updateItemPriceRequest() {
        return CatalogueItem.builder()
                .sku("TLG-SKU-1000")
                .price(1500.0)
                .createdOn(createItem().getCreatedOn())
                .updatedOn(now)
                .build();
    }

    /**
     * Generate sample Catalogue Item which will be used in updateItemBySKu Test Case
     *
     * @return updated CatalogueItemResponse
     */
    public static CatalogueItemResponse updatedItemPriceResponse() {
        return CatalogueItemResponse.builder()
                .id(1000L)
                .sku("TLG-SKU-1000")
                .name("ITEM 1000")
                .description("ITEM DESC 1000")
                .category("Books")
                .price(1500.0)
                .createdOn(createItem().getCreatedOn())
                .updatedOn(updateItemPriceRequest().getUpdatedOn())
                .build();
    }
}
