package com.divankits.mvc;


import android.view.View;

import java.lang.reflect.Field;


public interface IModelRenderer {

    IModel getModel();
    int getViewId();
    int getSubmitId();
    View getView();
    IOnModelChangedEventListener getOnModelChangedEventListener();
    BindDetails getBindDetails(Field field);
    IModelRenderer setModel(IModel model);
    IModelRenderer setOnModelChangedEventListener(IOnModelChangedEventListener listener);

}
