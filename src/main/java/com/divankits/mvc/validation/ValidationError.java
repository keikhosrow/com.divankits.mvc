package com.divankits.mvc.validation;


import java.lang.reflect.Field;

public class ValidationError {

    public int Error;

    public String Message;

    public String FieldName;

    public ValidationError() { }

    public ValidationError(int error , String message , String field) {

        Error = error;
        Message = message;
        FieldName = field;

    }

}
