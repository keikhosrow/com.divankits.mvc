package com.divankits.mvc.core;


import com.divankits.mvc.Bind.Events;

public class BoundData {

    public Object Target;
    public String FieldName;
    public String Get;
    public String Set;
    public Events Event;
    public boolean AutoUpdate;
    public ValueConverter Converter;

}
