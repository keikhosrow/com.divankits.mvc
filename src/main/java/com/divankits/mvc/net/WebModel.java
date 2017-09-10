package com.divankits.mvc.net;

import com.divankits.mvc.net.converters.WebModelToJSONBuilderConverter;
import com.divankits.mvc.net.json.JSONBuilder;
import com.divankits.mvc.security.SecureModel;

public abstract class WebModel extends SecureModel implements IWebModel {

    private WebModelToJSONBuilderConverter converter = new WebModelToJSONBuilderConverter();

    @Override
    public String toJSON() {

        return converter.convert(this).toString();

    }

    @Override
    public IWebModel fromJSON(String json) {

        JSONBuilder builder = new JSONBuilder(json);

        return converter.convertBack(builder);

    }

}
