package com.catalogue.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor()
@Table("CATALOGUE_ITEMS")
public class CatalogueItem {

    @Id
    private Long id;
    @NotNull
    @Column(value = "SKU_NUMBER")
    private String sku;
    @NotEmpty
    @Column(value = "ITEM_NAME")
    private String name;
    @NotEmpty
    @Column(value = "DESCRIPTION")
    private String description;
    @NotEmpty
    @Column(value = "CATEGORY")
    private String category;
    @NotNull
    @Column(value = "PRICE")
    private Double price;
    @NotNull
    @Column(value = "INVENTORY")
    private Integer inventory;
    @CreatedDate
    @Column(value = "CREATED_ON")
    private Instant createdOn;
    @LastModifiedDate
    @Column(value = "UPDATED_ON")
    private Instant updatedOn;

}