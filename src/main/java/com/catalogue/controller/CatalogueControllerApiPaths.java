package com.catalogue.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CatalogueControllerApiPaths {

    public static final String BASE_PATH = "/api/v1";
    public static final String CREATE = "/";
    public static final String GET_ITEMS = "/";
    public static final String GET_ITEMS_STREAM = "/stream";
    public static final String GET_ITEM_BY_SKU = "/{sku}";
    public static final String GET_ITEM_BY_ID = "/{id}";
    public static final String UPDATE = "/{sku}";
    public static final String DELETE = "/{sku}";
    public static final String UPLOAD_IMAGE = "/{sku}/image";
    public static final String GET_ITEMS_WS_EVENTS = BASE_PATH + "/ws/events";
}