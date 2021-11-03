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
 * Server-side implementation of JsonNumber.
 */
public class JsonNumber implements JsonValue {

    private double number;

    public JsonNumber(double number) {
        this.number = number;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean asBoolean() {
        return Double.isNaN(getNumber()) || Math.abs(getNumber()) == 0.0 ? false : true;
    }

    @Override
    public double asNumber() {
        return getNumber();
    }

    @Override
    public String asString() {
        return toJson();
    }

    public double getNumber() {
        return number;
    }

    @Override
    public JsonType getType() {
        return JsonType.NUMBER;
    }

    @Override
    public void traverse(JsonVisitor visitor, JsonContext ctx) {
        visitor.visit(getNumber(), ctx);
    }

    @Override
    public String toJson() {
        String toReturn = String.valueOf(number);
        if (toReturn.endsWith(".0")) {
            toReturn = toReturn.substring(0, toReturn.length() - 2);
        }
        return toReturn;
    }
}
