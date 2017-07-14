package com.divankits.mvc.annotations.modifiers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Unique {

    GeneratePolicy value() default GeneratePolicy.Number;

    enum GeneratePolicy {

        Number , Guid

    }

}
