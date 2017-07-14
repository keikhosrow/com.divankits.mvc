package com.divankits.mvc.validation.validators;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataType {
    DataTypes value();
    @StringRes int error() default Integer.MIN_VALUE;

    enum DataTypes {

        None , EmailAddress , PhoneNumber , DateTime , IPAddress , WebURL

    }

}
