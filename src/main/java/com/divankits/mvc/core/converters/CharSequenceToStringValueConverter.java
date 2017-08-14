package com.divankits.mvc.core.converters;

import com.divankits.mvc.core.ValueConverter;

public class CharSequenceToStringValueConverter extends ValueConverter<CharSequence , String> {

    @Override
    public String convert(CharSequence value) {

        return value.toString();

    }

    @Override
    public CharSequence convertBack(String value) {

        return value;

    }
}
