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
import java.util.List;

import elemental2.core.JsNumber;
import elemental2.core.JsObject;
import elemental2.core.Reflect;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;


public class ValueHolderJsonbTypeSerializer implements JsonbDeserializer<ValueHolder>, JsonbSerializer<ValueHolder> {

    @Override
    public void serialize(ValueHolder obj, JsonGenerator generator, SerializationContext ctx) {
        if(obj == null) {
            return;
        }
        JsonGenerator jsonGenerator = generator.writeStartObject();
        writeObject(jsonGenerator, null, obj);
        jsonGenerator.writeEnd();
    }

    void writeObject(JsonGenerator generator, String objName, Object obj) {
        if(obj == null) {
            return;
        }
        List<Reflect.OwnKeysArrayUnionType> keys = Reflect.ownKeys(obj).asList();
        if (!keys.isEmpty()) {
            JsonGenerator objBuilder = objName == null ? generator : generator.writeStartObject(objName);
            for (Reflect.OwnKeysArrayUnionType k : keys) {
                String key = k.asString();
                Object jsonValue = Js.asPropertyMap(obj).get(key);
                if (jsonValue instanceof String) {
                    objBuilder.write(key, (String) jsonValue);
                } else if (jsonValue instanceof Integer) {
                    objBuilder.write(key, (Integer) jsonValue);
                } else if (jsonValue instanceof Double) {
                    objBuilder.write(key, (Double) jsonValue);
                } else if (jsonValue instanceof Float) {
                    objBuilder.write(key, (Float) jsonValue);
                } else if (jsonValue instanceof Long) {
                    objBuilder.write(key, (Long) jsonValue);
                } else if (jsonValue instanceof Boolean) {
                    objBuilder.write(key, (Boolean) jsonValue);
                } else if (jsonValue instanceof JsonArray) {
                    JsonGenerator arrayBuilder = objBuilder.writeStartArray(key);
                    JsonArray jsonArray = (JsonArray) jsonValue;
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonValue arrayValue = jsonArray.get(i);
                        if (arrayValue.getValueType() == JsonValue.ValueType.STRING) {
                            arrayBuilder.write(jsonArray.getJsonString(i).getString());
                        } else if (arrayValue.getValueType() == JsonValue.ValueType.NUMBER) {
                            JsonNumber jsonNumber = jsonArray.getJsonNumber(i);
                            if(JsNumber.isInteger(jsonNumber.doubleValue())) {
                                arrayBuilder.write(jsonNumber.intValue());
                            } else {
                                arrayBuilder.write(jsonNumber.doubleValue());
                            }
                        } else if (arrayValue.getValueType() == JsonValue.ValueType.FALSE) {
                            arrayBuilder.write(false);
                        } else if (arrayValue.getValueType() == JsonValue.ValueType.TRUE) {
                            arrayBuilder.write(true);
                        } else {
                            JsonGenerator innerObject = arrayBuilder.writeStartObject();
                            List<Reflect.OwnKeysArrayUnionType> innerObjectKeys = Reflect.ownKeys(arrayValue).asList();
                            for (Reflect.OwnKeysArrayUnionType innerObjectKey : innerObjectKeys) {
                                Object innerObjectJsonValue = Js.asPropertyMap(obj).get(innerObjectKey.asString());
                                writeObject(innerObject, innerObjectKey.asString(), innerObjectJsonValue);
                            }
                            innerObject.writeEnd();
                        }
                    }
                    arrayBuilder.writeEnd();
                } else if (jsonValue instanceof JsonObject ) {
                    writeObject(objBuilder, key, ((JsonObject) jsonValue).asJsonObject());
                } else if(jsonValue instanceof JsObject) {
                    writeObject(objBuilder, key, jsonValue);
                } else {
                    throw new UnsupportedOperationException("unknown type " + jsonValue.getClass().getCanonicalName());
                }
            }
            objBuilder.writeEnd();
        }
    }

    @Override
    public ValueHolder deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonValue value = parser.getValue();
        if(value != null) {
            if (value.getValueType() != JsonValue.ValueType.NULL) {
                ValueHolder holder = new ValueHolder();
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
}
