package com.catalogue.controller;

public enum ApiEndpoints {

    BASE_PATH("/api/v1"),
    CREATE("/"),
    GET_ITEMS("/streams"),
    GET_ITEM_BY_ID("/{id}"),
    GET_ITEM_BY_SKU("/{sku}"),
    UPDATE("/{sku}"),
    DELETE("/{sku}"),
    UPLOAD_IMAGE("/{sku}/image"),
    GET_ITEMS_WS_EVENTS(BASE_PATH + "/ws/events");

    private final String url;

    ApiEndpoints(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
