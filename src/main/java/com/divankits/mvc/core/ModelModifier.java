package com.divankits.mvc.core;

import com.divankits.mvc.IModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class ModelModifier<T extends Annotation> {

    private T modifier;

    public ModelModifier(T modifier) {
        this.modifier = modifier;
    }

    public T getModifier() {

        return modifier;

    }

    public void invoke(IModel model, Field field, boolean restore) {

        try {

            if (this.getClass().isAnnotationPresent(ModifyOnce.class)) {

                Object value = model.getFieldValue(field.getName());

                if (value != null)
                    return;

                model.setFieldValue(field.getName(), modify(model, field));

                return;

            }

            model.setFieldValue(field.getName(),
                    restore ? restore(model, field) : modify(model, field));


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public abstract Object modify(IModel model, Field field);

    public abstract Object restore(IModel model, Field field);

}
