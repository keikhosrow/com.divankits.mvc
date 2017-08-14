package com.divankits.mvc.core;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.divankits.mvc.Bind;
import com.divankits.mvc.IModel;
import com.divankits.mvc.Multibind;
import com.divankits.mvc.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ModelRenderer extends Fragment implements IModelRenderer {


    private static final Map<Class, Class> primitives;
    private static String MODIFIERS_SUFFIX = "Modifier";
    private static String ERR_001 = "Submit is not defined in model";
    private static String ERR_002 = "Layout is not defined in model";

    static {

        primitives = new HashMap<>();
        primitives.put(Boolean.class, Boolean.TYPE);
        primitives.put(Byte.class, Byte.TYPE);
        primitives.put(Character.class, Character.TYPE);
        primitives.put(Float.class, Float.TYPE);
        primitives.put(Integer.class, Integer.TYPE);
        primitives.put(Long.class, Long.TYPE);
        primitives.put(Short.class, Short.TYPE);
        primitives.put(Double.class, Double.TYPE);

    }

    private IOnModelChangedEventListener changeListener;
    private IModel mModel;
    private android.view.View mView;

    public ModelRenderer() {
        super();
    }

    public void bindEvents()
            throws NoSuchFieldException, IllegalAccessException {

        if (getOnModelChangedEventListener() == null)
            return;

        for (Field field : getModel().getClass().getFields()) {

            try {

                final ArrayList<BoundData> boundList = getBoundData(field);

                if (boundList.isEmpty())
                    continue;

                for (final BoundData details : boundList) {

                    switch (details.Event) {

                        case None:
                            continue;

                        case Click:

                            ((android.view.View) details.Target).setClickable(true);

                            ((android.view.View) details.Target).setOnClickListener(new android.view.View.OnClickListener() {
                                @Override
                                public void onClick(android.view.View view) {

                                    try {

                                        Object value = getModel().getFieldValue(details.FieldName);

                                        getOnModelChangedEventListener().onFieldChanged(details, value);

                                    } catch (Exception e) {

                                        e.printStackTrace();

                                    }

                                }
                            });

                            break;

                        case Focus:
                        case Blur:

                            ((android.view.View) details.Target).setFocusable(true);

                            ((android.view.View) details.Target).setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(android.view.View view, boolean b) {

                                    getOnModelChangedEventListener().onFieldChanged(details, b);

                                }
                            });

                            break;

                    }

                }


            } catch (NullPointerException e) {

                e.printStackTrace();

                continue;

            }
        }

    }

    @Override
    public IModelRenderer update(boolean fromModel) {

        IOnModelChangedEventListener listener = getOnModelChangedEventListener();

        for (Field field : getModel().getClass().getFields()) {

            try {

                ArrayList<BoundData> boundList = getBoundData(field);

                if (boundList.isEmpty())
                    continue;

                for (BoundData data : boundList) {

                    if (!data.AutoUpdate || data.Target == null)
                        continue;

                    Class compType, compBaseType, fieldType, fieldPrimitiveType;

                    compType = data.Target.getClass();

                    compBaseType = null;

                    fieldType = field.getType();

                    fieldPrimitiveType = primitives.get(fieldType);

                    Method method = null;

                    boolean primitivesChecked = false;

                    String name = field.getName();

                    Object value = getModel().getFieldValue(name);

                    if (fromModel && data.Converter instanceof ValueConverter) {

                        fieldType = data.Converter.getClass()
                                .getDeclaredMethod("convertBack", fieldType)
                                .getReturnType();

                        fieldPrimitiveType = primitives.get(fieldType);

                    }

                    while (method == null && compType != null) {

                        try {

                            if (fromModel) {

                                method = compType.getDeclaredMethod(data.Set, fieldType);

                            } else {

                                method = compType.getDeclaredMethod(data.Get);

                            }

                        } catch (NoSuchMethodException e) {

                            if (compBaseType == null)
                                compBaseType = compType;

                            compType = compType.getSuperclass();

                            if (compType == null && !primitivesChecked) {

                                primitivesChecked = true;

                                compType = compBaseType;

                                if (fieldPrimitiveType != null)
                                    fieldType = fieldPrimitiveType;

                            }

                        }

                    }

                    if (method == null)
                        continue;

                    if (fromModel) {

                        if (value != null) {

                            if (data.Converter != null)
                                value = data.Converter.convertBack(value);

                            method.invoke(data.Target, value);

                        }

                    } else {

                        Object newValue = method.invoke(data.Target);

                        if (data.Converter != null)
                            newValue = data.Converter.convert(newValue);

                        getModel().setFieldValue(name, newValue);

                        if (listener != null && data.Event == Bind.Events.Change)
                            listener.onFieldChanged(data, value);

                    }

                }

            } catch (Exception ex) {

                ex.printStackTrace();

                continue;

            }

        }

        return this;

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

            modify(getModel(), true);

            bindEvents();

            update(true);

            int sid = getSubmitId();

            android.view.View submit = getView().findViewById(sid);

            if (submit != null)
                submit.setOnClickListener(new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(android.view.View view) {

                        if (changeListener != null)
                            changeListener.onSubmit(getModel());

                    }

                });


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

        if (this.mModel != null)
            modify(this.mModel, false);

        return this;

    }

    @Override
    public int getSubmitId() throws NullPointerException {

        return getViewAnnotationParams(ERR_001).submit();

    }

    @Override
    public int getViewId() throws NullPointerException {

        return getViewAnnotationParams(ERR_002).value();

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

            b.Event = bind.event();
            b.Target = getView().findViewById(bind.value());
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

    public IModelRenderer modify(IModel model, boolean restore) {

        List<ModifierHandler> modifiers = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (Field field : model.getClass().getFields()) {

            // check if field is collection or map

            if (model.isCollection(field)) {

                try {

                    Object value = model.getFieldValue(field.getName());

                    if (value == null)
                        continue;

                    Iterator collection = Iterable.class.cast(value).iterator();

                    while (collection.hasNext()) {

                        Object item = collection.next();

                        // apply item modifiers

                        if (item instanceof IModel) {

                            modify((IModel) item, restore);

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

            for (Annotation a : annotations) {

                String name = a.annotationType().getName().concat(MODIFIERS_SUFFIX);

                try {

                    Class<?> clazz = Class.forName(name);

                    ModelModifier object = (ModelModifier) clazz.getConstructor(a.annotationType())
                            .newInstance(a);

                    modifiers.add(new ModifierHandler(model , field , object));

                } catch (Exception e) {

                    continue;

                }

            }

        }

        for (ModifierHandler m : modifiers)
            m.modifier.invoke(m.model, m.field, restore);

        return this;

    }


    class ModifierHandler {

        Field field;
        IModel model;
        ModelModifier modifier;

        public ModifierHandler(IModel model , Field field , ModelModifier modifier){

            this.field = field;
            this.model = model;
            this.modifier = modifier;

        }

    }

}
