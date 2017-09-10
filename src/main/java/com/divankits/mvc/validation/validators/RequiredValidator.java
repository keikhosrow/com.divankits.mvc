package com.divankits.mvc.validation.validators;

import android.content.res.Resources;

import com.divankits.mvc.generic.PropertyInfo;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;


public class RequiredValidator extends Validator {

    public RequiredValidator(Resources resources) {

        super(resources);

    }

    @Override
    public int getErrorCode() {

        return 0;

    }

    @Override
    public boolean isValid(PropertyInfo property, Annotation modifier) {

        Object value;

        try {

            value = property.getValue();

            if(value.getClass() == String.class)
                return value != null && !((String)value).isEmpty();
            else
                return value != null;

        } catch (IllegalAccessException e) {

            e.printStackTrace();

            return false;

        }

    }

    @Override
    public String getErrorDefaultMessage(PropertyInfo property, Annotation modifier) {

        return ("Field \"").concat(property.getName()).concat("\" is required");

    }


}
