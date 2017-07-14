package com.divankits.mvc.validation.validators;

import android.content.res.Resources;


import com.divankits.mvc.IModel;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MaxLengthValidator extends Validator {

    public MaxLengthValidator(Resources resources) {
        super(resources);
    }

    @Override
    public int getErrorCode() {

        return 2;

    }

    @Override
    public boolean isValid(IModel model, Field field, Object value, Annotation modifier) {

        return ((String) value).length() <= ((MaxLength) modifier).value();

    }

    @Override
    public String getErrorDefaultMessage(Field field , Annotation modifier) {

        return ("Field \"")
                .concat(field.getName())
                .concat("\" must be less than ")
                .concat(String.valueOf(((MaxLength) modifier).value()))
                .concat(" characters");

    }

}
