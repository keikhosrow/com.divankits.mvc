package com.divankits.mvc.core;


import android.view.View;

import com.divankits.mvc.IModel;
import com.divankits.mvc.core.BoundData;

public interface IOnModelChangedEventListener {

    void onFieldChanged(BoundData details , Object oldValue);
    void onSubmit(IModel model);
    void onCreate(View view);

}

