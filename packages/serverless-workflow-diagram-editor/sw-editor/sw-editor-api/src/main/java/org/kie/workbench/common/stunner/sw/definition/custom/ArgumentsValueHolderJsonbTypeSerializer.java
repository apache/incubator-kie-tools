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

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.StringJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.StringJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;

public class ArgumentsValueHolderJsonbTypeSerializer extends JsonbDeserializer<Object>  implements JsonbSerializer<Object>  {

    private static final StringJsonSerializer stringJsonSerializer = new StringJsonSerializer();
    private static final StringJsonDeserializer stringJsonDeserializer = new StringJsonDeserializer();

    private static final ValueHolderJsonbTypeSerializer valueHolderJsonbTypeSerializer = new ValueHolderJsonbTypeSerializer("arguments");
    private static final ValueHolderJsonbTypeDeserializer valueHolderJsonbTypeDeserializer = new ValueHolderJsonbTypeDeserializer();

    @Override
    public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
        if (obj instanceof String) {
            stringJsonSerializer.serialize((String) obj, "arguments", generator, ctx);
        } else {
            valueHolderJsonbTypeSerializer.serialize((ValueHolder) obj,  generator, ctx);
        }
    }

    @Override
    public Object deserialize(JsonValue value, DeserializationContext ctx) {
        if (value.getValueType() != JsonValue.ValueType.NULL) {
            if (value.getValueType() == JsonValue.ValueType.STRING) {
                return stringJsonDeserializer.deserialize(value, ctx);
            } else if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                return valueHolderJsonbTypeDeserializer.deserialize(value, ctx);
            }
        }
        return null;
    }
}
