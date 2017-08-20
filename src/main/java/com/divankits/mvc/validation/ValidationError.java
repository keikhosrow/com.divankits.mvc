package com.divankits.mvc.validation;

import com.divankits.mvc.generic.PropertyInfo;

public class ValidationError {

    private int code;
    private int priority;
    private String message;
    private PropertyInfo property;

    public ValidationError(PropertyInfo property , int code, int priority, String message) {

        this.code = code;
        this.message = message;
        this.property = property;
        this.priority = priority;

    }

    public String getMessage() {

        return message;

    }

    public int getCode() {

        return code;

    }

    public int getPriority() {

        return priority;

    }

    public PropertyInfo getField() {

        return property;

    }

}
