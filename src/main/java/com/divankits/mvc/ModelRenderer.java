package com.divankits.mvc;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.divankits.mvc.annotations.Bind;
import com.divankits.mvc.annotations.Multibind;
import com.divankits.mvc.annotations.View;
import com.divankits.mvc.converters.ValueConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ModelRenderer extends Fragment implements IModelRenderer {

    private IOnModelChangedEventListener changeListener;
    private IModel mModel;
    private android.view.View mView;

    public ModelRenderer() {

        super();

    }

    private static void modifyModel(IModel model) {

        boolean isCollection = false;

        Field[] fields = model.getFields();
        List<ModelModifier> modifiers = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (Field field : fields) {

            Class type = field.getType();

            // check if field is collection or map

            isCollection = Collection.class.isAssignableFrom(type) ||
                    Map.class.isAssignableFrom(type);

            if (isCollection) {

                try {

                    Iterator collection = Iterable.class.cast(model.getFieldValue(field.getName()))
                            .iterator();

                    while (collection.hasNext()) {

                        Object item = collection.next();

                        // apply item modifiers

                        if (item instanceof IModel) {

                            modifyModel((IModel) item);

                        }

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    continue;

                }

            }

            // for non-collections

            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {

                String name = annotation.annotationType().getName().concat("Modifier");

                Class<?> clazz;

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

            modifier.modify(model);

        }

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

            modifyModel(getModel());

            Binder.bindEvents(this);

            Binder.updateValues(this, true);

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
    public ArrayList<BoundData> getBoundData(Field field) {

        ArrayList<BoundData> details = new ArrayList<>();

        Bind bind = field.getAnnotation(Bind.class);

        Multibind multibind = field.getAnnotation(Multibind.class);

        if (bind != null)
            details.add(bindToBoundData(bind, field));

        if (multibind != null)
            for (Bind b : multibind.value())
                details.add(bindToBoundData(b, field));


        return details;

    }

    private BoundData bindToBoundData(Bind bind, Field field) {

        BoundData b = new BoundData();

        try {

            Object elem = getView().findViewById(bind.value());

            b.Event = bind.event();
            b.Target = elem.getClass().cast(elem);
            b.FieldName = field.getName();
            b.AutoUpdate = bind.autoUpdate();
            b.Get = bind.get();
            b.Set = bind.set();

            b.Converter = bind.converter().getSuperclass() == ValueConverter.class ?
                    (ValueConverter) bind.converter().newInstance() : null;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return b;

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
