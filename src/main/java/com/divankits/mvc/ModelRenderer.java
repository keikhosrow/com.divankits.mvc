package com.divankits.mvc;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.divankits.mvc.annotations.Bind;
import com.divankits.mvc.annotations.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ModelRenderer extends Fragment implements IModelRenderer {

    private IOnModelChangedEventListener changeListener;

    private IModel model;

    private  android.view.View mView;

    public ModelRenderer() {

        super();

    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {

        try {

            mView = inflater.inflate(getViewId(), container, false);

            if (changeListener != null)
                changeListener.onCreate(mView);

            return mView;

        } catch (NullPointerException e) {

            e.printStackTrace();

        }

        return null;

    }

    @Override
    public void onStart() {

        super.onStart();

        try {

            Binder.bindEvents(this);

            Binder.updateValues(this, true);

            modifyModel();

            int submitId = getSubmitId();

            android.view.View submitBtn = getView().findViewById(submitId);

            if (submitBtn != null) {

                submitBtn.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {

                        if (changeListener != null)
                            changeListener.onSubmit(model);

                    }
                });

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void modifyModel() {

        Field[] fields = getModel().getFields();

        List<ModelModifier> modifiers = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (Field field : fields) {

            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {

                String name = annotation.annotationType().getName().concat("Modifier");

                Class<?> clazz = null;

                try {

                    if (names.contains(name))
                        continue;

                    clazz = Class.forName(name);

                    Constructor<?> ctor = clazz.getConstructor();

                    ModelModifier object = (ModelModifier) ctor.newInstance();

                    names.add(name);

                    modifiers.add(object);

                } catch (Exception e) {

                    continue;

                }

            }

        }

        for (ModelModifier modifier: modifiers) {

            modifier.modify(getModel());

        }

    }

    @Override
    public IModel getModel() {

        return this.model;

    }

    @Override
    public ModelRenderer setModel(IModel model) {

        this.model = model;

        return this;

    }

    @Override
    public int getSubmitId() throws NullPointerException {

        if (!model.getClass().isAnnotationPresent(View.class))
            throw new NullPointerException("Submit is not defined in model");

        return model.getClass().getAnnotation(View.class).submit();

    }

    @Override
    public int getViewId() throws NullPointerException {

        if (!model.getClass().isAnnotationPresent(View.class))
            throw new NullPointerException("Layout is not defined in model");

        return model.getClass().getAnnotation(View.class).value();

    }

    @Override
    public android.view.View getView(){

        return mView;

    }

    @Override
    public BindDetails getBindDetails(Field field) {

        if (!field.isAnnotationPresent(Bind.class)) return null;

        Bind bind = field.getAnnotation(Bind.class);

        Object elem = getView().findViewById(bind.value());

        BindDetails details = new BindDetails();
        details.Event = bind.event();
        details.Target = elem.getClass().cast(elem);
        details.FieldName = field.getName();
        details.AutoUpdate = bind.autoUpdate();
        details.Getter = bind.getter();
        details.Setter = bind.setter();
        return details;

    }

    @Override
    public IOnModelChangedEventListener getOnModelChangedEventListener() {

        return this.changeListener;

    }

    @Override
    public ModelRenderer setOnModelChangedEventListener(IOnModelChangedEventListener listener) {

        this.changeListener = listener;

        return this;

    }


}
