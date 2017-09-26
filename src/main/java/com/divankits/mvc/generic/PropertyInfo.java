package com.divankits.mvc.generic;

import com.divankits.mvc.Model;
import com.divankits.mvc.core.BoundData;
import com.divankits.mvc.core.ValueConverter;
import com.divankits.mvc.forms.Bind;
import com.divankits.mvc.forms.Multibind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class PropertyInfo {

    private Field field;
    private Object owner;

    public <T> PropertyInfo(T owner, Field field) {

        this.field = field;
        this.owner = owner;

    }

    public String getName() {

        return field.getName();

    }

    public Object getValue() throws IllegalAccessException {

        Object data;

        if (field.isAccessible()) {

            data = field.get(owner);

        } else {

            field.setAccessible(true);

            data = field.get(owner);

            field.setAccessible(false);

        }

        return data;

    }

    public PropertyInfo setValue(Object value) throws NoSuchFieldException, IllegalAccessException {

        if (field.isAccessible()) {

            field.set(owner, value);

        } else {

            field.setAccessible(true);

            field.set(owner, value);

            field.setAccessible(false);

        }

        return this;

    }

    public Object getOwner() {

        return this.owner;

    }

    public ArrayList<BoundData> getBoundData() {

        ArrayList<BoundData> details = new ArrayList<>();

        Bind bind = field.getAnnotation(Bind.class);

        Multibind multibind = field.getAnnotation(Multibind.class);

        if (bind != null)
            details.add(bindToBoundData(bind, field));

        if (multibind != null)
            for (Bind b : multibind.value())
                details.add(bindToBoundData(b, field));


        return details;

    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {

        return field.getAnnotation(annotationType);

    }

    public Annotation[] getDeclaredAnnotations() {

        return field.getDeclaredAnnotations();

    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {

        return field.isAnnotationPresent(annotationType);

    }

    public Class<?> getType() {

        return field.getType();

    }

    public Field toField() {

        return this.field;

    }

    public boolean isCollection() {

        return Iterable.class.isAssignableFrom(field.getType()) || isArray();

    }

    public boolean isArray() {

        return field.getType().isArray();

    }

    public boolean isModel() {

        return Model.class.isAssignableFrom(field.getType());

    }

    public boolean isSynthetic() {

        return field.isSynthetic();

    }

    private BoundData bindToBoundData(Bind bind, Field field) {

        BoundData b = new BoundData();

        try {

            b.Event = bind.event();
            b.Target = bind.value();
            b.FieldName = field.getName();
            b.AutoUpdate = bind.autoUpdate();
            b.Get = bind.get();
            b.Set = bind.set();

            b.Converter = bind.converter().getSuperclass() == ValueConverter.class ?
                    (ValueConverter) bind.converter().newInstance() : null;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return b;

    }

}
