package com.divankits.mvc.core;


import android.view.View;

import com.divankits.mvc.IModel;

import java.lang.reflect.Field;
import java.util.ArrayList;


public interface IModelRenderer {

    IModel getModel();

    IModelRenderer setModel(IModel model);

    int getViewId();

    int getSubmitId();

    View getView();

    IOnModelChangedEventListener getOnModelChangedEventListener();

    IModelRenderer setOnModelChangedEventListener(IOnModelChangedEventListener listener);

    ArrayList<BoundData> getBoundData(Field field);

    IModelRenderer update(boolean fromModel);

    IModelRenderer modify(IModel model , boolean restore);

}
