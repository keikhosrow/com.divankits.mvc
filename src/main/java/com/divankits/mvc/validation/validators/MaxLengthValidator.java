package com.divankits.mvc.validation.validators;

import android.content.res.Resources;

import com.divankits.mvc.generic.PropertyInfo;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;

public class MaxLengthValidator extends Validator {

    public MaxLengthValidator(Resources resources) {
        super(resources);
    }

    @Override
    public int getErrorCode() {

        return 2;

    }

    @Override
    public boolean isValid(PropertyInfo property, Annotation modifier) {

        String value;

        try {

            value = (String) property.getValue();

            return value.length() <= ((MaxLength) modifier).value();

        } catch (IllegalAccessException e) {

            e.printStackTrace();

            return false;

        }

    }

    @Override
    public String getErrorDefaultMessage(PropertyInfo property, Annotation modifier) {

        return ("Field \"")
                .concat(property.getName())
                .concat("\" must be less than ")
                .concat(String.valueOf(((MaxLength) modifier).value()))
                .concat(" characters");

    }

}
