/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.definition.custom.json;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.StringJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.StringJsonSerializer;

public class BatchSizeJsonTypeSerializer extends JsonbDeserializer<Object> implements JsonbSerializer<Object> {

    private final static String PARAMETER_NAME = "batchSize";

    @Override
    public Object deserialize(JsonValue value, DeserializationContext ctx) {
        switch (value.getValueType()) {
            case STRING:
                return new StringJsonDeserializer().deserialize(value, ctx);
            case NUMBER:
                return new BaseNumberJsonDeserializer.LongJsonDeserializer().deserialize(value, ctx);
            default:
                return null;
        }
    }

    @Override
    public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
        if (obj instanceof String) {
            new StringJsonSerializer().serialize((String) obj, PARAMETER_NAME, generator, ctx);
        } else if (obj instanceof Long) {
            new BaseNumberJsonSerializer.LongJsonSerializer().serialize((Long) obj, PARAMETER_NAME, generator, ctx);
        }
    }
}
