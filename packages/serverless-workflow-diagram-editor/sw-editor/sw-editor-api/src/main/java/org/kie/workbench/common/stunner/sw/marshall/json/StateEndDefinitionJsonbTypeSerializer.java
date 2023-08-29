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
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateEnd_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.StateEnd_JsonSerializerImpl;


public class StateEndDefinitionJsonbTypeSerializer implements JsonbDeserializer<Object>, JsonbSerializer<Object> {

    private static final StateEnd_JsonSerializerImpl serializer =
            StateEnd_JsonSerializerImpl.INSTANCE;

    private static final StateEnd_JsonDeserializerImpl deserializer =
            StateEnd_JsonDeserializerImpl.INSTANCE;

    @Override
    public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
        if (obj instanceof Boolean) {
            generator.write(((Boolean) obj));
        } else if (obj instanceof StateEnd) {
            JsonGenerator jsonGenerator = generator.writeStartObject();
            serializer.serialize((StateEnd) obj, jsonGenerator, ctx);
            jsonGenerator.writeEnd();
        }
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonValue value = parser.getValue();
        if(value != null) {
            if (value.getValueType() != JsonValue.ValueType.NULL) {
                if (value.getValueType() == JsonValue.ValueType.TRUE
                        || value.getValueType() == JsonValue.ValueType.FALSE) {
                    if (value.getValueType() == JsonValue.ValueType.TRUE) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                    return deserializer.deserialize(parser, ctx, rtType);
                }
            }
        }
        return null;
    }
}
