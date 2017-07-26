package com.divankits.mvc;


import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;


public interface IModelRenderer {

    IModel getModel();
    int getViewId();
    int getSubmitId();
    View getView();
    IOnModelChangedEventListener getOnModelChangedEventListener();
    ArrayList<BoundData> getBoundData(Field field);
    IModelRenderer setModel(IModel model);
    IModelRenderer setOnModelChangedEventListener(IOnModelChangedEventListener listener);

}
