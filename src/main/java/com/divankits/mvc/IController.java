package com.divankits.mvc;


import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;

import com.divankits.mvc.validation.ValidationResult;

public interface IController {

    void setModel(IModel model);

    int getPlaceholderId();

    Activity getActivity();

    IModelRenderer setRenderer(IModelRenderer renderer);

    IModelRenderer getRenderer();

    FragmentManager getFragmentManager();

    void onFieldChanged(BindDetails details, Object oldValue);

    void onSubmit(IModel model);

    void onCreate(View view);

    ValidationResult getModelState();

}
