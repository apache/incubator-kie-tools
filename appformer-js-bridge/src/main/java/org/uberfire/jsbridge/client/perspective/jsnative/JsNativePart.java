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

package org.uberfire.jsbridge.client.perspective.jsnative;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import org.uberfire.commons.data.Pair;

import static java.util.stream.Collectors.toMap;

public class JsNativePart {

    private final JavaScriptObject self;
    private final JsNativeContextDisplay contextDisplay;

    public JsNativePart(final JavaScriptObject self) {
        this.self = self;
        this.contextDisplay = new JsNativeContextDisplay(self, "displayInfo");
    }

    public String placeName() {
        return (String) get("placeName");
    }

    public Map<String, String> parameters() {

        final JavaScriptObject jsParameters = (JavaScriptObject) get("parameters");
        if (jsParameters == null) {
            return new HashMap<>();
        }

        final JSONObject parametersJson = new JSONObject(jsParameters);
        return parametersJson.keySet().stream()
                .map(key -> new Pair<>(key, parametersJson.get(key).isString().stringValue()))
                .collect(toMap(Pair::getK1, Pair::getK2));
    }

    public JsNativeContextDisplay contextDisplay() {
        return contextDisplay;
    }

    private native Object get(final String fieldToInvoke)   /*-{
        return this.@org.uberfire.jsbridge.client.perspective.jsnative.JsNativePart::self[fieldToInvoke];
    }-*/;
}
