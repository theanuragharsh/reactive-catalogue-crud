package com.catalogue.mapper;

import com.catalogue.dto.CatalogueResponseDto;
import com.catalogue.models.CatalogueItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogueMapper {
    CatalogueResponseDto toCatalogueResponse(CatalogueItem catalogueItem);
    CatalogueItem toCatalogue( CatalogueResponseDto catalogueResponseDto);
}
