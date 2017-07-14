package com.divankits.mvc;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.support.annotation.AnimatorRes;
import android.view.View;

import com.divankits.mvc.annotations.Submit;
import com.divankits.mvc.validation.ValidationResult;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Controller implements IController {

    private IModelRenderer renderer;

    private Activity activity;

    private int placeholder;

    private int[] animations;

    public Controller(Activity activity, int placeholder) {

        this.activity = activity;

        this.placeholder = placeholder;

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

    public void setModel(IModel model) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (animations != null) {

            if (animations.length == 2) {

                ft.setCustomAnimations(animations[0], animations[1]);

            } else {

                ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);

            }

        }

        setRenderer(new ModelRenderer())
                .setModel(model)
                .setOnModelChangedEventListener(new IOnModelChangedEventListener() {
                    @Override
                    public void onFieldChanged(BindDetails details, Object oldValue) {
                        Controller.this.onFieldChanged(details, oldValue);
                    }

                    @Override
                    public void onSubmit(IModel model) {
                        Controller.this.onSubmit(model);
                    }

                    @Override
                    public void onCreate(View view) {
                        Controller.this.onCreate(view);
                    }
                });

        ft.replace(placeholder, (ModelRenderer) renderer);

        ft.addToBackStack(null);

        ft.commit();

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

            Binder.updateValues(getRenderer(), false);

            Method[] methods = this.getClass().getDeclaredMethods();

            for (Method method : methods) {

                if (!method.isAnnotationPresent(Submit.class))
                    continue;

                Class clazz = model.getClass(), paramClass = null;

                Type[] params = method.getGenericParameterTypes();

                if (params.length > 0)
                    paramClass = (Class) params[0];

                if (paramClass == null)
                    continue;

                if (model.getClass() == clazz && paramClass == clazz) {

                    method.invoke(this, model);

                    break;

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void onFieldChanged(BindDetails details, Object oldValue) {
    }

    public abstract void onCreate(View view);

    public ValidationResult getModelState() {

        Resources res = getActivity().getResources();

        Field[] fields = getRenderer().getModel().getFields();

        List<ValidatorClassHandler> validators = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (Field field : fields) {

            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {

                String name = annotation.annotationType().getName().concat("Validator");

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


    private ValidationResult validate(ValidatorClassHandler... validators) {

        ValidationResult result = new ValidationResult();

        for (ValidatorClassHandler validator : validators) {

            result.concat(validator.Validator.validate(validator.Annotation.annotationType(), getRenderer()));

        }

        return result;

    }

    private class ValidatorClassHandler {

        public Validator Validator;
        public Annotation Annotation;

        public ValidatorClassHandler(Validator validator, Annotation annotation) {

            this.Validator = validator;
            this.Annotation = annotation;

        }

    }

}
