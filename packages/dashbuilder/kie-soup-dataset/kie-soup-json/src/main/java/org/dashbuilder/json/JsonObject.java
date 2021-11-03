/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-side implementation of JsonObject.
 */
public class JsonObject implements JsonValue {

    private static List<String> stringifyOrder(String[] keys) {
        List<String> toReturn = new ArrayList<String>();
        List<String> nonNumeric = new ArrayList<String>();
        for (String key : keys) {
            if (key.matches("\\d+")) {
                toReturn.add(key);
            } else {
                nonNumeric.add(key);
            }
        }
        Collections.sort(toReturn);
        toReturn.addAll(nonNumeric);
        return toReturn;
    }

    private JsonFactory factory;
    private Map<String, JsonValue> map = new LinkedHashMap<String, JsonValue>();

    public JsonObject(JsonFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean isEmpty() {
        return map == null || map.isEmpty();
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public double asNumber() {
        return Double.NaN;
    }

    @Override
    public String asString() {
        return "[object Object]";
    }

    public boolean has(String key) {
        return map.containsKey(key);
    }
    public JsonValue get(String key) {
        return map.get(key);
    }

    public JsonValue getFirst(Collection<String> keys) {
        for (String key : keys) {
            JsonValue val = get(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    public JsonArray getArray(Collection<String> keys) {
        JsonValue val = getFirst(keys);
        if (val == null || val instanceof JsonNull) {
            return null;
        }
        return (JsonArray) val;

    }
    public JsonArray getArray(String key) {
        JsonValue val = get(key);
        if (val == null || val instanceof JsonNull) {
            return null;
        }
        return (JsonArray) val;
    }

    public boolean getBoolean(String key) {
        JsonValue val = get(key);
        return val != null && val.asBoolean();
    }

    public Number getNumber(String key) {
        return getNumber(key, 0);
    }

    public Number getNumber(String key, Number defaultValue) {
        JsonValue val = get(key);
        return val == null ? defaultValue : val.asNumber();
    }

    public JsonObject getObject(String key) {
        return (JsonObject) get(key);
    }

    public String getString(String key) {
        JsonValue val = get(key);
        return val == null || val.isEmpty() ? null : val.asString();
    }

    public String getString(Collection<String> keys) {
        JsonValue val = getFirst(keys);
        return val == null || val.isEmpty() ? null : val.asString();
    }

    public JsonObject getObject(Collection<String> keys) {
        return (JsonObject) getFirst(keys);
    }

    @Override
    public JsonType getType() {
        return JsonType.OBJECT;
    }

    public boolean hasKey(String key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public String[] keys() {
        return map.keySet().toArray(new String[map.size()]);
    }

    public void put(String key, JsonValue value) {
        if (value == null) {
            value = factory.createNull();
        }
        map.put(key, value);
    }

    public void put(String key, String value) {
        put(key, value == null ? factory.createNull() : factory.create(value));
    }

    public void put(String key, double value) {
        put(key, factory.create(value));
    }

    public void put(String key, boolean bool) {
        put(key, factory.create(bool));
    }

    public void remove(String key) {
        map.remove(key);
    }

    public void set(String key, JsonValue value) {
        put(key, value);
    }

    @Override
    public String toJson() {
        return JsonUtil.stringify(this, 2);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public void traverse(JsonVisitor visitor, JsonContext ctx) {
        if (visitor.visit(this, ctx)) {
            JsonObjectContext objCtx = new JsonObjectContext(this);
            for (String key : stringifyOrder(keys())) {

                JsonValue value = get(key);
                if (!value.isEmpty()) {

                    objCtx.setCurrentKey(key);
                    if (visitor.visitKey(objCtx.getCurrentKey(), objCtx)) {
                        visitor.accept(get(key), objCtx);
                        objCtx.setFirst(false);
                    }
                }
            }
        }
        visitor.endVisit(this, ctx);
    }
}
