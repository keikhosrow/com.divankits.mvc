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
    String getter() default "getValue";
    String setter() default "setValue";


    enum Events {

         None , Click , Change , Focus , Blur

    }


}
