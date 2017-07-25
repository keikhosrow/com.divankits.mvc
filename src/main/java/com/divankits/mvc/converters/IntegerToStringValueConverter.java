package com.divankits.mvc.converters;


public class IntegerToStringValueConverter extends ValueConverter<Integer , String>  {

    @Override
    public String convert(Integer value) {

        return value.toString();

    }

    @Override
    public Integer convertBack(String value) {

        return Integer.valueOf(value);

    }

}
