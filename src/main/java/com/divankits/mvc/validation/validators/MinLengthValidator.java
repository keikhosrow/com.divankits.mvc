package com.divankits.mvc.validation.validators;

import android.content.res.Resources;

import com.divankits.mvc.generic.PropertyInfo;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;


public class MinLengthValidator extends Validator {

    public MinLengthValidator(Resources resources) {
        super(resources);
    }

    @Override
    public int getErrorCode() {
        return 1;
    }

    @Override
    public boolean isValid(PropertyInfo property ,  Annotation modifier) {

        String value;

        try {

            value = (String) property.getValue();

            return value.length() >= ((MinLength) modifier).value();

        } catch (IllegalAccessException e) {

            e.printStackTrace();

            return false;

        }

    }

    @Override
    public String getErrorDefaultMessage(PropertyInfo property, Annotation modifier) {

        return  ("Field \"")
                .concat(property.getName())
                .concat("\" must have at least ")
                .concat(String.valueOf(((MinLength) modifier).value()))
                .concat(" characters");

    }


}
