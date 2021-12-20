/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * GWT client-side implementation of URIUtil which relies on URI.js.
 */
public final class URIUtil {

    private static final Resources RESOURCES = GWT.create(Resources.class);

    static {
        ScriptInjector.fromString(RESOURCES.uriDotJs().getText()).inject();
    }

    public static String encode(String content) {
        return URL.encode(content);
    }

    public static String decode(String content) {
        return URL.decode(content);
    }

    public static String encodeQueryString(String content) {
        return URL.encodeQueryString(content);
    }

    public native static boolean isValid(final String uri) /*-{
        var components = URI.parse(uri);
        if (typeof components.errors !== 'undefined' && components.errors.length > 0) {
            return false;
        }
        if (components.reference != "absolute") {
            return false;
        }
        return true;
    }-*/;

    interface Resources extends ClientBundle {

        @Source("uri.min.js")
        TextResource uriDotJs();
    }
}