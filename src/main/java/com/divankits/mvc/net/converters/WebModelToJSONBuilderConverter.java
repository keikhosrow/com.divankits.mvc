package com.divankits.mvc.net.converters;

import com.divankits.mvc.core.ValueConverter;
import com.divankits.mvc.net.WebModel;
import com.divankits.mvc.net.json.JSONBuilder;
import com.divankits.mvc.net.json.JSONParser;

import org.json.JSONException;

public class WebModelToJSONBuilderConverter extends ValueConverter<WebModel, JSONBuilder> {

    @Override
    public JSONBuilder convert(WebModel value) {

        JSONBuilder builder = new JSONBuilder();

        try{

            builder.object(value);

        }catch (IllegalAccessException ex){

            ex.printStackTrace();

        }

        return builder;

    }

    @Override
    public WebModel convertBack(JSONBuilder value)  {

        WebModel result = null;

        try {

            result = new JSONParser(WebModel.class , value).parse();

        } catch (JSONException e) {

             e.printStackTrace();

        }

       return result;


    }

}
