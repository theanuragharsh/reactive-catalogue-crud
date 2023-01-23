package com.catalogue.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@Table("CATALOGUE_ITEMS")
public class CatalogueItem {

    @Id
    private Long id;
    private String sku;
    private String name;
}