package com.divankits.mvc.core;

import com.divankits.mvc.generic.Tuple;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Utilities {

    private static final Map<Class, Class> primitives;

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

    public static String[] getNumbers(String array, String separator) {

        return array.replace("\"", "")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .split(separator);

    }

    public static long[] toLongArray(String array, String separator) {

        String[] items = getNumbers(array, separator);

        long[] results = new long[items.length];

        for (int i = 0; i < items.length; i++) {

            results[i] = Long.parseLong(items[i]);

        }

        return results;

    }

    public static float[] toFloatArray(String array, String separator) {

        String[] items = getNumbers(array, separator);

        float[] results = new float[items.length];

        for (int i = 0; i < items.length; i++) {

            results[i] = Float.parseFloat(items[i]);

        }

        return results;

    }

    public static double[] toDoubleArray(String array, String separator) {

        String[] items = getNumbers(array, separator);

        double[] results = new double[items.length];

        for (int i = 0; i < items.length; i++) {

            results[i] = Double.parseDouble(items[i]);

        }

        return results;

    }

    public static int[] toIntegerArray(String array, String separator) {

        String[] items = getNumbers(array, separator);

        int[] results = new int[items.length];

        for (int i = 0; i < items.length; i++) {

            results[i] = Integer.parseInt(items[i]);

        }

        return results;

    }

    public static <E> E[] toPrimitive(E type, Object[] array) {

        E[] retVal = (E[]) new Object[array.length];

        for (int i = 0; i < array.length; i++) {

            retVal[i] = (E) array[i];

        }


        return retVal;

    }

    public static Class getPrimitive(Class fieldType) {

        return primitives.get(fieldType);

    }

    public static boolean hasPrimitive(Object value) {

        if (value == null) return false;

        Class clazz = value.getClass();

        return primitives.containsKey(clazz);

    }

    public static boolean isString(Object value) {

        return value.getClass() == String.class;

    }

    public static Tuple<Class , Class> getConverterTypes(ValueConverter converter) {

        Class t1 = null, t2 = null;

        for (Method m : converter.getClass().getDeclaredMethods()) {

            if (m.getName().equals("convert")) {

                t1 = m.getParameterTypes()[0];
                t2 = m.getReturnType();

            }

        }

        return new Tuple<>(t1, t2);

    }


}
