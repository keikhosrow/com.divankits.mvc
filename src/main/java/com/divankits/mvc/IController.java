package com.divankits.mvc;


import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.AnimatorRes;
import android.view.View;

import com.divankits.mvc.validation.ValidationResult;

public interface IController {

    void setModel(IModel model);
    int getPlaceholderId();
    Activity getActivity();
    IModelRenderer setRenderer(IModelRenderer renderer);
    IModelRenderer getRenderer();
    FragmentManager getFragmentManager();
    void onSubmit(IModel model);
    void setOnModelChangedEventListener(IOnModelChangedEventListener listener);
    void clearStack();
    int getStackEntryCount();
    void setAnimations(@AnimatorRes int a1, @AnimatorRes int a2);
    void setAnimations(@AnimatorRes int a1, @AnimatorRes int a2, @AnimatorRes int a3, @AnimatorRes int a4);
    void clearAnimations();
    ValidationResult getModelState();

}
