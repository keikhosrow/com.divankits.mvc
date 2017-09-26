package com.divankits.mvc.core;


import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.divankits.mvc.Model;
import com.divankits.mvc.forms.Bind;
import com.divankits.mvc.forms.ItemView;
import com.divankits.mvc.forms.View;
import com.divankits.mvc.generic.PropertyInfo;
import com.divankits.mvc.generic.Tuple;
import com.divankits.mvc.validation.ValidationResult;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class ModelRenderer extends Fragment {


    private static final String MODIFIERS_SUFFIX = "Modifier";
    private static final String VALIDATOR_SUFFIX = "Validator";
    private static final String CONVERTER_CONVERT_BACK = "convertBack";
    private static final String ERR_001 = "Submit is not defined in model";
    private static final String ERR_002 = "Layout is not defined in model";
    private static final String ERR_003 = "No View Bounds to Collection";

    private IOnModelChangedEventListener changeListener;
    private Model mModel;
    private android.view.View mView;
    private Context mContext;
    private int mLayoutId;
    private int mSubmitId;

    public ModelRenderer() {

        super();

    }

    public void bindEvents() throws NoSuchFieldException, IllegalAccessException {

        if (getOnModelChangedEventListener() == null)
            return;

        for (final PropertyInfo prop : getModel().getProperties()) {

            try {

                ArrayList<BoundData> boundList = prop.getBoundData();

                if (boundList.isEmpty())
                    continue;

                for (final BoundData details : boundList) {

                    android.view.View v = getView().findViewById(details.Target);

                    if (v == null) {

                        break;

                    }

                    switch (details.Event) {

                        case None:
                            continue;

                        case Click:

                            v.setClickable(true);

                            v.setOnClickListener(new android.view.View.OnClickListener() {
                                @Override
                                public void onClick(android.view.View view) {

                                    try {

                                        Object value = prop.getValue();

                                        getOnModelChangedEventListener().onFieldChanged(details, value);

                                    } catch (Exception e) {

                                        e.printStackTrace();

                                    }

                                }
                            });

                            break;

                        case Focus:
                        case Blur:

                            v.setFocusable(true);

                            v.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
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

            }

        }

    }

    public ModelRenderer update(final boolean fromModel) {

        return update(getModel(), fromModel);

    }

    public ModelRenderer update(Model model, final boolean fromModel) {

        IOnModelChangedEventListener listener = getOnModelChangedEventListener();

        for (PropertyInfo prop : getModel().getProperties()) {

            try {

                ArrayList<BoundData> boundList = prop.getBoundData();

                if (boundList.isEmpty())
                    continue;

                for (BoundData data : boundList) {

                    android.view.View view = getView().findViewById(data.Target);

                    if (!data.AutoUpdate || view == null)
                        continue;

                    Object value = prop.getValue();

                    // creates adapter and custom view for collections

                    if (prop.isCollection()) {

                        if (view instanceof AdapterView && prop.isAnnotationPresent(ItemView.class)) {

                            AdapterView target = (AdapterView) view;

                            if (target.getAdapter() == null) {

                                target.setAdapter(createCollectionAdapter(target, prop));

                                target.setOnItemClickListener(createCollectionItemClickListener());

                            }

                            continue;

                        }

                    }

                    Method method = findMethod(prop, data, fromModel);


                    if (method == null)
                        continue;


                    if (fromModel) {

                        if (value != null) {

                            if (data.Converter != null)
                                value = data.Converter.convertBack(value);

                            method.invoke(view, value);

                        }

                    } else {

                        Object newValue = method.invoke(view);

                        if (data.Converter != null)
                            newValue = data.Converter.convert(newValue);

                        prop.setValue(newValue);

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

            mView = inflater.inflate(getLayoutId(), container, false);

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

                        // updating values of fields

                        ModelRenderer.this.update(false)
                                .modify(ModelRenderer.this.getModel(), false);

                        if (changeListener != null)
                            changeListener.onSubmit(getModel());

                    }

                });


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public android.view.View getView() {

        return mView;

    }

    public Model getModel() {

        return this.mModel;

    }

    public ModelRenderer setModel(Model model) {

        return setModel(model, -1);

    }

    public ModelRenderer setModel(Model model, int layoutId) {

        return setModel(model, layoutId, -1);

    }

    public ModelRenderer setModel(Model model, int layoutId, int submitId) {

        this.mModel = model;
        this.mLayoutId = layoutId;
        this.mSubmitId = submitId;

        modify(this.mModel, false);

        return this;

    }

    public int getSubmitId() throws NullPointerException {

        try {

            return getViewAnnotationParams(ERR_001).submit();

        } catch (Exception e) {

            return mSubmitId;

        }

    }

    public int getLayoutId() throws NullPointerException {

        try {

            return getViewAnnotationParams(ERR_002).value();

        } catch (Exception e) {

            return mLayoutId;

        }

    }

    public IOnModelChangedEventListener getOnModelChangedEventListener() {

        return this.changeListener;

    }

    public ModelRenderer setOnModelChangedEventListener(IOnModelChangedEventListener listener) {

        this.changeListener = listener;

        return this;

    }

    public ModelRenderer modify(Model model, boolean restore) {

        List<Tuple<PropertyInfo , ModelModifier>> modifiers = new ArrayList<>();

        for (PropertyInfo prop : model.getProperties()) {

            // check if field is collection

            if (prop.isCollection()) {

                try {

                    Object value = prop.getValue();

                    if (value == null)
                        continue;

                    Iterator collection = prop.isArray() ? Arrays.asList(prop.getValue()).iterator() :
                            Iterable.class.cast(value).iterator();

                    while (collection.hasNext()) {

                        Object item = collection.next();

                        // apply item modifiers

                        if (item instanceof Model) {

                            modify((Model) item, restore);

                        }

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    continue;

                }

            }

            // for non-collections

            Annotation[] annotations = prop.getDeclaredAnnotations();

            for (Annotation a : annotations) {

                String name = a.annotationType().getName().concat(MODIFIERS_SUFFIX);

                try {

                    Class<?> clazz = Class.forName(name);

                    ModelModifier object = (ModelModifier) clazz.getConstructor(a.annotationType())
                            .newInstance(a);

                    modifiers.add(new Tuple(prop, object));

                } catch (Exception e) {

                    continue;

                }

            }

        }

        for (Tuple<PropertyInfo , ModelModifier> m : modifiers)
            m.Item2.invoke(m.Item1, restore);

        return this;

    }

    public void setContext(Context context) {

        mContext = context;

    }

    private View getViewAnnotationParams(String exMessage)
            throws NullPointerException {

        if (!getModel().getClass().isAnnotationPresent(View.class))
            throw new NullPointerException(exMessage);

        return getModel().getClass().getAnnotation(View.class);

    }

    private ArrayAdapter<Model> createCollectionAdapter(AdapterView view, PropertyInfo prop)
            throws NoSuchFieldException, IllegalAccessException, NullPointerException {

        final ItemView iw = prop.getAnnotation(ItemView.class);

        if (iw == null)
            throw new NullPointerException(ERR_003);

        return new ArrayAdapter<Model>(mContext, getViewAnnotationParams(ERR_003).value(),
                (List<Model>) prop.getValue()) {

            @Override
            public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {


                android.view.View v = convertView;

                if (v == null)
                    v = LayoutInflater.from(getContext()).inflate(iw.value(), null);

                Model p = getItem(position);

                if (p == null)
                    return v;

                for (PropertyInfo prop : p.getProperties()) {

                    try {

                        ArrayList<BoundData> dt = prop.getBoundData();

                        Object value = prop.getValue();

                        for (BoundData b : dt) {

                            Method m = findMethod(v, prop, b, true);

                            if (value != null && m != null) {

                                android.view.View target = v.findViewById(b.Target);

                                if (b.Converter != null)
                                    value = b.Converter.convertBack(value);

                                m.invoke(target, value);

                            }

                        }

                    } catch (Exception e) {

                        continue;

                    }

                }

                return v;

            }


        };
    }

    private AdapterView.OnItemClickListener createCollectionItemClickListener() {

        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, android.view.View view, int i, long l) {

                if (changeListener != null)
                    changeListener.onCollectionItemSelected(mModel, adapterView.getAdapter().getItem(i));

            }

        };

    }

    private Method findMethod(PropertyInfo property, BoundData data, boolean setter)
            throws NoSuchMethodException {

        return findMethod(getView(), property, data, setter);

    }

    private Method findMethod(android.view.View view, PropertyInfo property, BoundData data, boolean setter)
            throws NoSuchMethodException, NullPointerException {

        Class compType, fieldType, fieldPrimitiveType;

        fieldType = property.getType();
        compType = view.findViewById(data.Target).getClass();
        Method method = null;
        boolean primitivesChecked = false;


        if (setter && data.Converter instanceof ValueConverter) {

            fieldType = Utilities.getConverterTypes(data.Converter).Item1;

        }

        fieldPrimitiveType = Utilities.getPrimitive(fieldType);


        while (method == null) {

            try {

                if (setter) {

                    method = compType.getMethod(data.Set, fieldType);

                } else {

                    method = compType.getMethod(data.Get);

                }


            } catch (NoSuchMethodException e) {

                if (!primitivesChecked) {

                    primitivesChecked = true;

                    if (fieldPrimitiveType != null)
                        fieldType = fieldPrimitiveType;

                } else {

                    break;

                }

            }

        }

        return method;

    }

    public ValidationResult getModelState() {

        Resources res = getActivity().getResources();

        List<Tuple<Validator , Annotation>> validators = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (PropertyInfo field : getModel().getProperties()) {

            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {

                String name = annotation.annotationType().getName().concat(VALIDATOR_SUFFIX);

                Class<?> clazz;

                try {

                    if (names.contains(name))
                        continue;

                    clazz = Class.forName(name);

                    Constructor<?> ctor = clazz.getConstructor(Resources.class);

                    Validator object = (Validator) ctor.newInstance(res);

                    names.add(name);

                    validators.add(new Tuple(object, annotation));

                } catch (Exception e) {

                    // ignore all exceptions

                }

            }

        }

        return validate(validators.toArray(new Tuple[validators.size()]));

    }

    private ValidationResult validate(Tuple<Validator , Annotation>... pairs) {

        ValidationResult result = new ValidationResult();

        for (Tuple<Validator , Annotation> p : pairs) {

            result.concat(p.Item1.validate(p.Item2.annotationType(), this));

        }

        return result;

    }


}
