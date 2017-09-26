package com.divankits.mvc.core;

import android.view.View;

import com.divankits.mvc.Model;

public interface IOnModelChangedEventListener {

    void onFieldChanged(BoundData details , Object oldValue);
    void onSubmit(Model model);
    void onCreate(View view);
    void onCollectionItemSelected(Model model , Object item);

}

