package com.divankits.mvc.core;


import android.view.View;

import com.divankits.mvc.IModel;
import com.divankits.mvc.core.BoundData;

import java.lang.reflect.Field;

public interface IOnModelChangedEventListener {

    void onFieldChanged(BoundData details , Object oldValue);
    void onSubmit(IModel model);
    void onCreate(View view);
    void onCollectionItemSelected(IModel model , Object item);

}

