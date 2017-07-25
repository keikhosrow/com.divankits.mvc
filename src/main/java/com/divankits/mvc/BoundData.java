package com.divankits.mvc;


import com.divankits.mvc.annotations.Bind.Events;
import com.divankits.mvc.converters.ValueConverter;

public class BoundData {

    public Object Target;
    public String FieldName;
    public String Get;
    public String Set;
    public Events Event;
    public boolean AutoUpdate;
    public ValueConverter Converter;

}
