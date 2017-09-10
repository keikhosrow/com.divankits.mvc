package com.divankits.mvc.net;

import com.divankits.mvc.IModel;
import com.divankits.mvc.security.ISecureModel;

public interface IWebModel extends ISecureModel {

    String toJSON();
    IWebModel fromJSON(String json);

}
