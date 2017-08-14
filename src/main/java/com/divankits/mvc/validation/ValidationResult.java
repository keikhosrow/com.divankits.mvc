package com.divankits.mvc.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

            status = status.concat(error.getMessage() + "\n");

        }

        return status;

    }

    public String first(){

        return getErrors().get(0).getMessage();

    }

    public void concat(ValidationResult result){

        getErrors().addAll(result.getErrors());

    }

    public ValidationResult sortByPriority() {

        Collections.sort(getErrors(), new Comparator<ValidationError>() {
            @Override
            public int compare(ValidationError e1, ValidationError e2)
            {

                if(e1.getPriority() > e2.getPriority())

                    return -1;

                else if(e1.getPriority() > e2.getPriority())

                    return 1;

                else
                    return 0;

            }
        });

        return this;

    }


}
