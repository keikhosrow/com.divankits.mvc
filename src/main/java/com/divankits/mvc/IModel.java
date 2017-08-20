package com.divankits.mvc;

import com.divankits.mvc.generic.PropertyInfo;

public interface IModel {

    String getName();

    PropertyInfo[] getProperties();

    PropertyInfo getProperty(String name) throws NoSuchFieldException;

    Object getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException;

    void setFieldValue(String field, Object value) throws NoSuchFieldException, IllegalAccessException;


}
