package com.divankits.mvc.converters;

/**
 * @param <T1> Component set/get value method type
 * @param <T2> Field Type
 */
public abstract class ValueConverter<T1 ,T2> {

    public abstract T2 convert(T1 value);
    public abstract T1 convertBack(T2 value);

}
