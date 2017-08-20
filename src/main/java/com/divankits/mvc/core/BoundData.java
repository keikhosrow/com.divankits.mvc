package com.divankits.mvc.core;


import android.support.annotation.IdRes;

import com.divankits.mvc.forms.Bind.Events;

public class BoundData {

    @IdRes
    public int Target = Integer.MIN_VALUE;
    public String FieldName;
    public String Get;
    public String Set;
    public Events Event;
    public boolean AutoUpdate;
    public ValueConverter Converter;

}
