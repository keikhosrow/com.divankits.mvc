package com.divankits.mvc.validation;


import android.content.res.Resources;

import com.divankits.mvc.core.BoundData;
import com.divankits.mvc.IModel;
import com.divankits.mvc.core.IModelRenderer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Validator {

    private Resources resources;
    private enum ElementStyle {

        Normal , Invalid

    }

    public Validator(Resources resources){

        this.resources = resources;

    }

    public <A extends Annotation> String getErrorMessage(Class<A> validator , Field field) {

        String message = "";

        A annotation = field.getAnnotation(validator);

        try {

            Method error = annotation.getClass().getDeclaredMethod("error");

            int value = (int) error.invoke(annotation);

            if(value != Integer.MIN_VALUE)
                message = resources.getString(value);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return message;

    }

    public <A extends Annotation> ValidationResult validate(Class<A> validator , IModelRenderer renderer){

        ValidationResult result = new ValidationResult();

        try {

            IModel model = renderer.getModel();

            for (Field field : model.getClass().getFields()) {

                if (!field.isAnnotationPresent(validator))
                    continue;

                A modifier = field.getAnnotation(validator);

                ArrayList<BoundData> details = renderer.getBoundData(field);

                if (details.isEmpty())
                    continue;

                Object value = model.getFieldValue(field.getName());

                if (!isValid(model , field , value , modifier)) {

                    String error = getErrorMessage(validator , field);

                    if (error.isEmpty())
                        error = error.concat(getErrorDefaultMessage(field , modifier).toString());

                    result.getErrors().add(new ValidationError(model , field , getErrorCode(),
                            getPriority(validator , field) , error));

                    setElementsStyle(details , ElementStyle.Invalid);

                } else {

                    setElementsStyle(details , ElementStyle.Normal);

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    private void setElementsStyle(ArrayList<BoundData> d , ElementStyle s){

        for (BoundData b:d) {

            if(b.Target == null)
                continue;

            switch (s){

                case Normal:
                    if (b.Target instanceof IValidationSupportElement)
                        ((IValidationSupportElement) b.Target).showNormalStyle();
                    break;
                case Invalid:
                    if (b.Target instanceof IValidationSupportElement)
                        ((IValidationSupportElement) b.Target).showInvalidStyle();
                    break;

            }

        }

    }

    public static <A extends Annotation> int getPriority(Class<A> validator , Field field){

        int value = 0;

        if(!field.isAnnotationPresent(validator))
            return value;

        A modifier = field.getAnnotation(validator);

        try {

            value = (int) modifier.getClass().getMethod("priority").invoke(modifier);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return value;

    }

    public abstract int getErrorCode();
    public abstract boolean isValid(IModel model , Field field, Object value , Annotation modifier);
    public abstract String getErrorDefaultMessage(Field field ,  Annotation modifier);

}
