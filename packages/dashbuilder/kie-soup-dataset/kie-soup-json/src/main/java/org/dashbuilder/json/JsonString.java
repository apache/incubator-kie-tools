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

/**
 * Server-side implementation of JsonString.
 */
public class JsonString implements JsonValue {

    private String string;

    public JsonString(String string) {
        this.string = string;
    }

    @Override
    public boolean isEmpty() {
        return string == null;
    }

    @Override
    public boolean asBoolean() {
        return string != null && string.toLowerCase().equals("true");
    }

    @Override
    public double asNumber() {
        try {
            if (asString().isEmpty()) {
                return 0.0;
            } else {
                return Double.parseDouble(asString());
            }
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

    @Override
    public String asString() {
        return getString();
    }

    public String getString() {
        return string;
    }

    @Override
    public JsonType getType() {
        return JsonType.STRING;
    }

    @Override
    public void traverse(JsonVisitor visitor, JsonContext ctx) {
        visitor.visit(getString(), ctx);
    }

    @Override
    public String toJson() throws IllegalStateException {
        return JsonUtil.quote(getString());
    }
}
