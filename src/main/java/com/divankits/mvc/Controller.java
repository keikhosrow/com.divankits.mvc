package com.divankits.mvc;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.AnimatorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.divankits.mvc.core.BoundData;
import com.divankits.mvc.core.IOnModelChangedEventListener;
import com.divankits.mvc.core.ModelRenderer;
import com.divankits.mvc.forms.Submit;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private ModelRenderer renderer;
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

    public ModelRenderer getRenderer() {

        return renderer;

    }

    public ModelRenderer setRenderer(ModelRenderer renderer) {

        this.renderer = renderer;

        this.renderer.setContext(getActivity());

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

    public void view(IModel model) {

        setModel(model);

    }

    public void setModel(IModel model) {

        setModel(model, -1 , true);

    }

    public void setModel(IModel model, @LayoutRes int layoutId ){

        setModel(model, layoutId , true);

    }

    public void setModel(IModel model, boolean addToStack) {

        setModel(model,  -1 ,  addToStack);

    }

    public void setModel(IModel model, @LayoutRes int layoutId , boolean addToStack){

        setModel(model , layoutId , -1 , addToStack);

    }

    public void setModel(IModel model, @LayoutRes int layoutId , @IdRes int submitId){

        setModel(model , layoutId , submitId , true);

    }

    public void setModel(IModel model, @LayoutRes int layoutId , @IdRes int submitId , boolean addToStack){

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (animations != null)
            if (animations.length == 2)
                ft.setCustomAnimations(animations[0], animations[1]);
            else
                ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);


        if (getRenderer() != null && getRenderer().getModel() != null && addToStack)
            addToStack();

        setRenderer(new ModelRenderer())
                .setModel(model, layoutId , submitId)
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

                    @Override
                    public void onCollectionItemSelected(IModel model , Object item) {

                        if(mListener != null)
                            mListener.onCollectionItemSelected(model , item);

                    }

                });


        ft.replace(placeholder, getRenderer()).commit();

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

}
