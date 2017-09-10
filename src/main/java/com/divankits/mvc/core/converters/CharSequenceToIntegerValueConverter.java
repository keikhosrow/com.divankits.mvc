package com.divankits.mvc.core.converters;


import com.divankits.mvc.core.ValueConverter;

public class CharSequenceToIntegerValueConverter extends ValueConverter<CharSequence , Integer> {

    @Override
    public Integer convert(CharSequence value) {

        return Integer.parseInt(value.toString());

    }

    @Override
    public CharSequence convertBack(Integer value) {

        return String.valueOf(value);

    }

}
