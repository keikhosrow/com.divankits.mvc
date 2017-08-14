package com.divankits.mvc.net;

import com.divankits.mvc.IModel;
import com.divankits.mvc.security.SecureModel;

public class WebModel extends SecureModel implements IWebModel {

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public IModel fromJSON(String json) {
        return null;
    }

}
