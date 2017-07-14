package com.divankits.mvc.validation.validators;

import android.content.res.Resources;

import com.divankits.mvc.IModel;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class MinLengthValidator extends Validator {

    public MinLengthValidator(Resources resources) {
        super(resources);
    }

    @Override
    public int getErrorCode() {
        return 1;
    }

    @Override
    public boolean isValid(IModel model, Field field, Object value, Annotation modifier) {

        return ((String) value ).length() >= ((MinLength) modifier).value();

    }

    @Override
    public String getErrorDefaultMessage(Field field, Annotation modifier) {

        return  ("Field \"")
                .concat(field.getName())
                .concat("\" must have at least ")
                .concat(String.valueOf(((MinLength) modifier).value()))
                .concat(" characters");
    }


}
