package com.divankits.mvc.net;

import com.divankits.mvc.Model;
import com.divankits.mvc.net.converters.WebModelToJSONBuilderConverter;
import com.divankits.mvc.net.json.JSONBuilder;

public abstract class WebModel extends Model {

    private WebModelToJSONBuilderConverter converter = new WebModelToJSONBuilderConverter();

    public String toJSON() {

        return converter.convert(this).toString();

    }

    public WebModel fromJSON(String json) {

        JSONBuilder builder = new JSONBuilder(json);

        return converter.convertBack(builder);

    }

}
