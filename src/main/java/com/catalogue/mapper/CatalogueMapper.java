package com.catalogue.mapper;

import com.catalogue.dto.CatalogueItemResponse;
import com.catalogue.models.CatalogueItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogueMapper {
    // TODO: Implement error handling for the save operation

    CatalogueItemResponse toCatalogueResponse(CatalogueItem catalogueItem);
    CatalogueItem toCatalogue( CatalogueItemResponse catalogueItemResponse);
}
