package com.divankits.mvc.validation.validators;

import android.content.res.Resources;

import com.divankits.mvc.IModel;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class RequiredValidator extends Validator {

    public RequiredValidator(Resources resources) {

        super(resources);

    }

    @Override
    public int getErrorCode() {

        return 0;

    }

    @Override
    public boolean isValid(IModel model, Field field, Object value, Annotation modifier) {

        if(value.getClass() == String.class)
            return value != null && !((String) value).isEmpty();
        else
            return value != null;

    }

    @Override
    public String getErrorDefaultMessage(Field field, Annotation modifier) {

        return ("Field \"").concat(field.getName()).concat("\" is required");

    }


}
