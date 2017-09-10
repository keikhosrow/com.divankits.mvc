package com.divankits.mvc.net.converters;

import com.divankits.mvc.core.ValueConverter;
import com.divankits.mvc.net.IWebModel;
import com.divankits.mvc.net.json.JSONBuilder;
import com.divankits.mvc.net.json.JSONParser;

import org.json.JSONException;

public class WebModelToJSONBuilderConverter extends ValueConverter<IWebModel, JSONBuilder> {

    @Override
    public JSONBuilder convert(IWebModel value) {

        JSONBuilder builder = new JSONBuilder();

        try{

            builder.object(value);

        }catch (IllegalAccessException ex){

            ex.printStackTrace();

        }

        return builder;

    }

    @Override
    public IWebModel convertBack(JSONBuilder value)  {

        IWebModel result = null;

        try {

            result = new JSONParser(IWebModel.class , value).parse();

        } catch (JSONException e) {

             e.printStackTrace();

        }

       return result;


    }

}
