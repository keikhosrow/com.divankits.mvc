package com.divankits.mvc;


import android.support.annotation.Nullable;
import android.view.View;

import com.divankits.mvc.annotations.Bind;
import com.divankits.mvc.converters.ValueConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Binder {

    static final Map<Class, Class> primitives;
    static {

        primitives =  new HashMap<>();
        primitives.put(Boolean.class , Boolean.TYPE);
        primitives.put(Byte.class , Byte.TYPE);
        primitives.put(Character.class , Character.TYPE);
        primitives.put(Float.class , Float.TYPE);
        primitives.put(Integer.class , Integer.TYPE);
        primitives.put(Long.class , Long.TYPE);
        primitives.put(Short.class , Short.TYPE);
        primitives.put(Double.class , Double.TYPE);

    }

    public static void bindEvents(final IModelRenderer renderer)
            throws NoSuchFieldException, IllegalAccessException {


        if (renderer.getOnModelChangedEventListener() == null)
            return;

        for (Field field : renderer.getModel().getFields()) {

            try {

                final ArrayList<BoundData> boundList = renderer.getBoundData(field);

                if (boundList.isEmpty())
                    continue;

                for (final BoundData details: boundList) {

                    switch (details.Event) {

                        case None:
                            continue;

                        case Click:

                            ((View) details.Target).setClickable(true);

                            ((View) details.Target).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    try {

                                        Object value = renderer.getModel().getFieldValue(details.FieldName);

                                        renderer.getOnModelChangedEventListener().onFieldChanged(details, value);

                                    } catch (Exception e) {

                                        e.printStackTrace();

                                    }

                                }
                            });

                            break;

                        case Focus:
                        case Blur:

                            ((View) details.Target).setFocusable(true);

                            ((View) details.Target).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {

                                    renderer.getOnModelChangedEventListener().onFieldChanged(details, b);

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

    public static void updateValues(IModelRenderer renderer, boolean fromModel) {

        IModel model = renderer.getModel();

        IOnModelChangedEventListener listener = renderer.getOnModelChangedEventListener();

        for (Field field : model.getFields()) {

            try {

                ArrayList<BoundData> boundList = renderer.getBoundData(field);

                if (boundList.isEmpty())
                    continue;

                for (BoundData data:boundList) {

                    if(!data.AutoUpdate)
                        continue;

                    Class compType , compBaseType , fieldType , fieldPrimitiveType;

                    compType = data.Target.getClass();
                    compBaseType = null;
                    fieldType = field.getType();
                    fieldPrimitiveType = primitives.get(fieldType);

                    Method method = null;
                    boolean primitivesChecked = false;

                    String name = field.getName();
                    Object value = model.getFieldValue(name);

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

                            if(data.Converter != null)
                                value = data.Converter.convertBack(value);

                            method.invoke(data.Target, value);

                        }

                    } else {

                        Object newValue = method.invoke(data.Target);

                        if(data.Converter != null)
                            newValue = data.Converter.convert(newValue);

                        model.setFieldValue(name, newValue);

                        if (listener != null && data.Event == Bind.Events.Change)
                            listener.onFieldChanged(data, value);

                    }

                }

            } catch (Exception ex) {

                ex.printStackTrace();

                continue;

            }

        }

    }

}
