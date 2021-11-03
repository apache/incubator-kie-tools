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
 * Server-side implementation of JsonObject.
 */
public class JsonNull implements JsonValue {

    public static final JsonNull NULL_INSTANCE = new JsonNull();

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public double asNumber() {
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public JsonType getType() {
        return JsonType.NULL;
    }

    @Override
    public void traverse(JsonVisitor visitor, JsonContext ctx) {
        visitor.visitNull(ctx);
    }

    @Override
    public String toJson() {
        return null;
    }
}
