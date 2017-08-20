package com.divankits.mvc;

import com.divankits.mvc.generic.PropertyInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Model implements IModel {

    @Override
    public PropertyInfo getProperty(String name) throws NoSuchFieldException {

        return new PropertyInfo(this , this.getClass().getField(name));

    }

    @Override
    public Object getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException {

        return getProperty(field).getValue();

    }

    @Override
    public void setFieldValue(String field, Object value) throws NoSuchFieldException, IllegalAccessException {

        getProperty(field).setValue(value);

    }

    @Override
    public String getName() {

        return getClass().getSimpleName();

    }

    @Override
    public PropertyInfo[] getProperties() {

        ArrayList<PropertyInfo> props = new ArrayList<>();

        for (Field f : getClass().getFields()) {

            props.add(new PropertyInfo(this , f));

        }

        return props.toArray(new PropertyInfo[props.size()]);

    }


}
