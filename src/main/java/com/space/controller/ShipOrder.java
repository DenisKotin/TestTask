package com.space.controller;

public enum ShipOrder {
    ID("id"), // default
    SPEED("speed"),
    DATE("prodDate"),
    RATING("rating");

    private final String fieldName;

    ShipOrder(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}