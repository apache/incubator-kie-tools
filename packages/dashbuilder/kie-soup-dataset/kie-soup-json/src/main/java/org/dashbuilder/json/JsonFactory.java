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

public class JsonFactory {

    public JsonString create(String string) {
        return new JsonString(string);
    }

    public JsonNumber create(double number) {
        return new JsonNumber(number);
    }

    public JsonBoolean create(boolean bool) {
        return new JsonBoolean(bool);
    }

    public JsonArray createArray() {
        return new JsonArray(this);
    }

    public JsonNull createNull() {
        return JsonNull.NULL_INSTANCE;
    }

    public JsonObject createObject() {
        return new JsonObject(this);
    }

    public <T extends JsonValue> T parse(String jsonString) throws JsonException {
        if (jsonString.startsWith("(") && jsonString.endsWith(")")) {
            // some clients send in (json) expecting an eval is required
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }
        return new JsonTokenizer(this, jsonString).nextValue();
    }
}
