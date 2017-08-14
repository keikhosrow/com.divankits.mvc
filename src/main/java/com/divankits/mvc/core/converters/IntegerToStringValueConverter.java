package com.divankits.mvc.core.converters;

import com.divankits.mvc.core.ValueConverter;

public class IntegerToStringValueConverter extends ValueConverter<Integer , String> {

    @Override
    public String convert(Integer value) {

        return value.toString();

    }

    @Override
    public Integer convertBack(String value) {

        return Integer.valueOf(value);

    }

}
