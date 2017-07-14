package com.divankits.mvc.validation.validators;


import android.content.res.Resources;
import android.util.Patterns;

import com.divankits.mvc.IModel;
import com.divankits.mvc.validation.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTypeValidator extends Validator {


    public DataTypeValidator(Resources resources) {
        super(resources);
    }

    public static boolean isEmailAdress(String value) {

        return Patterns.EMAIL_ADDRESS.matcher(value).matches();

    }

    public static boolean isPhoneNumber(String value) {

        return Patterns.PHONE.matcher(value).matches();

    }

    public static boolean isIPAddress(String value) {

        return Patterns.IP_ADDRESS.matcher(value).matches();

    }

    public static boolean isWebURL(String value) {

        return Patterns.WEB_URL.matcher(value).matches();

    }

    public static boolean isDateTime(String value) {

        Date date = null;

        String format = "dd/MM/yyyy";

        try {

            DateFormat sdf = new SimpleDateFormat(format);

            date = sdf.parse(value);

            if (!value.equals(sdf.format(date)))
                date = null;

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

        return date != null;
    }


    @Override
    public int getErrorCode() {

        return 0;

    }

    @Override
    public boolean isValid(IModel model, Field field, Object value, Annotation modifier) {

        try {

            return !hasTypeError((DataType) modifier, model , field);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return true;

    }

    @Override
    public String getErrorDefaultMessage(Field field, Annotation modifier) {

        return ("Field \"")
                .concat(field.getName())
                .concat("\" is not valid ")
                .concat(String.valueOf(((DataType)modifier) .value().toString()));

    }

    private boolean hasTypeError(DataType dt, IModel model, Field field)
            throws NoSuchFieldException, IllegalAccessException {

        String value = (String) model.getFieldValue(field.getName());

        switch (dt.value()) {
            case EmailAddress:
                return !isEmailAdress(value);
            case PhoneNumber:
                return !isPhoneNumber(value);
            case IPAddress:
                return !isIPAddress(value);
            case WebURL:
                return !isWebURL(value);
            case DateTime:
                return !isDateTime(value);
            default:
                return false;
        }

    }


}
