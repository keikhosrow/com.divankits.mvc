package com.divankits.mvc;


import android.support.annotation.Nullable;
import android.view.View;

import com.divankits.mvc.annotations.Bind;
import com.divankits.mvc.converters.ValueConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Binder {

    public static void bindEvents(final IModelRenderer renderer)
            throws NoSuchFieldException, IllegalAccessException {


        if (renderer.getOnModelChangedEventListener() == null)
            return;

        for (Field field : renderer.getModel().getFields()) {

            try {

                final BoundData details = renderer.getBoundData(field);

                if (details == null)
                    continue;

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

                BoundData data = renderer.getBoundData(field);

                if (data == null || !data.AutoUpdate)
                    continue;

                Class clazz = data.Target.getClass();

                Method method = null;
                boolean primitivesChecked = false;
                Class firstClass = null;

                Class type = field.getType();
                String name = field.getName();
                Object value = model.getFieldValue(name);

                if (fromModel && data.Converter instanceof ValueConverter) {

                    type = data.Converter.getClass()
                            .getDeclaredMethod("convertBack", type)
                            .getReturnType();

                }

                while (method == null && clazz != null) {

                    try {

                        if (fromModel) {

                            method = clazz.getDeclaredMethod(data.Set, type);

                        } else {

                            method = clazz.getDeclaredMethod(data.Get);

                        }

                    } catch (NoSuchMethodException e) {

                        if (firstClass == null)
                            firstClass = clazz;

                        clazz = clazz.getSuperclass();

                        if (clazz == null && !primitivesChecked) {

                            primitivesChecked = true;

                            clazz = firstClass;

                            if (hasPrimitiveType(type))
                                type = getPrimitiveType(type);

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

            } catch (Exception ex) {

                ex.printStackTrace();

                continue;

            }

        }

    }

    @Nullable
    private static Class getPrimitiveType(Class c) {

        switch (c.getSimpleName()) {
            case "Boolean":
                return Boolean.TYPE;
            case "Byte":
                return Byte.TYPE;
            case "Character":
                return Character.TYPE;
            case "Float":
                return Float.TYPE;
            case "Integer":
                return Integer.TYPE;
            case "Long":
                return Long.TYPE;
            case "Short":
                return Short.TYPE;
            case "Double":
                return Double.TYPE;

        }

        return null;
    }

    private static boolean hasPrimitiveType(Class c) {

        if (c.equals(Boolean.class) ||
                c.equals(Byte.class) ||
                c.equals(Character.class) ||
                c.equals(Float.class) ||
                c.equals(Integer.class) ||
                c.equals(Long.class) ||
                c.equals(Short.class) ||
                c.equals(Double.class))

            return true;

        return false;

    }

}
