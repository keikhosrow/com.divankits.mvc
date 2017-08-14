package com.divankits.mvc.core;

public abstract class ValueConverter<T1 ,T2> {

    public abstract T2 convert(T1 value);
    public abstract T1 convertBack(T2 value);

}
