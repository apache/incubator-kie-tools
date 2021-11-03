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

public class JsonBoolean implements JsonValue {

    private boolean bool;

    public JsonBoolean(boolean bool) {
        this.bool = bool;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean asBoolean() {
        return getBoolean();
    }

    @Override
    public double asNumber() {
        return getBoolean() ? 1 : 0;
    }

    @Override
    public String asString() {
        return Boolean.toString(getBoolean());
    }

    public boolean getBoolean() {
        return bool;
    }

    public JsonType getType() {
        return JsonType.BOOLEAN;
    }

    @Override
    public void traverse(JsonVisitor visitor, JsonContext ctx) {
        visitor.visit(getBoolean(), ctx);
    }

    @Override
    public String toJson() throws IllegalStateException {
        return String.valueOf(bool);
    }
}
