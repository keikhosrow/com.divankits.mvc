package com.divankits.mvc.validation;


import com.divankits.mvc.IModel;

import java.lang.reflect.Field;

public class ValidationError {

    private int code;
    private int priority;
    private String message;
    private Field field;
    private IModel model;

    public ValidationError(IModel model, Field field, int code, int priority, String message) {

        this.model = model;
        this.code = code;
        this.message = message;
        this.field = field;
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

    public IModel getModel() {

        return model;

    }

    public Field getField() {

        return field;

    }

}
