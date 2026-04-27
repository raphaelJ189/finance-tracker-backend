package com.financetracker.exception;

public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;

    public DuplicateResourceException(String resourceName,
                                      String fieldName,
                                      String fieldValue) {
        super(resourceName + " already exists with "
                + fieldName + ": " + fieldValue);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName() { return fieldName; }
    public String getFieldValue() { return fieldValue; }
}