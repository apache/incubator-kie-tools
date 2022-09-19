/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;

public class ValueHolderJsonbTypeDeserializer extends JsonbDeserializer<ValueHolder> {

    @Override
    public ValueHolder deserialize(JsonValue value, DeserializationContext ctx) {
        if (value.getValueType() != JsonValue.ValueType.NULL) {
            ValueHolder holder = new ValueHolder();
            for (String v : ((JsonObject) value).keySet()) {
                JsonValue jsonValue = ((JsonObject) value).get(v);
                if(jsonValue.getValueType() == JsonValue.ValueType.STRING) {
                    Js.asPropertyMap(holder).set(v, ((JsonObject) value).getString(v));
                } else if (jsonValue.getValueType() == JsonValue.ValueType.NUMBER) {
                    Js.asPropertyMap(holder).set(v, ((JsonObject) value).getJsonNumber(v).numberValue());
                } else {
                    // and so on
                    Js.asPropertyMap(holder).set(v, (((JsonObject) value).get(v)));
                }
            }
            return holder;
        }
        return null;
    }
}
