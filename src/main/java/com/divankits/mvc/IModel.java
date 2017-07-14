package com.divankits.mvc;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public interface IModel {

    String getModelId();
    Field getFieldByName(String name) throws NoSuchFieldException;
    Field[] getFields();
    Object getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException;
    void setFieldValue(String field , Object value) throws NoSuchFieldException , IllegalAccessException;
    String toString();
    JSONObject toJSONObject() throws NoSuchFieldException, IllegalAccessException, JSONException;

}
