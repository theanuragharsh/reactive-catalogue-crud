package com.catalogue.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@Table("CATALOGUE_ITEMS")
public class CatalogueItem {

    @Id
    private Long id;
    @Column(value = "SKU_NUMBER")
    @NotNull
    private String sku;
    @Column(value = "ITEM_NAME")
    @NotEmpty
    private String name;
    @Column(value = "DESCRIPTION")
    @NotEmpty
    private String description;
    @Column(value = "CATEGORY")
    @NotEmpty
    private String category;
    @Column(value = "PRICE")
    @NotNull
    private Double price;
    @Column(value = "INVENTORY")
    @NotNull
    private Integer inventory;
    @Column(value = "CREATED_ON")
    @NotNull
    private LocalDateTime created_on;
    @Column(value = "UPDATED_ON")
    private LocalDateTime updated_on;
}