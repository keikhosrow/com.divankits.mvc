package com.divankits.mvc.net.json;

import android.util.Log;

import com.divankits.mvc.core.Utilities;
import com.divankits.mvc.generic.PropertyInfo;
import com.divankits.mvc.net.IWebModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class JSONParser<T extends IWebModel> {

    private final static String ARRAY_SEPARATOR = ",";
    private final Class<T> typeClass;
    private JSONObject object;

    public JSONParser(Class<T> typeClass, JSONBuilder builder) throws JSONException {

        this.typeClass = typeClass;

        object = builder.toJSONObject();

    }

    private void parse(PropertyInfo property, Object value) {


        // ignore synthetic and internal properties

        if (property.isSynthetic() || property.isAnnotationPresent(Internal.class))
            return;


        Class propType, compType;
        Constructor constructor = null;

        propType = property.getType();



        // check if can set property directly

        if (Utilities.hasPrimitive(propType) || Utilities.isString(propType)) {

            try {

                property.setValue(value);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return;

        }



        // find component type if exist

        if (property.isAnnotationPresent(ComponentType.class)) {

            compType = property.getAnnotation(ComponentType.class).value();

        } else {

            compType = propType.getComponentType();

        }



        try {

            // get item constructor

            if (compType == null) {

                constructor = propType.getConstructor();

            } else {

                constructor = compType.getConstructor();

            }

        } catch (NoSuchMethodException e) {

            // constructor not found

        }



        if (property.isCollection()) {

            try {

                JSONArray array = (JSONArray) value;
                ArrayList itemVal = new ArrayList<>();

                boolean hasPrimitive = Utilities.hasPrimitive(compType) || compType.isPrimitive();



                for (int i = 0; i < array.length(); i++) {


                    // check if json value is like "[#, #, #, ...]"

                    if (hasPrimitive && property.isArray()) {

                        String a = array.toString();

                        if (compType == Integer.TYPE) {

                            // property is int[]

                            property.setValue(Utilities.toIntegerArray(a , ARRAY_SEPARATOR));

                        } else if (compType == Double.TYPE) {

                            // property is double[]

                            property.setValue(Utilities.toDoubleArray(a , ARRAY_SEPARATOR));

                        } else if (compType == Float.TYPE) {

                            // property is float[]

                            property.setValue(Utilities.toFloatArray(a , ARRAY_SEPARATOR));

                        } else if (compType == Long.TYPE) {

                            // property is long[]

                            property.setValue(Utilities.toLongArray(a , ARRAY_SEPARATOR));

                        }

                        break;

                    }




                    Object item = array.get(i), instance;

                    if (constructor != null) {

                        instance = constructor.newInstance();


                        // set items properties

                        for (Field f : instance.getClass().getFields()) {

                            PropertyInfo itemProperty = new PropertyInfo(instance, f);

                            parse(itemProperty, item);

                        }


                        itemVal.add(instance);

                    } else if (compType != null) {

                        itemVal.add(item);

                    }

                }



                if (!hasPrimitive && property.isArray()) {

                    property.setValue(itemVal.toArray());

                } else if (!hasPrimitive) {

                    property.setValue(itemVal);

                }


            } catch (Exception ex) {

                ex.printStackTrace();

                return;

            }


        } else {

            JSONObject object;
            Object itemInstance = null;

            try {

                if(value == null || value.equals("null")) {

                    property.setValue(null);

                    return;

                }

                object = value instanceof JSONObject ? (JSONObject) value : new JSONObject((String) value);


                if(constructor != null){

                    itemInstance = constructor.newInstance();

                }

                if (itemInstance != null && !Utilities.hasPrimitive(itemInstance) && !Utilities.isString(itemInstance)) {

                    for (Field f : itemInstance.getClass().getFields()) {

                        PropertyInfo itemProperty = new PropertyInfo(itemInstance, f);

                        if (itemProperty.isSynthetic()) {

                            continue;

                        }

                        try {

                            parse(itemProperty, object.get(itemProperty.getName().toLowerCase()));

                        } catch (Exception e) {

                            e.printStackTrace();

                            continue;

                        }

                    }

                } else {

                    String propName = property.getName().toLowerCase();
                    String val = (String) object.get(propName);

                    if (propType == Integer.TYPE) {

                        // property is int

                        property.setValue(Integer.parseInt(val));

                    } else if (propType == Double.TYPE) {

                        // property is double

                        property.setValue(Double.parseDouble(val));

                    } else if (propType == Float.TYPE) {

                        // property is float

                        property.setValue(Float.parseFloat(val));

                    } else if (propType == Long.TYPE) {

                        // property is long

                        property.setValue(Long.parseLong(val));

                    } else {

                        property.setValue(val);

                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

                return;

            }

        }

    }

    public T parse() {

        T result = null;

        try {

            result = typeClass.newInstance();

            if (object == null) {

                return result;

            }

            for (PropertyInfo p : result.getProperties()) {

                String param = p.getName().toLowerCase();

                if (!object.has(param))
                    continue;

                parse(p, object.get(param));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        Log.e("result", result.toString());

        return result;

    }

}
