package com.divankits.mvc.core.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Identity {

    GenerateType value() default GenerateType.Indexing;

    enum GenerateType {

        RandomNumber, Guid , Indexing

    }

}
