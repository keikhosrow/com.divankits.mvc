package com.divankits.mvc;


import com.divankits.mvc.annotations.Internal;
import com.divankits.mvc.annotations.modifiers.UniqueModifier;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public abstract class Model implements IModel {


    private String modelId;

    @Override
    public String getModelId(){

        if(modelId.equals(null) || modelId.isEmpty()){
            modelId = UniqueModifier.getRandomGuid();
        }

        return modelId;

    }

    @Override
    public Field getFieldByName(String name) throws NoSuchFieldException {

        return this.getClass().getDeclaredField(name);

    }

    @Override
    public Field[] getFields() {

        return this.getClass().getDeclaredFields();

    }

    @Override
    public Object getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException {

        Field f = getFieldByName(field);

        Object data;

        if (f.isAccessible()) {

            data = f.get(this);

        } else {

            f.setAccessible(true);

            data = f.get(this);

            f.setAccessible(false);

        }

        return data;

    }

    @Override
    public void setFieldValue(String field, Object value) throws NoSuchFieldException, IllegalAccessException {

        Field f = getFieldByName(field);

        if (f.isAccessible()) {

            f.set(this, value);

        } else {

            f.setAccessible(true);

            f.set(this, value);

            f.setAccessible(false);

        }

    }

    @Override
    public String toString(){

        try {

            return toJSONObject().toString();

        } catch (Exception e) {

            return super.toString();

        }

    }

    @Override
    public JSONObject toJSONObject() throws NoSuchFieldException, IllegalAccessException, JSONException {

        JSONObject obj = new JSONObject();

        for (Field field : getFields()) {

            if (field.isAnnotationPresent(Internal.class))
                continue;

            obj.put(field.getName(), getFieldValue(field.getName()));

        }

        return obj;

    }


    @Override
    public String getName() {

        return getClass().getSimpleName();

    }


}
