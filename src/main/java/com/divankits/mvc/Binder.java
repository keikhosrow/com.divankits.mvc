package com.divankits.mvc;


import android.view.View;

import com.divankits.mvc.annotations.Bind;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Binder {

    public static void bindEvents(final IModelRenderer renderer)
            throws NoSuchFieldException, IllegalAccessException {


        if (renderer.getOnModelChangedEventListener() == null)
            return;

        for (Field field : renderer.getModel().getFields()) {

            try {

                final BindDetails details = renderer.getBindDetails(field);

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


    public static void updateValues(IModelRenderer renderer, boolean fromModel)
            throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        IModel model = renderer.getModel();

        IOnModelChangedEventListener listener = renderer.getOnModelChangedEventListener();

        for (Field field : model.getFields()) {

            BindDetails details = renderer.getBindDetails(field);

            String name  = field.getName();

            Object value = model.getFieldValue(field.getName());

            if (details == null || !details.AutoUpdate)
                continue;

            Class clazz = details.Target.getClass();

            Method method = null;

            while (method == null && clazz != null) {

                try {

                    if (fromModel)
                        method = clazz.getDeclaredMethod(details.Setter, field.getType());
                    else
                        method = clazz.getDeclaredMethod(details.Getter);

                } catch (NoSuchMethodException e) {

                    clazz = clazz.getSuperclass();

                }

            }

            if (method == null)
                continue;

            if (fromModel) {

                if (value != null)
                    method.invoke(details.Target, value);

            } else {

                model.setFieldValue(name, method.invoke(details.Target));

                if (listener != null && details.Event == Bind.Events.Change)
                    listener.onFieldChanged(details, value);

            }

        }

    }



}
