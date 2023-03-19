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

/**
 * Server-side implementation of JsonArray.
 */
public class JsonArray implements JsonValue {

    private ArrayList<JsonValue> arrayValues = new ArrayList<JsonValue>();

    private JsonFactory factory;

    public JsonArray(JsonFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean isEmpty() {
        return arrayValues == null || arrayValues.isEmpty();
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public double asNumber() {
        switch (length()) {
            case 0:
                return 0;
            case 1:
                return get(0).asNumber();
            default:
                return Double.NaN;
        }
    }

    @Override
    public String asString() {
        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < length(); i++) {
            if (i > 0) {
                toReturn.append(", ");
            }
            toReturn.append(get(i).asString());
        }
        return toReturn.toString();
    }

    public JsonValue get(int index) {
        return arrayValues.get(index);
    }

    public JsonArray getArray(int index) {
        return (JsonArray) get(index);
    }

    public boolean getBoolean(int index) {
        return ((JsonBoolean) get(index)).getBoolean();
    }

    public double getNumber(int index) {
        return ((JsonNumber) get(index)).getNumber();
    }

    public JsonObject getObject(int index) {
        return (JsonObject) get(index);
    }

    public String getString(int index) {
        JsonValue val = get(index);
        return val == null ? null : val.asString();
    }

    @Override
    public JsonType getType() {
        return JsonType.ARRAY;
    }

    public int length() {
        return arrayValues.size();
    }

    public void remove(int index) {
        arrayValues.remove(index);
    }

    public void set(int index, JsonValue value) {
        if (value == null) {
            value = factory.createNull();
        }
        if (index == arrayValues.size()) {
            arrayValues.add(index, value);
        } else {
            arrayValues.set(index, value);
        }
    }

    public void set(int index, String string) {
        set(index, factory.create(string));
    }

    public void set(int index, double number) {
        set(index, factory.create(number));
    }

    public void set(int index, boolean bool) {
        set(index, factory.create(bool));
    }

    public String toJson() {
        return JsonUtil.stringify(this);
    }

    @Override
    public void traverse(JsonVisitor visitor, JsonContext ctx) {
        if (visitor.visit(this, ctx)) {
            JsonArrayContext arrayCtx = new JsonArrayContext(this);
            for (int i = 0; i < length(); i++) {
                arrayCtx.setCurrentIndex(i);
                if (visitor.visitIndex(arrayCtx.getCurrentIndex(), arrayCtx)) {
                    visitor.accept(get(i), arrayCtx);
                    arrayCtx.setFirst(false);
                }
            }
        }
        visitor.endVisit(this, ctx);
    }
}
