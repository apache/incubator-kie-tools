/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.stunner.sw.marshall.json;

import java.lang.reflect.Type;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import org.kie.j2cl.tools.json.mapper.internal.deserializer.StringJsonDeserializer;
import org.kie.j2cl.tools.json.mapper.internal.serializer.StringJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.StateExecTimeout;
import org.kie.workbench.common.stunner.sw.definition.StateExecTimeout_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.StateExecTimeout_JsonSerializerImpl;

public class StateExecTimeoutJsonSerializer implements JsonbDeserializer<Object>, JsonbSerializer<Object> {

    private static final StateExecTimeout_JsonSerializerImpl serializer =
            StateExecTimeout_JsonSerializerImpl.INSTANCE;

    private static final StateExecTimeout_JsonDeserializerImpl deserializer =
            StateExecTimeout_JsonDeserializerImpl.INSTANCE;
    private static final StringJsonSerializer stringJsonSerializer = new StringJsonSerializer();

    private static final StringJsonDeserializer stringJsonDeserializer = new StringJsonDeserializer();

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctx, Type type) {
        JsonValue value = parser.getValue();
        if(value != null) {
            if (value.getValueType() != JsonValue.ValueType.NULL) {
                if (value.getValueType() == JsonValue.ValueType.STRING) {
                    return stringJsonDeserializer.deserialize(value, ctx);
                } else if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                    return deserializer.deserialize(parser.getValue(), ctx);
                }
            }
        }
        return null;
    }

    @Override
    public void serialize(Object stateExecTimeout, JsonGenerator generator, SerializationContext serializationContext) {
        if (stateExecTimeout instanceof String) {
            stringJsonSerializer.serialize((String) stateExecTimeout, generator, serializationContext);
        } else if (stateExecTimeout instanceof StateExecTimeout) {
            JsonGenerator jsonGenerator = generator.writeStartObject();
            serializer.serialize((StateExecTimeout) stateExecTimeout, jsonGenerator, serializationContext);
            jsonGenerator.writeEnd();
        }
    }
}
