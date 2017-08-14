package com.divankits.mvc;

import java.lang.reflect.Field;

public interface IModel {

    String getName();

    Field getFieldByName(String name) throws NoSuchFieldException;

    Object getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException;

    void setFieldValue(String field, Object value) throws NoSuchFieldException, IllegalAccessException;

    boolean isCollection(Field field);

}
