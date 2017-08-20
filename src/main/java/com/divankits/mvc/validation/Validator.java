package com.divankits.mvc.validation;


import android.content.res.Resources;

import com.divankits.mvc.IModel;
import com.divankits.mvc.core.BoundData;
import com.divankits.mvc.core.ModelRenderer;
import com.divankits.mvc.generic.PropertyInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Validator {

    private Resources resources;

    public Validator(Resources resources) {

        this.resources = resources;

    }

    public static <A extends Annotation> int getPriority(Class<A> validator, PropertyInfo property) {

        int value = 0;

        if (!property.isAnnotationPresent(validator))
            return value;

        A modifier = property.getAnnotation(validator);

        try {

            value = (int) modifier.getClass().getMethod("priority").invoke(modifier);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return value;

    }

    public <A extends Annotation> String getErrorMessage(Class<A> validator, PropertyInfo property) {

        String message = "";

        A annotation = property.getAnnotation(validator);

        try {

            Method error = annotation.getClass().getDeclaredMethod("error");

            int value = (int) error.invoke(annotation);

            if (value != Integer.MIN_VALUE)
                message = resources.getString(value);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return message;

    }

    public <A extends Annotation> ValidationResult validate(Class<A> validator, ModelRenderer renderer) {

        ValidationResult result = new ValidationResult();

        try {

            IModel model = renderer.getModel();

            for (PropertyInfo prop : model.getProperties()) {

                if (!prop.isAnnotationPresent(validator))
                    continue;

                A modifier = prop.getAnnotation(validator);

                ArrayList<BoundData> details = prop.getBoundData();

                if (details.isEmpty() || isValid(prop, modifier))
                    continue;

                String error = getErrorMessage(validator, prop);

                if (error.isEmpty())
                    error = error.concat(getErrorDefaultMessage(prop, modifier));

                result.getErrors().add(new ValidationError(prop, getErrorCode(),
                        getPriority(validator, prop), error));


            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public abstract int getErrorCode();

    public abstract boolean isValid(PropertyInfo field, Annotation modifier);

    public abstract String getErrorDefaultMessage(PropertyInfo field, Annotation modifier);

}
