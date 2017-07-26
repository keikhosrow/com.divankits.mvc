package com.divankits.mvc.validation.validators;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
    int priority() default 0;
    @StringRes int error() default Integer.MIN_VALUE;
}
