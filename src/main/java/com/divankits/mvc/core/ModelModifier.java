package com.divankits.mvc.core;

import com.divankits.mvc.generic.PropertyInfo;

import java.lang.annotation.Annotation;

public abstract class ModelModifier<T extends Annotation> {

    private T modifier;

    public ModelModifier(T modifier) {
        this.modifier = modifier;
    }

    public T getModifier() {

        return modifier;

    }

    public void invoke(PropertyInfo property, boolean restore) {

        try {

            if (this.getClass().isAnnotationPresent(ModifyOnce.class)) {

                Object value = property.getValue();

                if (value != null)
                    return;

                property.setValue(modify(property));

                return;

            }

            property.setValue(restore ? restore(property) : modify(property));

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public abstract Object modify(PropertyInfo property);

    public abstract Object restore(PropertyInfo property);

}
