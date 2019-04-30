/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class JsPlaceRequest extends JavaScriptObject {

    protected JsPlaceRequest() {
    }

    public static JsPlaceRequest fromPlaceRequest(PlaceRequest pr) {
        JsPlaceRequest jspr = newInstance();
        jspr.setIdentifier(pr.getIdentifier());
        JSONObject rawParams = new JSONObject();
        for (String name : pr.getParameterNames()) {
            rawParams.put(name,
                          new JSONString(pr.getParameters().get(name)));
        }
        jspr.setParams(rawParams.getJavaScriptObject());
        return jspr;
    }

    public static native JsPlaceRequest newInstance() /*-{
        return {identifier: '', params: {}};
    }-*/;

    public final native String getIdentifier() /*-{
        return this.identifier;
    }-*/;

    public final native void setIdentifier(final String newIdentifier) /*-{
        this.identifier = newIdentifier;
    }-*/;

    public final native JavaScriptObject getParams() /*-{
        return this.params;
    }-*/;

    public final native void setParams(final JavaScriptObject newParams) /*-{
        this.params = newParams;
    }-*/;
}
