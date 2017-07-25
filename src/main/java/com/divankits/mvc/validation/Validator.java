package com.divankits.mvc.validation;


import android.content.res.Resources;

import com.divankits.mvc.BoundData;
import com.divankits.mvc.IModel;
import com.divankits.mvc.IModelRenderer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class Validator {


    private Resources resources;

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

            Field[] fields = model.getFields();

            for (Field field : fields) {

                if (!field.isAnnotationPresent(validator))
                    continue;

                A modifier = field.getAnnotation(validator);

                BoundData details = renderer.getBoundData(field);

                if (details == null || details.Target == null)
                    continue;

                Object value = model.getFieldValue(field.getName());

                if (!isValid(model , field , value , modifier)) {

                    String error = getErrorMessage(validator , field);

                    if (error.isEmpty())
                        error = error.concat(getErrorDefaultMessage(field , modifier).toString());

                    result.getErrors().add(new ValidationError(getErrorCode(), error, field.getName()));

                    if (details.Target instanceof IValidationSupportElement)
                        ((IValidationSupportElement) details.Target).showInvalidStyle();

                } else {

                    if (details.Target instanceof IValidationSupportElement)
                        ((IValidationSupportElement) details.Target).showNormalStyle();


                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public abstract int getErrorCode();
    public abstract boolean isValid(IModel model , Field field, Object value , Annotation modifier);
    public abstract String getErrorDefaultMessage(Field field ,  Annotation modifier);

}
