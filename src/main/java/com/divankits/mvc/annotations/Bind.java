package com.divankits.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Bind {

    int value();
    Events event() default Events.None;
    boolean autoUpdate() default true;
    String get() default "getValue";
    String set() default "setValue";
    Class converter() default Object.class;

    enum Events {

         None , Click , Change , Focus , Blur

    }


}

