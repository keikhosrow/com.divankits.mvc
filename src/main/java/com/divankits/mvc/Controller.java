package com.divankits.mvc;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.support.annotation.AnimatorRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.divankits.mvc.core.BoundData;
import com.divankits.mvc.core.IModelRenderer;
import com.divankits.mvc.core.IOnModelChangedEventListener;
import com.divankits.mvc.core.ModelRenderer;
import com.divankits.mvc.validation.ValidationResult;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private static String VALIDATOR_SUFFIX = "Validator";

    private IModelRenderer renderer;
    private Activity activity;
    private int placeholder;
    private int[] animations;
    private List<IModel> stack;
    private IOnModelChangedEventListener mListener;
    private String view;

    public Controller(Activity activity, int placeholder) {

        this.activity = activity;
        this.placeholder = placeholder;
        stack = new ArrayList<>();

    }

    public void setOnModelChangedEventListener(IOnModelChangedEventListener listener) {

        this.mListener = listener;

    }

    public IModelRenderer getRenderer() {

        return renderer;

    }

    public IModelRenderer setRenderer(IModelRenderer renderer) {

        this.renderer = renderer;

        return this.renderer;

    }

    public FragmentManager getFragmentManager() {

        return activity.getFragmentManager();

    }

    public int getPlaceholderId() {

        return placeholder;

    }

    public Activity getActivity() {

        return activity;

    }

    public void view(String page) {

        for (Method m : getClass().getDeclaredMethods()) {

            if (!methodHasName(m, page, false))
                continue;

            try {

                if (!m.isAnnotationPresent(Submit.class)) {

                    view = page;

                    m.invoke(this);

                }

            } catch (Exception e) {

                e.printStackTrace();

                break;

            }

        }

    }

    public void view(String page, IModel model) {

        setModel(model);

        view(page);

    }

    public void setModel(IModel model) {

        setModel(model, true);

    }

    public void setModel(IModel model, boolean addToStack) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (animations != null)
            if (animations.length == 2)
                ft.setCustomAnimations(animations[0], animations[1]);
            else
                ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);


        if (getRenderer() != null && getRenderer().getModel() != null && addToStack)
            addToStack();

        setRenderer(new ModelRenderer())
                .setModel(model)
                .setOnModelChangedEventListener(new IOnModelChangedEventListener() {

                    @Override
                    public void onFieldChanged(BoundData details, Object oldValue) {

                        if (mListener != null)
                            mListener.onFieldChanged(details, oldValue);

                    }

                    @Override
                    public void onSubmit(IModel model) {

                        Controller.this.onSubmit(model);

                        if (mListener != null)
                            mListener.onSubmit(model);

                    }

                    @Override
                    public void onCreate(View view) {

                        if (mListener != null)
                            mListener.onCreate(view);

                    }

                });


        ft.replace(placeholder, (ModelRenderer) getRenderer()).commit();

    }

    private void addToStack() {

        stack.add(getRenderer().getModel());

    }

    public int getStackEntryCount() {

        return stack.size();

    }

    public void clearStack() {

        stack.clear();

    }

    @Nullable
    private IModel popStack() {

        if (getStackEntryCount() > 0)
            return stack.remove(stack.size() - 1);

        return null;

    }

    public boolean popBackStack() {

        IModel model = popStack();

        if (model != null)
            setModel(model, false);

        return model != null;

    }

    public void setAnimations(@AnimatorRes int a1, @AnimatorRes int a2) {
        animations = new int[]{a1, a2};
    }

    public void setAnimations(@AnimatorRes int a1, @AnimatorRes int a2, @AnimatorRes int a3, @AnimatorRes int a4) {
        animations = new int[]{a1, a2, a3, a4};
    }

    public void clearAnimations() {

        animations = null;

    }

    public void onSubmit(IModel model) {

        try {

            // updating values of fields

            getRenderer().update(false).modify(getRenderer().getModel() , false);

            // calling associated submit method

            for (Method m : getClass().getDeclaredMethods()) {

                Type[] params = m.getGenericParameterTypes();

                if (!m.isAnnotationPresent(Submit.class) || params.length < 1)
                    continue;

                // checking if model has set by view() method

                if (view != null && !methodHasName(m, view, false))
                    continue;

                try {

                    Class clazz = model.getClass(), paramClass = (Class) params[0];

                    if (model.getClass() == clazz && paramClass == clazz) {

                        m.invoke(this, model);

                        view = null;

                        return;

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    break;

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private boolean methodHasName(Method method, String name, boolean matchCase) {

        return matchCase ? method.getName().equals(name) :
                method.getName().toLowerCase().equals(name.toLowerCase());

    }

    public ValidationResult getModelState() {

        Resources res = getActivity().getResources();

        List<ValidatorClassHandler> validators = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (Field field : getRenderer().getModel().getClass().getFields()) {

            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {

                String name = annotation.annotationType().getName().concat(VALIDATOR_SUFFIX);

                Class<?> clazz = null;

                try {

                    if (names.contains(name))
                        continue;

                    clazz = Class.forName(name);

                    Constructor<?> ctor = clazz.getConstructor(Resources.class);

                    Validator object = (Validator) ctor.newInstance(res);

                    names.add(name);

                    validators.add(new ValidatorClassHandler(object, annotation));

                } catch (Exception e) {

                    continue;

                }

            }

        }

        return validate(validators.toArray(new ValidatorClassHandler[validators.size()]));

    }

    private ValidationResult validate(ValidatorClassHandler... handlers) {

        ValidationResult result = new ValidationResult();

        for (ValidatorClassHandler handler : handlers) {

            result.concat(handler.validator.validate(handler.annotation.annotationType(),
                    getRenderer()));

        }

        return result;

    }

    private class ValidatorClassHandler {

        private Validator validator;

        private Annotation annotation;

        public ValidatorClassHandler(Validator validator, Annotation annotation) {

            this.validator = validator;
            this.annotation = annotation;

        }

    }

}
