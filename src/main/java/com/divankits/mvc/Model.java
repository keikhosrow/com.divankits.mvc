package com.divankits.mvc;

import java.lang.reflect.Field;
import java.util.Map;

public class Model implements IModel {

    @Override
    public Field getFieldByName(String name) throws NoSuchFieldException {

        return this.getClass().getField(name);

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
    public boolean isCollection(Field field) {

        Class type = field.getType();

        return java.util.Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);

    }

    @Override
    public String getName() {

        return getClass().getSimpleName();

    }


}
