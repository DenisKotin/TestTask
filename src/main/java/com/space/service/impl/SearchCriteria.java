package com.space.service.impl;


class SearchCriteria {
    private final String nameField;
    private final SearchOperation operation;
    private final Object value;

    public String getNameField() {
        return nameField;
    }

    public SearchOperation getOperation() {
        return operation;
    }

    public Object getValue() {
        return value;
    }

    public SearchCriteria(String nameField, SearchOperation operation, Object value) {
        this.nameField = nameField;
        this.operation = operation;
        this.value = value;
    }


}
