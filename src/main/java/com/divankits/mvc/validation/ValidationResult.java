package com.divankits.mvc.validation;


import java.util.ArrayList;

public class ValidationResult {

    public ArrayList<ValidationError> Errors;

    public ValidationResult() {

        Errors = new ArrayList<>();

    }

    public void concat(ValidationResult result){

        Errors.addAll(result.Errors);

    }

    public boolean isValid() {

        return Errors.size() == 0;

    }

    public String trace() {

        String status = "";

        for (ValidationError error : Errors) {

            status = status.concat(error.Message + "\n");

        }

        return status;

    }

}
