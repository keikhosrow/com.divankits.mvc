package com.divankits.mvc;


import android.view.View;

public interface IOnModelChangedEventListener {

    void onFieldChanged(BoundData details , Object oldValue);
    void onSubmit(IModel model);
    void onCreate(View view);

}

