package com.catalogue.util;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class CatalogueItemGenerator {

    private static final Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

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

    public static CatalogueItem updateItemPriceRequest() {
        return CatalogueItem.builder()
                .sku("TLG-SKU-1000")
                .price(1500.0)
                .createdOn(createItem().getCreatedOn())
                .updatedOn(now)
                .build();
    }

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
