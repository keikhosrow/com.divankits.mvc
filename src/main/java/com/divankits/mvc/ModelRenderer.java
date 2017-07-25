package com.divankits.mvc;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.divankits.mvc.annotations.Bind;
import com.divankits.mvc.annotations.View;
import com.divankits.mvc.converters.ValueConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ModelRenderer extends Fragment implements IModelRenderer {

    private IOnModelChangedEventListener changeListener;
    private IModel mModel;
    private android.view.View mView;

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
                            changeListener.onSubmit(getModel());

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

        for (ModelModifier modifier : modifiers) {

            modifier.modify(getModel());

        }

    }

    @Override
    public IModel getModel() {

        return this.mModel;

    }

    @Override
    public ModelRenderer setModel(IModel model) {

        this.mModel = model;

        return this;

    }

    @Override
    public int getSubmitId() throws NullPointerException {

        return getViewAnnotationParams("Submit is not defined in model")
                .submit();

    }

    @Override
    public int getViewId() throws NullPointerException {

        return getViewAnnotationParams("Layout is not defined in model")
                .value();

    }

    private View getViewAnnotationParams(String exMessage)
            throws NullPointerException {

        if (!getModel().getClass().isAnnotationPresent(View.class))
            throw new NullPointerException(exMessage);

        return getModel().getClass().getAnnotation(View.class);

    }

    @Override
    public android.view.View getView() {

        return mView;

    }

    @Override
    public BoundData getBoundData(Field field) {

        if (!field.isAnnotationPresent(Bind.class)) return null;

        BoundData details = new BoundData();

        try {

            Bind bind = field.getAnnotation(Bind.class);
            Object elem = getView().findViewById(bind.value());

            details.Event = bind.event();
            details.Target = elem.getClass().cast(elem);
            details.FieldName = field.getName();
            details.AutoUpdate = bind.autoUpdate();
            details.Get = bind.get();
            details.Set = bind.set();
            details.Converter = bind.converter().getSuperclass() == ValueConverter.class ?
                    (ValueConverter) bind.converter().newInstance() : null;

        } catch (Exception e) {

            e.printStackTrace();

        }

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
