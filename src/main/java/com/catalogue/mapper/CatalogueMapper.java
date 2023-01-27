package com.catalogue.mapper;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogueMapper {
    CatalogueItemResponse toCatalogueResponse(CatalogueItem catalogueItem);
    CatalogueItem toCatalogue( CatalogueItemResponse catalogueItemResponse);
}
