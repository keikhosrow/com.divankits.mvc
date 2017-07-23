package com.divankits.mvc.validation;

import java.util.ArrayList;

public class ValidationResult {

    private ArrayList<ValidationError> errors;

    public ArrayList<ValidationError> getErrors(){

        if(errors == null)
            errors = new ArrayList<>();

        return errors;

    }

    public boolean isValid() {

        return getErrors().size() == 0;

    }

    public String toString(){

        String status = "";

        for (ValidationError error : getErrors()) {

            status = status.concat(error.Message + "\n");

        }

        return status;

    }

    public void concat(ValidationResult result){

        getErrors().addAll(result.getErrors());

    }

    /**
     * @deprecated
     * Use toString() instead
     */
    @Deprecated
    public String trace() {

        return toString();

    }

}
