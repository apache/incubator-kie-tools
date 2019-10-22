/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils;

import java.util.Objects;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;

public class JsonVerifier {

    private static final String WARNING = "*******************WARNING*******************";
    private static final String OTHER_ATTRIBUTES = "otherAttributes";
    private static final String H_$ = "$H";

    public static void compareJSITDefinitions(JSITDefinitions original, JSITDefinitions marshalled) {
        final String originalString = asString(original);
        final String marshalledString = asString(marshalled);
        checkCircularIssue(originalString, "originalString");
        checkCircularIssue(marshalledString, "marshalledString");
        JSONValue originalJSONValue = getJSONValue(originalString);
        JSONValue marshalledJSONValue = getJSONValue(marshalledString);
        if (checkNotNull(originalJSONValue, marshalledJSONValue)) {
            compareJSONValue(originalJSONValue, marshalledJSONValue);
        } else {
            GWT.log(WARNING);
            GWT.log("originalJSONValue is  null ? " + Objects.isNull(originalJSONValue));
            GWT.log("marshalledJSONValue is  null ? " + Objects.isNull(marshalledJSONValue));
        }
    }

    private static void checkCircularIssue(String toCheck, String name) {
        if (toCheck.contains("Circular~")) {
            GWT.log(WARNING);
            GWT.log(name + " contains Circular issue");
            GWT.log(toCheck);
        }
    }

    private static void compareJSONValue(JSONValue original, JSONValue marshalled) {
        JSONObject originalJSONObject = original.isObject();
        JSONObject marshalledJSONObject = marshalled.isObject();
        JSONArray originalJSONArray = original.isArray();
        JSONArray marshalledJSONArray = marshalled.isArray();
        if (checkNotNull(originalJSONObject, originalJSONObject)) {
            compareJSONObject(originalJSONObject, marshalledJSONObject);
        } else if (checkNotNull(originalJSONArray, marshalledJSONArray)) {
            compareJSONArray(originalJSONArray, marshalledJSONArray);
        } else if (!Objects.equals(original, marshalled)) {
            GWT.log(WARNING);
            GWT.log("original expected : " + limitedString(original));
            GWT.log("marshalled retrieved : " + limitedString(marshalled));
        }
    }

    private static void compareJSONObject(JSONObject original, JSONObject marshalled) {
        checkKeys(original, marshalled);
        for (String originalKey : original.keySet()) {
            // TODO {gcardosi} to remove after otherAttributes are populated
            if (!Objects.equals(OTHER_ATTRIBUTES, originalKey) && !Objects.equals(H_$, originalKey)) {
                compareJSONObjectKey(original, marshalled, originalKey);
            }
        }
    }

    private static void checkKeys(JSONObject original, JSONObject marshalled) {
        final Set<String> originalKeys = original.keySet();
        final Set<String> marshalledKeys = marshalled.keySet();
        for (String originalKey : originalKeys) {
            if (!marshalledKeys.contains(originalKey)) {
                GWT.log(WARNING);
                GWT.log("original key " + originalKey + " missing in marshalled " + limitedString(marshalled));
            }
        }
    }

    private static void compareJSONObjectKey(JSONObject original, JSONObject marshalled, String key) {
        final JSONValue originalJSONValue = original.get(key);
        final JSONValue marshalledJSONValue = marshalled.get(key);
        if (checkNotNull(originalJSONValue, marshalledJSONValue)) {
            compareJSONValue(originalJSONValue, marshalledJSONValue);
        } else {
            GWT.log(WARNING);
            GWT.log("original " + limitedString(original) + ":" + key + " is null ? " + Objects.isNull(originalJSONValue));
            GWT.log("marshalled " + limitedString(marshalled) + ":" + key + " is null ? " + Objects.isNull(marshalledJSONValue));
        }
    }

    private static boolean compareJSONArray(JSONArray original, JSONArray marshalled) {
        boolean toReturn = true;
        for (int i = 0; i < original.size(); i++) {
            boolean retrieved = false;
            for (int j = 0; j < marshalled.size(); j++) {
                if (compareJSONValueForArray(original.get(i), marshalled.get(j))) {
                    retrieved = true;
                    break;
                }
            }
            if (!retrieved) {
                GWT.log(WARNING);
                GWT.log("original expected " + limitedString(original.get(i)) + " not found in " + limitedString(marshalled));
                toReturn = false;
            }
        }
        return toReturn;
    }

    // COPY'N'PASTE - TO BE DONE BETTER

    private static boolean compareJSONValueForArray(JSONValue original, JSONValue marshalled) {
        JSONObject originalJSONObject = original.isObject();
        JSONObject marshalledJSONObject = marshalled.isObject();
        JSONArray originalJSONArray = original.isArray();
        JSONArray marshalledJSONArray = marshalled.isArray();
        boolean toReturn = true;
        if (checkNotNull(originalJSONObject, originalJSONObject)) {
            toReturn = compareJSONObjectForArray(originalJSONObject, marshalledJSONObject);
        } else if (checkNotNull(originalJSONArray, marshalledJSONArray)) {
            toReturn = compareJSONArray(originalJSONArray, marshalledJSONArray);
        } else {
            toReturn = Objects.equals(original, marshalled);
        }
        return toReturn;
    }

    private static boolean compareJSONObjectForArray(JSONObject original, JSONObject marshalled) {
        boolean toReturn = checkKeysForArray(original, marshalled);
        for (String originalKey : original.keySet()) {
            toReturn = toReturn && compareJSONObjectKeyForArray(original, marshalled, originalKey);
        }
        return toReturn;
    }

    private static boolean checkKeysForArray(JSONObject original, JSONObject marshalled) {
        final Set<String> originalKeys = original.keySet();
        final Set<String> marshalledKeys = marshalled.keySet();
        boolean toReturn = true;
        for (String originalKey : originalKeys) {
            // TODO {gcardosi} to remove after otherAttributes are populated
            if (!marshalledKeys.contains(originalKey) && !Objects.equals(OTHER_ATTRIBUTES, originalKey) && !Objects.equals(H_$, originalKey)) {
                GWT.log(WARNING);
                GWT.log("original key " + originalKey + " missing in marshalled " + limitedString(marshalled));
                toReturn = false;
            }
        }
        return toReturn;
    }

    private static boolean compareJSONObjectKeyForArray(JSONObject original, JSONObject marshalled, String key) {
        final JSONValue originalJSONValue = original.get(key);
        final JSONValue marshalledJSONValue = marshalled.get(key);
        boolean toReturn = true;
        // TODO {gcardosi} to remove after otherAttributes are populated
        if (!Objects.equals(OTHER_ATTRIBUTES, key) && !Objects.equals(H_$, key)) {
            toReturn = checkNotNull(originalJSONValue, marshalledJSONValue) && compareJSONValueForArray(originalJSONValue, marshalledJSONValue);
        }
        return toReturn;
    }

    private static boolean checkNotNull(JSONValue original, JSONValue marshalled) {
        return !Objects.isNull(original) && !Objects.isNull(marshalled);
    }

    private static JSONValue getJSONValue(String jsonString) {
        try {
            return JSONParser.parseStrict(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    private static <D> String limitedString(D toConvert) {
        String toReturn = asString(toConvert);
        if (toReturn.length() > 100) {
            toReturn = toReturn.substring(0, 100);
        }
        return toReturn;
    }

    private static native <D> String asString(D toConvert) /*-{

        function serializer(replacer, cycleReplacer) {
            var stack = [], keys = []

            if (cycleReplacer == null) cycleReplacer = function (key, value) {
                if (stack[0] === value) return "[Circular ~]"
                return "[Circular ~." + keys.slice(0, stack.indexOf(value)).join(".") + "]"
            }
            return function (key, value) {
                if (stack.length > 0) {
                    var thisPos = stack.indexOf(this)
                    ~thisPos ? stack.splice(thisPos + 1) : stack.push(this)
                    ~thisPos ? keys.splice(thisPos, Infinity, key) : keys.push(key)
                    if (~stack.indexOf(value)) value = cycleReplacer.call(this, key, value)
                } else stack.push(value)

                return replacer == null ? value : replacer.call(this, key, value)
            }
        }

        return JSON.stringify(toConvert, serializer(null, null), 2)
    }-*/;
}
