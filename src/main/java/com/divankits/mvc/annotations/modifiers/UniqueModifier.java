package com.divankits.mvc.annotations.modifiers;


import com.divankits.mvc.IModel;
import com.divankits.mvc.ModelModifier;

import java.lang.reflect.Field;
import java.util.UUID;

public class UniqueModifier extends ModelModifier {

    public static int getRandomHex() {

        return getRandomHex(0x0, 0xffffff);

    }

    public static int getRandomHex(int min, int max) {

        return min + ((int) (Math.random() * (max - min)));

    }

    public static String getRandomGuid() {

        return UUID.randomUUID().toString();

    }

    @Override
    public void modify(IModel model) {

        for (Field field : model.getFields()) {

            if (!field.isAnnotationPresent(Unique.class))
                continue;

            try {

                String name = field.getName();
                Unique.GenerateType policy = field.getAnnotation(Unique.class).value();
                Object value = model.getFieldValue(name);
                Object newValue = policy == Unique.GenerateType.Guid ? getRandomGuid() : getRandomHex();

                if (value == null)
                    model.setFieldValue(name, newValue);

            } catch (Exception e) {

                e.printStackTrace();

                continue;

            }

        }

    }

}