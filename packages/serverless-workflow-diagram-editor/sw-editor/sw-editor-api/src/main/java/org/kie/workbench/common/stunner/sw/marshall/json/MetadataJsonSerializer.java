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

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.sw.definition.Metadata;

public class MetadataJsonSerializer implements JsonbDeserializer<Metadata>, JsonbSerializer<Metadata> {

    private static final ValueHolderJsonbTypeSerializer valueHolderJsonbTypeSerializer = new ValueHolderJsonbTypeSerializer();


    @Override
    public Metadata deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonValue value = parser.getValue();
        if(value != null) {
            if (value.getValueType() != JsonValue.ValueType.NULL) {
                Metadata holder = new Metadata();
                for (String v : ((JsonObject) value).keySet()) {
                    JsonValue jsonValue = ((JsonObject) value).get(v);
                    if (jsonValue.getValueType() == JsonValue.ValueType.STRING) {
                        Js.asPropertyMap(holder).set(v, ((JsonObject) value).getString(v));
                    } else if (jsonValue.getValueType() == JsonValue.ValueType.NUMBER) {
                        Js.asPropertyMap(holder).set(v, ((JsonObject) value).getJsonNumber(v).numberValue());
                    } else if (jsonValue.getValueType() == JsonValue.ValueType.ARRAY) {
                        Js.asPropertyMap(holder).set(v, jsonValue.asJsonArray());
                    } else if (jsonValue.getValueType() == JsonValue.ValueType.TRUE) {
                        Js.asPropertyMap(holder).set(v, true);
                    } else if (jsonValue.getValueType() == JsonValue.ValueType.FALSE) {
                        Js.asPropertyMap(holder).set(v, false);
                    } else {
                        Js.asPropertyMap(holder).set(v, (((JsonObject) value).get(v)));
                    }
                }
                return holder;
            }
        }
        return null;
    }

    @Override
    public void serialize(Metadata metadata, JsonGenerator generator, SerializationContext ctx) {
        if(metadata == null) {
            return;
        }
        JsonGenerator jsonGenerator = generator.writeStartObject();
        valueHolderJsonbTypeSerializer.writeObject(jsonGenerator, null, metadata);
        jsonGenerator.writeEnd();
    }
}
