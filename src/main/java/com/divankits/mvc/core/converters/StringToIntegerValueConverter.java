package com.divankits.mvc.core.converters;


import com.divankits.mvc.core.ValueConverter;

public class StringToIntegerValueConverter  extends ValueConverter<String , Integer>  {


    @Override
    public Integer convert(String value) {

        return Integer.valueOf(value);

    }

    @Override
    public String convertBack(Integer value) {

        return value.toString();

    }

}
