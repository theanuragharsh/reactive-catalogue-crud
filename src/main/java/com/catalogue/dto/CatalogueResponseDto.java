package com.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogueResponseDto {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private Double price;
}
