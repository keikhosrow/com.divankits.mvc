package com.divankits.mvc.core.schema;


import com.divankits.mvc.core.ModelModifier;
import com.divankits.mvc.core.ModifyOnce;
import com.divankits.mvc.generic.PropertyInfo;

import java.util.HashMap;
import java.util.UUID;

@ModifyOnce
public class IdentityModifier extends ModelModifier<Identity> {

    private static HashMap<Class, Integer> counter = new HashMap<>();

    public IdentityModifier(Identity modifier) {

        super(modifier);

    }

    public static int getRandomHex(int min, int max) {

        return min + ((int) (Math.random() * (max - min)));

    }

    @Override
    public Object modify(PropertyInfo property) {

        Object value = null, newValue = null;

        try {

            value = property.getValue();

            if (value != null)
                return value;

            Identity.GenerateType type = getModifier().value();

            switch (type) {

                case Guid:

                    newValue = UUID.randomUUID().toString();
                    break;

                case RandomNumber:

                    newValue = getRandomHex(0x0, 0xffffff);
                    break;

                case Indexing:

                    Class clazz = property.getOwner().getClass();

                    if (!counter.containsKey(clazz))
                        counter.put(clazz, -1);

                    int count = counter.get(clazz).intValue();
                    counter.remove(clazz);
                    counter.put(clazz, ++count);

                    newValue = count;

                    break;

            }

            value = newValue.toString();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return value;

    }

    @Override
    public Object restore(PropertyInfo property) {

        Object value = null;

        try {

            value = property.getValue();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return value;

    }

    public static void seed(Class entity, int begin) {

        if (!counter.containsKey(entity)) {
            counter.put(entity, begin);
            return;
        }

        counter.remove(entity);
        counter.put(entity, begin);

    }

}